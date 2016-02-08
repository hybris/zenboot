#!/bin/bash

ASK=yes

while [ $# -gt 0 ]; do
  case "$1" in
    -v)
      VERSION=$2
      shift 2 ;;
    -n)
      ask=no
      echo "won't ask anything"
      shift 1 ;;
  esac
done

[ -z "${VERSION:+x}" ]    && echo "# Error: VERSION not present or empty" && exit 2
# version is not allowed to start with a letter
echo $VERSION | egrep -v -q "^[0-9]" && echo "# VERSION needs to start with a digit. The "v" will added inside the script" && exit 2

# Check whether travis finished the build, something like:
while [ ! "`curl https://api.travis-ci.org/repos/hybris/zenboot/builds | jq -r .[0].state`XX" == "finishedXX" ]; do
  echo "# build is not finished ... waiting ..."
  sleep 20
done

date
echo "# Will now release $1"
# ------------------verifying versions -----------------
sed -i -e "s/app.version=.*/app.version=$VERSION/" application.properties
sed -i -e "s/download\/v[.0-9]*/download\/v$VERSION/" Dockerfile

git add application.properties
git add Dockerfile
git commit -m "Release v${VERSION}"
git push
git tag v${VERSION}
git push origin v${VERSION}
# enforce to type in sudo-password
echo "waiting for at last 5 mins ..."
sleep 300
while [ ! "`curl https://api.travis-ci.org/repos/hybris/zenboot/builds | jq -r .[0].state`XX" == "finishedXX" ]; do
  echo "build is till ot finished ... waiting ..."
  sleep 60
done
docker build -t hybris/zenboot:v${VERSION} .
echo -n "about to tag the Dockerimage"
docker tag hybris/zenboot:v${VERSION} hybris/zenboot:latest
docker push hybris/zenboot:latest
docker push hybris/zenboot:v${VERSION}
date
