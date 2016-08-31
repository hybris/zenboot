##### START tomcat

# Copyright (c) 2012-2016 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# Contributors:
# Codenvy, S.A. - initial API and implementation

FROM debian:jessie
EXPOSE 8080
RUN apt-get update && \
    apt-get -y install locales sudo procps wget unzip && \
    echo "%sudo ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers && \
    useradd -u 1000 -G users,sudo -d /home/user --shell /bin/bash -m user \
    && apt-get -y autoremove \
    && apt-get -y clean \
    && rm -rf /var/lib/apt/lists/*

USER user

LABEL che:server:8080:ref=tomcat8 che:server:8080:protocol=http

ENV JAVA_VERSION=8u101 \
    JAVA_VERSION_PREFIX=1.8.0_101 \
    TOMCAT_HOME=/home/user/tomcat \
    TOMCAT8_VERSION=8.0.33

ENV JAVA_HOME=/opt/jdk$JAVA_VERSION_PREFIX

ENV PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH

RUN mkdir $TOMCAT_HOME && \
  wget \
  --no-cookies \
  --no-check-certificate \
  --header "Cookie: oraclelicense=accept-securebackup-cookie" \
  -qO- \
  "http://download.oracle.com/otn-pub/java/jdk/$JAVA_VERSION-b13/jdk-$JAVA_VERSION-linux-x64.tar.gz" | sudo tar -zx -C /opt/

ENV TERM xterm

RUN wget -qO- "http://archive.apache.org/dist/tomcat/tomcat-8/v$TOMCAT8_VERSION/bin/apache-tomcat-$TOMCAT8_VERSION.tar.gz" | tar -zx --strip-components=1 -C $TOMCAT_HOME && \
    rm -rf $TOMCAT_HOME/webapps/*

ENV LANG C.UTF-8
RUN echo "export JAVA_HOME=/opt/jdk$JAVA_VERSION_PREFIX\nexport M2_HOME=/home/user/apache-maven-$MAVEN_VERSION\nexport TOMCAT_HOME=$TOMCAT_HOME\nexport PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH" >> /home/user/.bashrc && \
    sudo localedef -i en_US -f UTF-8 en_US.UTF-8

WORKDIR $TOMCAT_HOME

##### END tomcat

ADD zenboot.properties /etc/zenboot/zenboot.properties
USER root
RUN sudo apt-get update && sudo apt-get install -y curl ansible openssh-client sshpass socat dnsutils jq less vim netcat-openbsd git \
    && apt-get -y autoremove \
    && apt-get -y clean \
    && rm -rf /var/lib/apt/lists/*
ADD docker-provisioning/ansible.cfg /etc/ansible/ansible.cfg

USER user

ARG VERSION
RUN if [ -z "$VERSION" ]; \
        then echo "the build argument 'VERSION' is mandatory"; \
        exit 1; \
    fi
ARG ZENBOOT_WAR=https://github.com/hybris/zenboot/releases/download/v$VERSION/zenboot.war

RUN mkdir -p /home/user/zenboot
ADD $ZENBOOT_WAR $TOMCAT_HOME/webapps/zenboot.war
ADD docker-provisioning/setenv.sh $TOMCAT_HOME/bin/setenv.sh
RUN sudo chown user:user $TOMCAT_HOME/bin/setenv.sh
RUN sudo chown user:user $TOMCAT_HOME/webapps/zenboot.war

CMD bin/catalina.sh run 2>&1
