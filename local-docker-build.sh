#!/bin/bash

set -e

./grailsw -plain-output -non-interactive compile
./grailsw -plain-output -non-interactive war

docker build . -t hybris/zenboot --build-arg VERSION=ignored --build-arg ZENBOOT_WAR=target/zenboot.war --build-arg ZENBOOT_CLI=zenboot-cli/bin/zenboot-linux-amd64
