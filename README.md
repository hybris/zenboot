Zenboot - orchestrate your scripts
==================================
[![Build Status](https://travis-ci.org/hybris/zenboot.png?branch=master)](https://travis-ci.org/hybris/zenboot)

## In a nutshell ##
* A kind of slim RunDeck with better Environment-support
* executing exactly the same in each environment just with another configuration
* Running scripts in chains, reuse scripts and maintain the configuration in key-value pairs
* Use it e.g. to provision manually, bootstrap all your environments, storing
all the servers in the DB
* or fullfill your orchestration-needs, expose scripts via REST
* based on grails 2.5

## Go with Docker

``` bash
./docker-run.sh
# login at localhost:8080 with admin/zenboot
mkdir -p zenboot-scripts/mytest/scripts
mkdir -p zenboot-scripts/mytest/plugins
# create your scripts and have fun
# modify zenboot.properties.Docker for e.g. DB-connection
```

## Installation on a debian based system in a nutshell ##

``` bash
apt-get update
apt-get install unzip git wget openjdk-8-jdk
export JAVA_HOME=/usr/lib/jvm/default-java
git clone https://github.com/hybris/zenboot.git
cd zenboot
./grailsw run-app
```

Make sure that default-java points to the newly installed openjdk-8-jdk installation. Otherwise grails will fail.

## Use and understand the example-type
The example-type should show you the abilities of booting machines and the functionality
of zenboot without doing the actual job. So nothing get really created, eventually.
* ./docker-run.sh
* login at http://localhost:8080/zenboot with admin/zenboot
* Create a example-execution-zone: Processsing -> ExecutionZone -> Create
* Check enabled, create
* Processing -> example -> create-domain -> DOMAIN = testdomain.com ->
* This job might have setup your DNS-server for that domain and as sideeffect it created
a DOMAIN key in the key-value-list (Processing -> example -> edit_execution_zone)
* Processing -> example -> create_chefserver -> CUSTOMER_EMAIL = yourmail@testdomain.com
* This will might have created a chefserver and it created a a DB-entry for that server
(Data_Management -> hosts -> with your mail as a customer of that machine)
* For each machine to boot, you have to type in CUSTOMER_EMAIL which might be inappropriate
for your usecase, so let's fix that by setting it as a kind of default
* Processing -> example -> edit_execution_zone -> click "+" -> CUSTOMER_EMAIL = yourmail@testdomain.com -> click "update"
* Spinup a jenkinsmaster: Processing -> example -> create_jenkinsmaster -> execute
* spinup a couple of slaves as well
* now let's configure an autopurge of machines, so that:
 * we never want to delete the chefserver
 * we want to keep the jenkins-master and 2 slaves
* Processing -> example -> edit_execution_zone -> click "+" -> DELETEHOSTJOB_HOST_FILTER = !( host.cname.startsWith('chefserver') )
* Add another key-value -> DELETEHOSTJOB_ROLES_MINIMUM = ["jks":2,"jkm":1]
* tick "enable_autodeletion" and "update"
* Tab "delete" ->  delete_host -> click "-" so that the key HOSTNAME disappears -> "expose"
* choose "25 * * * * ?" which will run that job every minute
* Accessible by -> ROLE_ADMIN
* set a REST-url like "delete-host" and "create"
* Your jenkins-slaves which have exceeded their lifetime (default is zenboot.host.instances.lifetime=60 (seconds))
will get deleted automatically
[WorkInProgress]

## Connect to LDP
zenboot supports LDAP since v0.12.1. This is for authentication only. Whenever
someone logs in who can be authenticated via LDAP with the given settings,
the user will be created on the fly and added to the User-Role.

To activate, do something like this:
``` bash
cp SecurityConfigExample.groovy SecurityConfig.groovy
vi SecurityConfig.groovy
```

## Zenboot-Scripts
Avoid using the object.execute() method to execute a command in groovy shell. Use executeCommand(Object command) instead of this.
The object.execute() method writes the output into stdout and stderr which could cause overlapped process output if multiple scripts are running at the same time.

e.g.
```
executeCommand('ls')
or
executeCommand(['ls', '-la'])
``` 


## DISCLAIMER ##
Don't use zenboot in production with Docker, because security!

## License ##
Copyright 2013 hybris GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
