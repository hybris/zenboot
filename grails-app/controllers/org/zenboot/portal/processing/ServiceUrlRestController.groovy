package org.zenboot.portal.processing

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.ServiceUrl
import org.zenboot.portal.security.Role

class ServiceUrlRestController extends AbstractRestController  {

    static allowedMethods = [listserviceurls: "GET"]

    def springSecurityService
    def accessService

    /**
     * The method returns a list with active hosts service urls. It is possible to specify the execution zone or a list of execution zones delimited by ',' (?execId=1,2,3,5...). If the execId param is
     * not set, the method returns the service urls of all execution zones.
     */
    def listserviceurls = {
        def execZones = []
        if (params.execId) {
            List<String> execZoneIds = params.execId.split(',')
            execZoneIds.each {
                if (it.isInteger()) {
                    ExecutionZone zone = ExecutionZone.get(it as Long)
                    if (zone) {
                        if (accessService.userHasAccess(zone)) {
                            execZones.add(zone)
                        } else {
                            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'You have no permissions to change the parameters ' +
                                    'of the executionZone with id ' + params.execId + '.')
                            return
                        }
                    } else {
                        this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No execution zone with id ' + it + ' found.')
                        return
                    }
                } else {
                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The execId param ' + it + ' invalid. ' +
                            'It has to be a Long value.')
                    return
                }
            }
        } else {
            if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
                execZones = ExecutionZone.getAll()
            } else {
                def execZoneIds = accessService.accessCache[springSecurityService.getCurrentUserId() as Long] ?
                        accessService.accessCache[springSecurityService.getCurrentUserId() as Long].findAll {
                            it.value == true
                        }.collect { it.key }
                        : []

                execZoneIds.each {
                    execZones.add(ExecutionZone.get(it as Long))
                }
            }
        }

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String serviceURLsXML = builder.bind {
                    serviceURLs {
                        execZones.each { ExecutionZone zone ->
                            zone.getActiveServiceUrls().each { ServiceUrl activeServiceUrl ->
                                serviceURL {
                                    id activeServiceUrl.id
                                    url activeServiceUrl.url
                                    serviceowner {
                                        ipaddress activeServiceUrl.owner.ipAddress
                                        cname activeServiceUrl.owner.cname
                                        creationDate activeServiceUrl.owner.creationDate
                                        expiryDate activeServiceUrl.owner.expiryDate
                                        state activeServiceUrl.owner.state.name()
                                        host {
                                            id activeServiceUrl.owner.hostname.id
                                            name activeServiceUrl.owner.hostname.name
                                        }
                                    }
                                    execId zone.id
                                }
                            }
                        }
                    }
                }

                def xml = XmlUtil.serialize(serviceURLsXML).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                def serviceURLsJSON = []
                execZones.each { ExecutionZone zone ->
                    zone.getActiveServiceUrls().each { ServiceUrl activeServiceUrl ->
                        def serviceURLJSON = [:]
                        serviceURLJSON.put('id', activeServiceUrl.id)
                        serviceURLJSON.put('url', activeServiceUrl.url)
                        def serviceOwner = [:]
                        serviceOwner.put('ipaddress', activeServiceUrl.owner.ipAddress)
                        serviceOwner.put('cname', activeServiceUrl.owner.cname)
                        serviceOwner.put('creationDate', activeServiceUrl.owner.creationDate)
                        serviceOwner.put('expiryDate', activeServiceUrl.owner.expiryDate)
                        serviceOwner.put('state', activeServiceUrl.owner.state.name())
                        def host = [:]
                        host.put('id', activeServiceUrl.owner.hostname.id)
                        host.put('name', activeServiceUrl.owner.hostname.name)
                        serviceOwner.put('host', host)
                        serviceURLJSON.put('serviceOwner', serviceOwner)
                        serviceURLJSON.put('execId', zone.id)
                        serviceURLsJSON.add(serviceURLJSON)
                    }
                }
                render serviceURLsJSON as JSON
            }
        }
    }
}
