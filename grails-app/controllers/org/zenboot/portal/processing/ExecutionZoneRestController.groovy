package org.zenboot.portal.processing

import grails.converters.JSON
import grails.converters.XML
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role

class ExecutionZoneRestController extends AbstractRestController {

    def springSecurityService
    def accessService
    def scriptDirectoryService
    def executionZoneService
    def applicationEventPublisher

    static allowedMethods = [help: "GET", list: ["GET","POST"], execute: "POST", listparams: "POST", listactions: "POST"]

    //The help method gives you an overview about the possible rest endpoints and which parameters could be set
    def help = {
        withFormat {
            xml {
                render(contentType: "text/xml") {
                    restendpoints {
                        restendpoint {
                            name 'execute'
                            description 'The method execute the specific action of an execution zone based on the parameters.'
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                            }
                            action {
                                description 'The name of the action.'
                                type 'String'
                                mandatory 'Yes'
                            }
                        }
                        restendpoint {
                            name 'list'
                            description 'The method returns the execution zones of the user.'
                            execType {
                                description 'The id or the name of the execution zone type. If not set the method returns all enabled execution zones of the user.'
                                type 'Long or String.'
                                mandatory 'No'
                            }
                        }
                        restendpoint {
                            name 'listparams'
                            description 'The method returns all required parameters on an specific execution zone action.'
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                            }
                            action {
                                description 'The name of the action.'
                                type 'String'
                                mandatory 'Yes'
                            }
                        }
                        restendpoint {
                            name 'listactions'
                            description 'The method return all action names of the specific execution zone.'
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
                def action = [description: 'The name of the action.', type: 'String', mandatory: 'Yes']
                def execType = [description: 'The id or the name of the execution zone type. If not set the method returns all enabled execution zones of the user.', type: 'Long or String.',
                                mandatory: 'No']

                def executeEndPoint = [description: 'The method execute the specific action of an execution zone based on the parameters.', execId: execId, action: action]
                def listEndPoint = [description: 'The method returns the execution zones of the user.', execType: execType]
                def listparamsEndPoint = [description: 'The method returns all required parameters on an specific execution zone action.', execId: execId, action: action]
                def listactionsEndPoint = [description: 'The method return all action names of the specific execution zone.', execId: execId]

                render (contentType: "text/json") { restendpoints execute: executeEndPoint, list: listEndPoint, listparams: listparamsEndPoint, listactions: listactionsEndPoint }
            }
        }
    }

    def execute = {

        ExecutionZone executionZone
        String actionName

        if (ExecutionZone.get(params.execId)) {
            executionZone = ExecutionZone.get(params.execId)
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone id (execId) not set.')
            return
        }

        if (params.action) {
            actionName = params.action
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'Action name (action) not set.')
            return
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) || userHasAccess(executionZone)) {

            File stackDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" + actionName)

            ExecutionZoneAction action = executionZoneService.createExecutionZoneAction(executionZone, stackDir)
            applicationEventPublisher.publishEvent(new ProcessingEvent(action, springSecurityService.currentUser, "REST-call run"))

            this.renderRestResult(HttpStatus.OK, executionZone)
        }
        else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'This user has no permission to execute this execution Zone.')
        }
    }

    //Return a list of enabled execution zones to which the user has access
    // The list is be more specified if an execType param is set
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

    // the method returns a list of all required parameters of an execution zone
    def listparams = {

        ExecutionZone executionZone
        String actionName

        if (ExecutionZone.get(params.execId)) {
            executionZone = ExecutionZone.findById(params.execId)
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone id (execId) not set.')
            return
        }

        if (params.action) {
            actionName = params.action
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'Action name (action) not set.')
            return
        }

        if (userHasAccess(executionZone)) {

            File stackDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" + actionName)

            def paramsSet = executionZoneService.getExecutionZoneParameters(executionZone, stackDir)

            withFormat {
                xml {
                    render (contentType: "text/xml") {
                        parameters {
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
                    def parameters = [:]
                    parameters.put('parameters', paramsSet.collect {['parameterName': it.name, 'parameterValue': it.value]} )

                    render (contentType: "text/json") { parameters } as JSON
                }
            }
        }
        else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'This user has no permission to request the parameter for this zone.')
        }
    }

    // this method creates a list of all possible actions for the executionzone
    def listactions = {

        ExecutionZone executionZone
        File scriptDir

        if (ExecutionZone.get(params.id)) {
            executionZone = ExecutionZone.get(params.id)
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'executionZone id not set.')
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
        File[] scriptDirFiles = scriptDir.listFiles()

        withFormat {
            xml {
                render (contentType: "text/xml") {
                    actions {
                        scriptDirFiles.each {
                            action it.name
                        }
                    }
                }
            }
            json {
                def dirContent = [:]
                dirContent.put('actions', scriptDirFiles.collect {it.name})

                render (contentType: "text/json") { dirContent as JSON }
            }
        }
    }

    private Boolean userHasAccess(ExecutionZone executionZone) {
        return accessService.accessCache[springSecurityService.getCurrentUserId()] != null ?
                accessService.accessCache[springSecurityService.getCurrentUserId()][executionZone.id] :
                accessService.userHasAccess(executionZone)
    }

}