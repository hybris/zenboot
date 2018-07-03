FROM golang:1.9-stretch

ENV GRAILS_VERSION 2.5.5
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV GOPATH /infrabox/.go

RUN apt-get update -qq \
    && apt-get install -y unzip openjdk-8-jdk \
    && apt-get -y autoremove \
    && apt-get -y clean \
    && rm -rf /var/lib/apt/lists/*

# Install Grails
WORKDIR /usr/lib/jvm
RUN wget https://github.com/grails/grails-core/releases/download/v$GRAILS_VERSION/grails-$GRAILS_VERSION.zip && \
    unzip grails-$GRAILS_VERSION.zip && \
    rm -rf grails-$GRAILS_VERSION.zip && \
    ln -s grails-$GRAILS_VERSION grails

COPY . /zenboot/
WORKDIR /zenboot

RUN grails compile
