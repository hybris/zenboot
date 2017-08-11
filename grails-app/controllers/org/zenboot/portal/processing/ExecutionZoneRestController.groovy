package org.zenboot.portal.processing

import grails.plugin.springsecurity.SpringSecurityUtils
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role

class ExecutionZoneRestController {

    def springSecurityService
    def accessService

    static allowedMethods = [list: ["GET","POST"], save: "POST", update: "POST", delete: "POST"]

    def rest = {

    }

    //The help method gives you an overview about the possible rest endpoints and which parameters could be set
    def help = {

    }


    //Return a list of enabled execution zones to which the user has access
    // The list is be more specified if an execType param is set
    def list = {

        def results
        ExecutionZoneType executionZoneType

        if (params.execType) {
            if (params.long('execType')) {
                execType = ExecutionZoneType.findById(params.execType)
            } else if (params.execType instanceof String) {
                execType = ExecutionZoneType.findByName(params.execType)
            }
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {

            if (executionZoneType) {
                results = ExecutionZone.findAllByTypeAndEnabled(execType, true)
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
                results = executionZones.findAll {it.executionZoneType == executionZoneType && it.enabled}
            }
            else {
                results = executionZones.findAll() {it.enabled}
            }
        }

        request.withFormat {
            xml {
                render (contentType:"text/xml") {
                    executionzones {
                        results.each { result ->
                            execId result.id
                            execType result.type.name
                            execDescription result.description
                        }
                    }
                }
            }
            json {
                render (contentType:"text/json"){

                    def executionZones = [ executionZones:array {
                        results.each { result ->
                            zone(execId: result.id, execType: result.type.name, execDescription: result.description)
                        }
                    }]

                    executionzones executionZones
                }
            }
        }
    }
}
