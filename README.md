Zenboot - orchestrate your scripts
==================================
[![Build Status](https://travis-ci.org/hybris/zenboot.png?branch=master)](https://travis-ci.org/hybris/zenboot)

## In a nutshell ##
* A kind of slim RunDeck with better Environment-support
* executing exactly the same in each environment just with another configuration
* Running scripts in chains, reuse scripts and maintain the configuration in key-value pairs
* Use it e.g. to provision manually, bootstrap your complete environment (puppet apply example coming soon)
* or fullfill your orchestration-needs, expose scripts via REST
* grails based 2.0.3

## Installation on a debian based system in a nutshell ##

``` bash
apt-get update
apt-get install unzip git wget openjdk-7-jdk
export JAVA_HOME=/usr/lib/jvm/default-java
git clone https://github.com/hybris/zenboot.git
cd zenboot
./grailsw run-app
```

## Usage in a nutshell
* login at http://localhost:8080/zenboot with admin/zenboot
* Create a example-execution-zone: Processsing -> ExecutionZone -> Create
* Check enabled, create
* choose "helloworld" scriptdir, execute!
* Click the appeared Process on the left-hand-side
* Click on the callout in the Process-Output-column to see the output of the script
* Go again to the execution-zone, choose helloworld again, modify the value of "WHAT_TO_SAY", execute
* Examine the Output again
* Go again to the execution-zone, "edit" and add a key-value WHAT_TO_SAY -> "zenboot is cool!", update
* choose helloworld again, consider how the value has now been changed, execute


## License ##
Copyright 2013 hybris GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

