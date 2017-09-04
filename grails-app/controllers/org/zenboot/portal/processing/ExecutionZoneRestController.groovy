package org.zenboot.portal.processing

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONException
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role

class ExecutionZoneRestController extends AbstractRestController implements ApplicationEventPublisherAware{

    def springSecurityService
    def accessService
    def scriptDirectoryService
    def executionZoneService
    def grailsLinkGenerator
    def applicationEventPublisher

    static allowedMethods = [index: 'GET' , help: "GET", list: "GET", execute: "POST", listparams: "GET", listactions: "GET"]

    @Override
    void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.applicationEventPublisher = eventPublisher
    }

    /**
     * The method gives you an overview about the possible rest endpoints and which parameters could be set.
     */
    def help = {
        withFormat {
            xml {
                render(contentType: "text/xml") {
                    restendpoints {
                        restendpoint {
                            name 'execute'
                            description 'The method execute the specific action of an execution zone based on the parameters.'
                            url '/rest/v1/executionzones/{execId}/actions/{execAction}/execute'
                            exampleurl '/rest/v1/executionzones/1/actions/internal/execute'
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                            }
                            execAction {
                                description 'The name of the action.'
                                type 'String'
                                mandatory 'Yes'
                            }
                            parameters 'Requires json or xml where all the necessary parameters are stored. You can save the result of /listparams to get a working template.'
                        }
                        restendpoint {
                            name 'list'
                            description 'The method returns the execution zones of the user.'
                            urls {
                                all '/rest/v1/executionzones/list'
                                specific '/rest/v1/executionzones/list?execType={execType}'
                                exampleurl '/rest/v1/executionzones/list?execType=internal'
                            }
                            execType {
                                description 'The id or the name of the execution zone type. If not set the method returns all enabled execution zones of the user.'
                                type 'Long (id) or String (name).'
                            }
                        }
                        restendpoint {
                            name 'listparams'
                            description 'The method returns all required parameters on an specific execution zone action.'
                            url '/rest/v1/executionzones/{execId}/actions/{execAction}/listparams'
                            exampleurl '/rest/v1/executionzones/1/actions/sanitycheck/listparams'
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                            }
                            execAction {
                                description 'The name of the action.'
                                type 'String'
                                mandatory 'Yes'
                            }
                        }
                        restendpoint {
                            name 'listactions'
                            description 'The method return all action names of the specific execution zone.'
                            url '/rest/v1/executionzones/$execId/listactions'
                            exampleurl '/rest/v1/executionzones/1/listactions'
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                                }
                            }
                        }
                }
            }
            json {

                def execId = [description: 'The id of the specific execution zone.', type: 'Long', mandatory: 'Yes']
                def execAction = [description: 'The name of the action.', type: 'String', mandatory: 'Yes']
                def execType = [description: 'The id or the name of the execution zone type. If not set the method returns all enabled execution zones of the user.', type: 'Long or String.',
                                mandatory: 'No']

                def executeEndPoint = [description: 'The method execute the specific action of an execution zone based on the parameters.',
                                       parameters: 'Requires json or xml where all the necessary parameters are stored. You can save the result of /listparams to get a working template.']
                def listEndPoint = [description: 'The method returns the execution zones of the user.', execType: execType]
                def listparamsEndPoint = [description: 'The method returns all required parameters on an specific execution zone action.', execId: execId, action: execAction]
                def listactionsEndPoint = [description: 'The method return all action names of the specific execution zone.', execId: execId]

                render (contentType: "text/json") { restendpoints execute: executeEndPoint, list: listEndPoint, listparams: listparamsEndPoint, listactions: listactionsEndPoint }
            }
        }
    }

    /**
     * Execute a specific action (scriptstack) of a specific execution zone.
     */
    def execute = {

        ExecutionZone executionZone
        String actionName
        Map parameters =[:]
        Boolean hasError = false

        if (params.execId) {
            if(ExecutionZone.findById(params.execId)){
                executionZone = ExecutionZone.findById(params.execId)
            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone with id ${params.execId} not found.')
                return
            }
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone id (execId) not set.')
            return
        }

        if (params.execAction) {
            actionName = params.execAction
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'Action name (execAction) not set.')
            return
        }

        // get data from incomming json or xml
        request.withFormat {
            xml {

                def xml
                try {
                    xml = request.XML
                }
                catch (ConverterException e) {
                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The XML could not be parsed.')
                    hasError = true
                    return
                }

                def xmlparameter = xml[0].children.findAll {it.name == 'parameter'}

                xmlparameter.each{ node ->
                    def name = ''
                    def value = ''

                    node.children.each{
                        if (it.name == 'parameterName') {
                            name = it.text()
                        }
                        else if (it.name == 'parameterValue') {
                            value = it.text()
                        }
                    }

                    parameters[name] = value
                }
            }
            json {

                String text = request.getReader().text

                def json

                try {
                    json = new JSONObject(text)
                }
                catch (JSONException e) {
                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, e.getMessage())
                    hasError = true
                    return
                }

                if(json.parameters) {
                    json.parameters.each {
                        parameters[it.parameterName] = it.parameterValue
                    }
                }
            }
        }

        if (hasError) {
            return
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) || userHasAccess(executionZone)) {

            if (parameters.any { key, value -> value == '' || value == null}) {
                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'No empty parameter values allowed - please check your data.')
                return
            }

            File stackDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" + actionName)

            if(!isValidScriptDir(stackDir)) {
                return
            }

            if(!SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
                // check if it allowed to change the parameters
                def origin_params = executionZoneService.getExecutionZoneParameters(executionZone, stackDir)

                origin_params.each {
                    ProcessingParameter org_parameter = new ProcessingParameter(name: it.name, value: it.value.toString())
                    ProcessingParameter new_parameter

                    if (parameters[it.name]) {
                        new_parameter = new ProcessingParameter(name: it.name, value: parameters[it.name])
                    }
                    else {
                        if (it.value.toString()) {
                            new_parameter = new ProcessingParameter(name: it.name, value: it.value.toString())
                        }
                        else {
                            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'No empty parameter values allowed - please check your data. Empty parameter: ' + it.name)
                            return
                        }

                    }

                    if (org_parameter.value != new_parameter.value && !executionZoneService.actionParameterEditAllowed(new_parameter, org_parameter)) {
                        //not allowed to change this param so change back
                        parameters[org_parameter.name] = org_parameter.value
                    }
                }
            }

            // create action with zone, stackdir and parameters
            ExecutionZoneAction action = executionZoneService.createExecutionZoneAction(executionZone, stackDir, parameters)
            //publish event to start execution
            applicationEventPublisher.publishEvent(new ProcessingEvent(action, springSecurityService.currentUser, "REST-call run"))

            URI referral = new URI(grailsLinkGenerator.link(absolute:true, controller:'executionZoneAction', action:'rest', params:[id:action.id]))

            this.renderRestResult(HttpStatus.CREATED, null, referral, actionName + ' sucessfully created.')
        }
        else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'This user has no permission to execute this execution Zone.')
        }
    }

    /**
     * Returns a list of enabled execution zones to which the user has access.
     * The list is be more specified if an execType param is set.
     */
    def list = {

        def results
        ExecutionZoneType executionZoneType

        if (params.execType) {
            if (params.long('execType')) {
                executionZoneType = ExecutionZoneType.findById(params.execType)
            } else if (params.execType instanceof String) {
                executionZoneType = ExecutionZoneType.findByName(params.execType)
            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'The executionZoneType (execType) has to be a long or a string')
                return
            }
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {

            if (executionZoneType) {
                results = ExecutionZone.findAllByTypeAndEnabled(executionZoneType, true)
            }
            else {
                results = ExecutionZone.findAllByEnabled(true)
            }
        }
        else {

            List<ExecutionZone> executionZones = new ArrayList<ExecutionZone>()

            def executionZonesIDs

            if (accessService.accessCache[springSecurityService.getCurrentUserId()]) {
                executionZonesIDs = accessService.accessCache[springSecurityService.getCurrentUserId()].findAll {it.value}
            }
            else {
                accessService.refreshAccessCacheByUser(Person.findById(springSecurityService.getCurrentUserId()))
                executionZonesIDs = accessService.accessCache[springSecurityService.getCurrentUserId()].findAll {it.value}
            }

            executionZonesIDs.each {
                executionZones.add(ExecutionZone.get(it.key))
            }

            if (executionZoneType) {
                results = new ArrayList<ExecutionZone>()

                executionZones.each {zone ->
                    if (zone.type == executionZoneType && zone.enabled) {
                        results.add(zone)
                    }
                }
            }
            else if (executionZoneType == null && params.execType) {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'The requested execution zone type does not exist.')
                return
            }
            else {
                results = executionZones.findAll() {it.enabled}
            }
        }

        def executionZones = results.collect {[execId: it.id, execType: it.type.name, execDescription: it.description]}

        withFormat {
            xml {
                render(contentType: "text/xml") {
                    executionzones {
                        executionZones.each { execZone ->
                            executionzone {
                                execId execZone.execId
                                execType execZone.execType
                                execDescription execZone.execDescription
                            }
                        }
                    }
                }
            }
            json {
                def zones = [:]
                zones.put('executionZones', executionZones)

                render(contentType: "text/json") { zones } as JSON
            }
        }
    }

    /**
     * The method returns a list of all required parameters of an execution zone.
     */
    def listparams = {

        ExecutionZone executionZone
        String actionName

        if (params.execId) {
            if(ExecutionZone.findById(params.execId)){
                executionZone = ExecutionZone.findById(params.execId)
            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone with id ${params.execId} not found.')
                return
            }
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone id (execId) not set.')
            return
        }

        if (params.execAction) {
            actionName = params.execAction
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'Action name (execAction) not set.')
            return
        }

        if ( SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) || userHasAccess(executionZone)) {

            File stackDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" + actionName)

            if(!isValidScriptDir(stackDir)) {
                return
            }

            def paramsSet = executionZoneService.getExecutionZoneParameters(executionZone, stackDir)

            withFormat {
                xml {
                    render (contentType: "text/xml") {
                        parameters {
                            execId executionZone.id
                            execAction actionName
                            paramsSet.each { param ->
                                parameter {
                                    parameterName param.name
                                    parameterValue param.value
                                }
                            }
                        }
                    }
                }
                json {
                    def responseParams = [:]
                    responseParams.put('execId', executionZone.id)
                    responseParams.put('execAction', actionName)
                    responseParams.put('parameters', paramsSet.collect {['parameterName': it.name, 'parameterValue': it.value]} )

                    render (contentType: "text/json") { responseParams } as JSON
                }
            }
        }
        else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'This user has no permission to request the parameter for this zone.')
        }
    }

    /**
     * This method returns a list of all possible actions for the executionzone.
     */
    def listactions = {

        ExecutionZone executionZone
        File scriptDir

        if (params.execId) {
            if(ExecutionZone.findById(params.execId)){
                executionZone = ExecutionZone.findById(params.execId)
            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone with id ${params.execId} not found.')
                return
            }
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone id (execId) not set.')
            return
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            scriptDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" )
        }
        else if (userHasAccess(executionZone)) {

            scriptDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" )
        }
        else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'This user has no permission to request the actions for this zone.')
            return
        }

        if(!isValidScriptDir(scriptDir)) {
            return
        }

        File[] scriptDirFiles = scriptDir.listFiles()

        withFormat {
            xml {
                render (contentType: "text/xml") {
                    execActions {
                        scriptDirFiles.each {
                            execAction it.name
                        }
                    }
                }
            }
            json {
                def dirContent = [:]
                dirContent.put('execActions', scriptDirFiles.collect {it.name})

                render (contentType: "text/json") { dirContent } as JSON
            }
        }
    }

    /**
     * Check if the user is already in the cache and has access to the requested execution zone.
     * @param executionZone - the execution zone which has to be checked for access.
     * @return true if the user has access otherwise false.
     */
    private Boolean userHasAccess(ExecutionZone executionZone) {
        return accessService.accessCache[springSecurityService.getCurrentUserId()] != null ?
                accessService.accessCache[springSecurityService.getCurrentUserId()][executionZone.id] :
                accessService.userHasAccess(executionZone)
    }

    /**
     * Check if the script file exists. If not it renders NOT_FOUND with the error message that the script file does not exists.
      * @param scriptDir the script File object.
     * @return true if exists otherwise false.
     */
    private Boolean isValidScriptDir(File scriptDir) {
        if (scriptDir.exists()) {
            return Boolean.TRUE
        }
        else {
            renderRestResult(HttpStatus.NOT_FOUND, null, null, 'The script with path ${scriptDir.getPath()} does not exists.')
        }
        return Boolean.FALSE
    }
}