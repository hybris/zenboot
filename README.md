Zenboot - a way to provision and orchestrate your scripts
=========================================================

## In a nutshell ##
* A kind of slim RunDeck with better Environment-support
* executing exactly the same in each environment just with another configuration
* Running scripts in chains, reuse scripts and maintain the configuration in key-value pairs
* Use it e.g. to provision manually, bootstrap your complete environment (puppet apply example coming soon)
* or fullfill your orchestration-needs, expose scripts via REST
* grails based 2.0.3

## Installation on a debian based system in a nutshell ##

`apt-get update`

`apt-get install unzip mercurial wget openjdk-7-jdk`

`echo "export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/" >> /etc/profile`

`. /etc/profile`

`#wget --http-user=user --http-passwd=heheThatsASecret  http://somedomain.com/artifactory/infrastructure-local/devop-adminportal/devop-adminportal-source/tip/devop-adminportal-source-tip.zip`

`hg clone ssh://hg@bitbucket.org/hybris/devop-adminportal`

`cd devop-adminportal`

`cp zenboot.properties.example zenboot.properties`

`./grailsw run-app`

## License ##
Copyright 2013 hybris GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

