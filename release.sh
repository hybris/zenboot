#!/bin/bash

set -e

while [ $# -gt 0 ]; do
  case "$1" in
    -v)
      VERSION=$2
      shift 2 ;;
  esac
done

[ -z "${VERSION:+x}" ]    && echo "# Error: VERSION not present or empty" && exit 2

VERSION=$(echo $VERSION | sed 's/^v//')
# version is not allowed to start with a letter
echo $VERSION | egrep -v -q "^[0-9]" && echo "# VERSION needs to start with a digit. The "v" will added inside the script" && exit 2

if ! docker ps > /dev/null 2>&1; then
   echo "failed to connect to the docker daemon, is docker(-machine) running and are you allowed to connect?"
   exit 1
fi

if [[ $(git name-rev --name-only HEAD) != master ]]; then
    echo refusing to release from a branch that is not master
    exit 1
fi

echo "# make sure jq supports the --exit-status option"
echo "{}" | jq . --exit-status || exit 1

wait_for_travis() {
    echo -n "waiting for travis CI to finish"
    while curl -s 'https://api.travis-ci.org/repos/hybris/zenboot/builds' |\
        jq --exit-status -r '.[0].state != "finished"' > /dev/null; do
      echo -n "."
      sleep 20
    done
    echo " done"
}

git fetch
upstream=$(git for-each-ref --format='%(upstream:short)' refs/heads/master)
if ! git diff --quiet --exit-code $upstream; then
    echo "you are not on the latest commit of you upstream $upstream"
    echo "please update your working copy first"
    exit 1
fi

wait_for_travis

date
echo "# Will now release $1"
sed -i'.bak' -e "s/app.version=.*/app.version=$VERSION/" application.properties

git add application.properties
git commit -m "Release v${VERSION}"
git push
git tag v${VERSION}
git push origin v${VERSION}

wait_for_travis

echo "waiting for the artifact to appear on github"
SUCCESS=false
for i in $(seq 1 60); do
    if curl -X HEAD -sf "https://github.com/hybris/zenboot/releases/download/v${VERSION}/zenboot.war" -o /dev/null
    then
        SUCCESS=true
        break
    fi
    echo -n "."
    sleep 15
done
echo " done"

if [[ $SUCCESS = false ]]; then
    echo "failed to get https://github.com/hybris/zenboot/releases/download/v${VERSION}/zenboot.war"
    exit 1
fi

docker build -t hybris/zenboot:v${VERSION} --build-arg VERSION=$VERSION .
echo "tagging the image"
docker tag hybris/zenboot:v${VERSION} hybris/zenboot:latest
echo "pushing"
docker push hybris/zenboot:latest
docker push hybris/zenboot:v${VERSION}
date
