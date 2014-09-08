#/bin/bash

CWD=`pwd`
dockerimage="k9ert/zenboot"

echo -n "type i[enter] for interactive (need to run catalina.sh start manually) [ ] "
read interactive

if [ $interactive == "i" ] ; then
sudo docker run -t -i -p 8080:8080 -v ${CWD}:/home/user/zenboot $dockerimage /bin/bash
else
sudo docker run -t -p 8080:8080 -v ${CWD}:/home/user/zenboot $dockerimage
fi 
