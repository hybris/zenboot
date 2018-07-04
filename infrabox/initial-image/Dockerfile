FROM golang:1.9-stretch

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV GOPATH /infrabox/.go

RUN apt-get update -qq \
    && apt-get install -y openjdk-8-jdk \
    && apt-get -y autoremove \
    && apt-get -y clean \
    && rm -rf /var/lib/apt/lists/*

COPY . /zenboot/
WORKDIR /zenboot

RUN ./grailsw compile
