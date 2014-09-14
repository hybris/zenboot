#!/bin/bash

echo "Will now release $1"
git add application.properties
git add Dockerfile
git commit -m "Release $1"
echo -n "about to push ... [enter]" && read
git push
git tag $1
echo -n "about to push tag ... [enter]" && read
git push origin $1
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
sudo docker build -t k9ert/zenboot .
echo -n "about to tag the Dockerimage"
sudo docker tag k9ert/zenboot:latest $1
sudo docker push k9ert/zenboot:lastest
sudo docker push k9ert/zenboot:${1}

