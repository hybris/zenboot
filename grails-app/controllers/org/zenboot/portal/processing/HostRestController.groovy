package org.zenboot.portal.processing

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.Host
import org.zenboot.portal.HostState
import org.zenboot.portal.security.Role

class HostRestController extends AbstractRestController {

    static allowedMethods = [listhosts: "GET", listhoststates: "GET"]

    def springSecurityService
    def accessService

    /**
     * The method returns the hosts. The result could be more specific if 'hostState' parameter is added to the request url e.g. ?hostState=completed to return all
     * hosts with the state completed. It is also possible to add multiple states. In this case call the url with ?hostState=completed,created . Furthermore it is possible
     * to restrict the search for a single execution zone. In this case add e.g. ?execId=104 to the url. You also can use both e.g. ?execId=104&hostState=completed,created .
     */
    def listhosts = {
        ExecutionZone executionZone

        if (params.execId && params.execId.isInteger()) {
            if (ExecutionZone.findById(params.execId as Long)) {
                executionZone = ExecutionZone.findById(params.execId as Long)
            } else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone with id ' + params.execId + ' not found.')
                return
            }
        }

        List<Long> usersExecutionZones = []
        if (!SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            Map execZoneMap = accessService.accessCache[springSecurityService.getCurrentUserId()]
            usersExecutionZones = execZoneMap.findAll{it.value == true}.collect{it.key}
            if (usersExecutionZones.isEmpty()) {
                this.renderRestResult(HttpStatus.UNAUTHORIZED, null, null, 'You do not have any access to any executionzone.')
            }
        }

        def hostsFromZone = []

        if (params.hostState) {
            def hostStates = []

            if (params.hostState.contains(',')) {
                hostStates = params.hostState.split(',')
            } else {
                hostStates.add(params.hostState as String)
            }

            hostStates.each {
                String state = it as String
                state = state.toUpperCase()
                if (HostState.values().find { it.toString() == state }) {
                    HostState hostState = HostState.valueOf(state)
                    if (executionZone) {
                        if (accessService.userHasAccess(executionZone)) {
                            hostsFromZone.addAll(Host.findAllByExecZoneAndState(executionZone, hostState))
                        }
                        else {
                            this.renderRestResult(HttpStatus.UNAUTHORIZED, null, null, 'You have no permissions to list the hosts of this execution zone.')
                        }
                    } else {
                        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
                            hostsFromZone.addAll(Host.findAllByState(hostState))
                        }
                        else {
                            usersExecutionZones.each {
                                hostsFromZone.addAll(Host.findAllByStateAndExecZone(hostState, ExecutionZone.findById(it)))
                            }
                        }
                    }
                } else {
                    this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No hoststate found for state: ' + params.hostState)
                    return
                }
            }
        } else {
            if (executionZone) {
                if (accessService.userHasAccess(executionZone)) {
                    hostsFromZone = Host.findAllByExecZone(executionZone)
                }
                else {
                    this.renderRestResult(HttpStatus.UNAUTHORIZED, null, null, 'You have no permissions to list the hosts of this execution zone.')
                }
            } else {
                if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
                    hostsFromZone = Host.list()
                }
                else {
                    usersExecutionZones.each {
                        hostsFromZone.addAll(Host.findAllByExecZone(ExecutionZone.findById(it)))
                    }
                }
            }
        }

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String foundHosts = builder.bind {
                    hosts {
                        hostsFromZone.each { hostElement ->
                            host {
                                hostname hostElement.hostname.toString()
                                cname hostElement.cname
                                hoststate hostElement.state.toString()
                                ipadress hostElement.ipAddress
                                serviceUrls {
                                    hostElement.serviceUrls.each { singleurl ->
                                        serviceUrl singleurl.url
                                    }
                                }
                            }
                        }
                    }
                }

                def xml = XmlUtil.serialize(foundHosts).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                Map hosts = [:]
                List host = hostsFromZone.collect {
                    [hostname: it.hostname.toString(), cname: it.cname, hoststate: it.state.toString(), ipadress: it.ipAddress, serviceUrls: [it.serviceUrls.collect {
                        it.url
                    }]]
                }
                hosts.put('hosts', host)
                render hosts as JSON
            }
        }
    }

    /**
     * The method returns a list of all existing states of a host.
     */
    def listhoststates = {
        def hostStates = HostState.findAll().collect{it.toString()}

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String states = builder.bind {
                    hoststates {
                        hostStates.each {
                            hoststate it
                        }
                    }
                }

                def xml = XmlUtil.serialize(states).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                Map jsonhoststates = [:]
                jsonhoststates.put('hoststates', hostStates)
                render jsonhoststates as JSON
            }
        }
    }
}
