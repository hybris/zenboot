#!/bin/bash

set -e
FORCE=false

while [ $# -gt 0 ]; do
  case "$1" in
    -v)
      VERSION=$2
      shift 2 ;;
    -f)
      FORCE=true
      shift 1 ;;
  esac
done

if !docker ps  > /dev/null 2>&1; then
   echo "failed to connect to the docker daemon, is docker(-machine) running and are you allowed to connect?"
   exit 1
fi

if [[ $(git name-rev --name-only HEAD) != master ]]; then
    echo refusing to release from a branch that is not master
    exit 1
fi

upstream=$(git for-each-ref --format='%(upstream:short)' refs/heads/master)
if [[ $FORCE != true ]] && ! git diff --quiet --exit-code $upstream; then
    echo "you are not on the latest commit of you upstream $upstream"
    echo "use -f to force the release on your current HEAD"
    exit 1
fi

[ -z "${VERSION:+x}" ]    && echo "# Error: VERSION not present or empty" && exit 2
# version is not allowed to start with a letter
echo $VERSION | egrep -v -q "^[0-9]" && echo "# VERSION needs to start with a digit. The "v" will added inside the script" && exit 2

# Check whether travis finished the build, something like:
while curl -s 'https://api.travis-ci.org/repos/hybris/zenboot/builds' |\
    jq --exit-status -r '.[0].state != "finished"'; do
  echo "# last travis build is not finished ... waiting ..."
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

echo "waiting for at last 5 mins ..."
sleep 300
while curl -s 'https://api.travis-ci.org/repos/hybris/zenboot/builds' |\
    jq --exit-status -r '.[0].state != "finished"'; do
  echo "build is still not finished ... waiting ..."
  sleep 60
done
docker build -t hybris/zenboot:v${VERSION} .
echo -n "about to tag the Dockerimage"
docker tag -f hybris/zenboot:v${VERSION} hybris/zenboot:latest
docker push hybris/zenboot:latest
docker push hybris/zenboot:v${VERSION}
date
