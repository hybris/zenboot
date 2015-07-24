#/bin/bash

CWD=`pwd`
dockerimage="hybris/zenboot"

[ -f zenboot.Docker.properties ] || cp zenboot.Docker.properties.template zenboot.Docker.properties

echo -n "type i[enter] for interactive (need to run catalina.sh start manually) [ ] "
read interactive

if [ $interactive == "i" ] ; then
sudo docker run -t -i -p 8080:8080 -p 8090:8090 -v ${CWD}:/home/user/zenboot $dockerimage /bin/bash
else
sudo docker run -t -p 8080:8080 -p 8090:8090 -v ${CWD}:/home/user/zenboot $dockerimage
fi
