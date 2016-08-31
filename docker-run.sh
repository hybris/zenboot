#/bin/bash

CWD=`pwd`
dockerimage="hybris/zenboot"

[ -f zenboot.Docker.properties ] || cp zenboot.Docker.properties.template zenboot.Docker.properties

if [[ -z $interactive ]]; then
    echo -n "type i[enter] for interactive (need to run catalina.sh start manually) [ ] "
    read interactive
fi

if [ $interactive == "i" ] ; then
    docker run -t -i -p 8080:8080 -v ${CWD}:/home/user/zenboot $dockerimage /bin/bash
else
    docker run -t -p 8080:8080 -v ${CWD}:/home/user/zenboot $dockerimage
fi
