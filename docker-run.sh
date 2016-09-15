#/bin/bash

CWD=`pwd`
dockerimage="hybris/zenboot"
PARAMETERS="-p 8080:8080 -v ${CWD}:/home/user/zenboot $dockerimage"

[ -f zenboot.Docker.properties ] || cp zenboot.Docker.properties.template zenboot.Docker.properties

if [[ -z $interactive ]]; then
    echo -n "type i[enter] for interactive (need to run catalina.sh start manually) [n] "
    read interactive
fi

if [[ -z $interactive && $interactive == "i" ]] ; then
    docker run -t -i ${PARAMETERS} /bin/bash
else
    docker run -t -d ${PARAMETERS}
fi
