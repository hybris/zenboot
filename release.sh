#!/bin/bash

# Check whether travis finished the build, something like:
while [ ! "`curl https://api.travis-ci.org/repos/hybris/zenboot/builds | jq -r .[0].state`XX" == "finishedXX" ]; do
  echo "build is not finished ... waiting ..."
  sleep 20
done

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
echo "waiting for at last 5 mins ..."
sleep 300
while [ ! "`curl https://api.travis-ci.org/repos/hybris/zenboot/builds | jq -r .[0].state`XX" == "finishedXX" ]; do
  echo "build is till ot finished ... waiting ..."
  sleep 60
done
sudo docker build -t k9ert/zenboot:${1} .
echo -n "about to tag the Dockerimage"
sudo docker tag k9ert/zenboot:${1} k9ert/zenboot:latest
sudo docker push k9ert/zenboot:latest
sudo docker push k9ert/zenboot:${1}
date

