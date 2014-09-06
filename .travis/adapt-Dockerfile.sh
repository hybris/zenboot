#!/bin/bash

echo "# adapting Dockerfile"
sed -i 's/ADD http.*/ADD https:\/\/github.com\/hybris\/zenboot\/releases\/download\/${TRAVIS_TAG}\/zenboot.war \/home\/user\/tomcat7\/webapps\/zenboot.war/' ../Dockerfile
echo "# done, looks like this:"
cat ./Dockerfile

