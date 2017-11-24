package org.zenboot.portal.processing

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.security.Role

class ExecutionZoneActionRestController extends AbstractRestController  {

    static allowedMethods = [listdetailedactions: "GET"]

    def springSecurityService
    def accessService

    /**
     * The method returns a detailed list of execution zone actions. It is possible to specify the execution zone or a list of execution zones delimited by ',' (?execId=1,2,3,5...).
     * If you don't specify the execId the method tries to return all execution zone actions as once. If the number of results is bigger than 100, the pagination mechanism will be forced
     * so the method returns a list of prepared urls.
     */
    def listdetailedactions = {
        List<ExecutionZone> execZones = []
        int offset
        if (params.execId) {
            if (params.execId.contains(',')) {
                List<String> execZoneIds = params.execId.split(',')
                execZoneIds.each {
                    if(it.isInteger()) {
                        ExecutionZone zone = ExecutionZone.get(it as Long)
                        if (zone) {
                            if (accessService.userHasAccess(zone)) {
                                execZones.add(zone)
                            } else {
                                this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'You have no permissions to get the actions ' +
                                        'of the executionZone with id ' + params.execId + '.')
                                return
                            }
                        } else {
                            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No execution zone with id ' + it + ' found.')
                            return
                        }
                    }
                    else {
                        this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The execId param ' + it +' invalid. ' +
                                'It has to be a Long value.')
                        return
                    }
                }
            } else if (params.execId.isInteger()) {
                ExecutionZone zone = ExecutionZone.get(params.execId as Long)
                if (zone) {
                    if (accessService.userHasAccess(zone)){
                        execZones.add(zone)
                    }
                    else {
                        this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'You have no permissions to change the parameters ' +
                                'of the executionZone with id ' + params.execId + '.')
                        return
                    }
                } else {
                    this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No execution zone with id ' + params.execId + ' found.')
                    return
                }
            } else {
                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The execId param invalid. ' +
                        'It has to be a Long value or a list of Long values delimited by ","')
                return
            }
        } else {
            if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
                execZones = ExecutionZone.getAll()
            } else {
                def execZoneIds = accessService.accessCache[springSecurityService.getCurrentUserId() as Long]?
                        accessService.accessCache[springSecurityService.getCurrentUserId() as Long].findAll {it.value == true}.collect {it.key}
                        : []

                execZoneIds.each {
                    execZones.add(ExecutionZone.get(it as Long))
                }
            }
        }

        if (params.offset && params.offset.isInteger()) {
            offset = params.offset as Integer
        }
        else {
            offset = 0
        }

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                int totalSize = execZones.sum {it.actions.size()} as Integer

                if (100 < totalSize && offset == 0) {
                    String actionsXML = builder.bind {
                        pagination {
                            execZones.each { ExecutionZone execZone ->
                                int start = 0
                                def zoneSize = execZone.actions.size()
                                def pages = Math.round((zoneSize / 100) + 0.5) //round to next full page

                                pages.times {
                                    url "/zenboot/rest/v1/listactions?execId=" + execZone.id + "&offset=" + start

                                    if ((start + 100) < zoneSize) {
                                        start += 100
                                    }
                                }
                            }
                        }
                    }

                    def xml = XmlUtil.serialize(actionsXML).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                    xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                    render contentType: "text/xml", xml
                }
                else {
                    String actionsXML = builder.bind {
                        executionZoneActions {
                            execZones.each { execZone ->
                                def list = execZone.actions as List
                                if (list.size() > (offset+100)) {
                                    list = list.subList(offset, offset + 100)
                                } else {
                                    list = list.subList(offset, (list.size()-1))
                                }
                                list.each { ExecutionZoneAction action ->
                                    executionZoneAction {
                                        execId execZone.id
                                        execActionId action.id
                                        execActionName action.toString()
                                        scriptDir action.scriptDir.path
                                        creationDate action.creationDate.toString()
                                        processingParameters {
                                            action.processingParameters.each { ProcessingParameter pp ->
                                                processingParameterName pp.name
                                                processingParameterValue pp.value
                                            }
                                        }
                                        scriptletBatchIds action.scriptletBatches.collect { it.id }
                                    }
                                }
                            }
                        }
                    }
                    def xml = XmlUtil.serialize(actionsXML).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                    xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                    render contentType: "text/xml", xml
                }
            }
            json {
                int totalSize = execZones.sum { it.actions.size() } as Integer

                if (100 < totalSize && offset == 0) {
                    def paginationJSON = []
                    execZones.each { ExecutionZone execZone ->
                        int start = 0
                        def zoneSize = execZone.actions.size()
                        def pages = Math.round((zoneSize / 100) + 0.5) //round to next full page

                        pages.times {
                            paginationJSON.add("/zenboot/rest/v1/listactions?execId=" + execZone.id + "&offset=" + start)

                            if ((start + 100) < zoneSize) {
                                start += 100
                            }
                        }
                    }
                    render paginationJSON as JSON
                } else {

                    def actionsJSON = [:]
                    def actionsList = []
                    execZones.each { ExecutionZone execZone ->
                        def list = execZone.actions as List
                        if (list.size() > (offset+100)) {
                            list = list.subList(offset, offset + 100)
                        } else {
                            list = list.subList(offset, (list.size()-1))
                        }
                        list.each { ExecutionZoneAction action ->
                            def actionJSON = [:]
                            actionJSON.put('execId', execZone.id)
                            actionJSON.put('execActionId', action.id)
                            actionJSON.put('execActionName', action.toString())
                            actionJSON.put('scriptDir', action.scriptDir.path)
                            actionJSON.put('creationDate', action.creationDate.toString())
                            def processingParameters = [:]
                            action.processingParameters.each { ProcessingParameter pp ->
                                processingParameters.put(pp.name, pp.value)
                            }
                            actionJSON.put('processingParameters', processingParameters)
                            actionJSON.put('scriptletBatchIds', action.scriptletBatches.collect { it.id })
                            actionsList.add(actionJSON)
                        }
                    }
                    actionsJSON.put('executionZoneActions', actionsList)
                    render actionsJSON as JSON
                }
            }
        }
    }
}
