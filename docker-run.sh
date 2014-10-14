#/bin/bash

dockerimage="k9ert/zenboot"

while [ $# -gt 0 ]; do
  case "$1" in
    -i)
      dockerimage=$2
      shift 2 ;;
  esac
done

CWD=`pwd`

echo -n "type i[enter] for interactive (need to run catalina.sh start manually) [ ] "
read interactive

#docker run --privileged=true -e DEV_UID=$UID -e DEV_GID=$GID -i -t -v ${HOME}/devel:/devel willb/java-dev:centos7-spark-uid

if [ $interactive == "i" ] ; then
sudo docker run -e DEV_UID=$UID -e DEV_GID=$GID -t -i -p 8080:8080 -v ${CWD}:/home/user/zenboot $dockerimage /bin/bash
else
sudo docker run -e DEV_UID=$UID -e DEV_GID=$GID -t -p 8080:8080 -v ${CWD}:/home/user/zenboot $dockerimage
fi
