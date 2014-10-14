#!/bin/sh

# thanks to http://chapeau.freevariable.com/2014/08/docker-uid.html

export ORIGPASSWD=$(cat /etc/passwd | grep user)
export ORIG_UID=$(echo $ORIGPASSWD | cut -f3 -d:)
export ORIG_GID=$(echo $ORIGPASSWD | cut -f4 -d:)

export DEV_UID=${DEV_UID:=$ORIG_UID}
export DEV_GID=${DEV_GID:=$ORIG_GID}

ORIG_HOME=$(echo $ORIGPASSWD | cut -f6 -d:)

sudo sed -i -e "s/:$ORIG_UID:$ORIG_GID:/:$DEV_UID:$DEV_GID:/" /etc/passwd
sudo sed -i -e "s/user:x:$ORIG_GID:/user:x:$DEV_GID:/" /etc/group

# Why would we need that?!
# chown -R ${DEV_UID}:${DEV_GID} ${ORIG_HOME}

cd /home/user/tomcat7/bin && ./catalina.sh run
