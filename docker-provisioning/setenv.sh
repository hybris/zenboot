#!/bin/bash
hostname=`cat /home/user/zenboot/zenboot.Docker.properties | grep -v '#' | grep java.rmi.server.hostname | cut -d'=' -f2 | sed ':a;N;$!ba;s/\n.*//g'`

export CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8090 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.rmi.port=8090 "

if [ "AA$hostname" != "AA" ]; then
  export CATALINA_OPTS="$CATALINA_OPTS -Djava.rmi.server.hostname=${hostname}"
fi
