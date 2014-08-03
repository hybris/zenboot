#/bin/bash

CWD=`pwd`
sudo docker run -t -p 8080:8080 -v ${CWD}:/home/user/zenboot k9ert/zenboot
