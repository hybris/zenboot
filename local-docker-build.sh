#!/bin/bash

set -e

./grailsw -plain-output -non-interactive compile
./grailsw -plain-output -non-interactive war

mkdir zenboot-cli/bin
go get -d ./...
GOOS=linux GOARCH=amd64 go build -o zenboot-cli/bin/zenboot-linux-amd64 zenboot-cli/zenboot.go
GOOS=linux GOARCH=386 go build -o zenboot-cli/bin/zenboot-linux-386 zenboot-cli/zenboot.go
GOOS=darwin GOARCH=amd64 go build -o zenboot-cli/bin/zenboot-darwin-amd64 zenboot-cli/zenboot.go
GOOS=darwin GOARCH=386 go build -o zenboot-cli/bin/zenboot-darwin-386 zenboot-cli/zenboot.go

docker build . -t hybris/zenboot --build-arg VERSION=ignored --build-arg ZENBOOT_WAR=target/zenboot.war --build-arg ZENBOOT_CLI=zenboot-cli/bin/zenboot-linux-amd64
