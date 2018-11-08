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

## Connect to LDAP
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

## REST ##

### GENERAL ###
The REST API itself accept only data as XML or JSON.

### HEADERS ###

For a successful request some headers are required.

Send data:
"Content-Type: text/xml" or "Content-Type: application/xml" if you send your data via XML.
"Content-Type: text/json" or "Content-Type: application/json" if you send your data via JSON.

Receive data:
"Accept: text/xml" or "Accept: application/xml" if you want the response in XML.
"Accept: text/json" or "Accept: application/json" if you want the response in JSON.

Authorization:
Requires username:password as base64 like "Authorization: Basic bWljaGk6bWljaGk="

Request method:

GET - To get information about an object
POST - To create a new object or execute
PUT - To change an existing object
DELETE - To delete an existing object

Example:
curl -sL --request POST --data @sanitycheck.xml -H "Authorization: Basic bWljaGk6bWljaGk=" -H "Content-Type: text/xml" -H "Accept: application/json" localhost:8080/zenboot/rest/v1/executionzones/1/actions/sanitycheck/1/execute/
The curl command above executes the sanitycheck. It send the data via xml from a xml file and get the result as json.

### REST ENDPOINTS ###

####/rest/v1/help

The help shows you an overview of all possible REST endpoints, a description and which parameters (and their name) are required to run the endpoint.
Request method: GET


####/rest/v1/executionzones/$execId/actions/$execAction/$quantity/execute

The execute endpoint starts an execution in Zenboot. It requires XML or JSON in its data object, which contains all the required data for the execution zone action. To get a template with all required parameters and predefined values of these parameters, it is possible to save the result of the listparams endpoint into a file, modify, and send the content of the file back to this endpoint. Keep in mind that you only can change parameters if you have the permissions to do that.
Request method: POST

####/rest/v1/executionzones/list

This endpoint gives you an overview of all execution zones (which are enabled and the user has access to).
Request method: GET

####/rest/v1/executionzones/$execId/actions/$execAction/params/list

The endpoint returns all required parameters of an execution zone action as XML or JSON. If values in Zenboot are predefined, it also fills the values of these parameters.
Request method: GET

####/rest/v1/executionzones/$execId/actions/list
This endpoint return all actions of a specific execution zone.
Request method: GET

####/rest/v1/executionzones/execzonetemplate
The execzonetemplate endpoint returns a template in JSON or XML which can be used to create a new execution zone.
Request method: GET; Restriction: ADMIN only

####/rest/v1/executionzones/create
The create endpoint creates a new execution zone based on the data in the JSON or XML object, which must be sent to the endpoint. After the execution was created, the executionzone details will be returned as JSON or XML.
Request method: POST;
Restriction: ADMIN only

####/rest/v1/executionzones/{execId}/clone
The clone endpoint clones an existing Execution Zone. After cloning has finished, it returns the data of the cloned Execution Zone as JSON or XML.
Request method: POST; 
Restriction: ADMIN only

####/rest/v1/executionzones/{execId}/params/list
This endpoint returns a list with processing parameters of an specific execution zone.
Request method: GET

####/rest/v1/executionzones/{execId}/params/edit
This endpoint changes the processing parameters of an existing execution zone. It is possible to change / add processing parameters due add a new key / value parameter pair in the data or change the value of an existing one. To change / add processing parameters you have to use
request method PUT. If you want to remove processing parameters you have to use the request method DELETE. Keep in mind that all key / value pairs in your data will be removed.
Request method: PUT, DELETE

####/rest/v1/executionzones/{execId}/attribute/list
This endpoint returns a list with attributes of an execution zone.
Request method: GET

####/rest/v1/executionzones/{execId}/attribute/edit
This endpoint changes the execution zone attributes. It return INTERNAL SERVER ERROR if the execution zone could not be saved e.g. because of wrong datatype. Some of the values are already catched so that all correct values will be changed. In case of changing the description the access cache will be updated for this zone to ensure that users which roles does not match the new expression will no longer have access.
Request method: PUT

####/rest/v1/executionzones/${execId}/actions/$scriptletBatchName/details
This endpoint returns a list of detailed information about the scriptletbatch and releated scriptlet metadata details.
Request method: GET

####/rest/v1/actions/list?execId=
The endpoint returns a detailed list of execution zone actions. It is possible to specify the execution zone or a list of execution zones delimited by ',' (?execId=1,2,3,5...). If you don't specify the execId the method tries to return all execution zone actions as once. If the number of results is bigger than 100, the pagination mechanism will be forced so the method returns a list of prepared urls.
Request method: GET

####/rest/v1/serviceurls/list?
The endpoint returns a list with active hosts service urls. It is possible to specify the execution zone or a list of execution zones delimited by ',' (?execId=1,2,3,5...). If the execId param is not set, the method returns the service urls of all execution zones.
Request method: GET

####/rest/v1/hoststates/list
This endpoint returns a list of all existing host states.
Request method: GET

####/rest/v1/hosts/list
This endpoint returns a list of all hosts of the execution zones where the user has access.
Request method: GET

####/rest/v1/hosts/list?hostState={hostState}&execId={execId}
To make the result more specific you are able to add the hostStates and/or execId, so your result is filtered.
Request method: GET

####/rest/v1/hosts/{hostId}/edit?
The endpoint changes the properties of of an existing host for given data as XML or JSON. To set ?markUnknown=true or ?markBroken=true and the end of the url will mark the host into one of these states. Changing the rest of the properties requires admin permissions.
Request method: PUT

####/rest/v1/customers/list?
The endpoint returns a list with customers. It is possible to specify the customer by identifers (email, customerId) a list of emails delimited by ',' (?identifier=my.email.com,my.email2.com,1,...). If no identifier is set, the method returns a list of all customers.
Request method: GET

####/rest/v1/customers/{identifier}/edit
The endpoint changes the properties of an existing customer identified by id or email.
Request method: PUT

####/rest/v1/usernotifications/list
The endpoint returns a list of user notifications. It is possible to specify the enabled param to get all enabled or disabled user notifications. If the enabled parameter is not set, the method return all available user notifications.
Request method: GET

####/rest/v1/usernotifications/{notificationId}/edit
The endpoint overrides the values of an existing user notification. Admin permissions are required to edit an user notification.
Request method: PUT

####/rest/v1/usernotifications/create
The endpoint creates a new user notification. Admin permissions are required to create a new one.
Request method: POST

####/rest/v1/usernotifications/{notificationId}/delete
The endpoint deletes an user notification by id. Admin permissions are required to delete an user notification.
Request method: DELETE

####/rest/v1/exectypes/list
The endpoint returns a list of all existing executionZoneTypes.
Request method: GET

####/rest/v1/exectypes/{execTypeId}/edit
The endpoint changes the property values of an existing executionzonetype.
Request method: PUT

## DISCLAIMER ##
Don't use zenboot in production with Docker, because security!

## License ##
Copyright 2018 hybris GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
