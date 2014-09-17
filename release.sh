#!/bin/bash

# Check whether travis finished the build, something like:
[ `curl https://api.travis-ci.org/repos/hybris/zenboot/builds | jq -r '.[0].state'` == "finished" ] || exit 2

# ToDo updating the number through the Grails Application Version Update Plugin 
# http://grails.org/plugin/version-update

# ./grailsw version-update

date
echo "Will now release $1"
git add application.properties
git add Dockerfile
git commit -m "Release $1"
echo -n "about to push ... [enter]" && read
git push
git tag $1
echo -n "about to push tag ... [enter]" && read
git push origin $1
# enforce to type in sudo-password
sudo ls -l > /dev/null
echo -n "after push, we'll sleep for 5 mins and afterwards build  the Dockerimage ...[enter]" && read
sleep 60
echo -n "another 4 minutes ..."
sleep 60
echo -n "another 3 minutes ..."
sleep 60
echo -n "another 2 minutes ..."
sleep 60
echo -n "another minute ..."
sleep 60
sudo docker build -t k9ert/zenboot:${1} .
echo -n "about to tag the Dockerimage"
sudo docker tag k9ert/zenboot:${1} k9ert/zenboot:latest
sudo docker push k9ert/zenboot:latest
sudo docker push k9ert/zenboot:${1}
date

