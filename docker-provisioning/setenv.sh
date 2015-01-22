#!/bin/bash
export JAVA_OPTS="$JAVA_OPTS -XX:MaxPermSize=512m"

hostname=`grep java.rmi.server.hostname /home/user/zenboot/zenboot.Docker.properties | cut -d'=' -f2`

export CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8090 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.rmi.port=8090 "

if [ "AA$hostname" != "AA" ]; then
  export CATALINA_OPTS="$CATALINA_OPTS -Djava.rmi.server.hostname=${hostname}"
fi
