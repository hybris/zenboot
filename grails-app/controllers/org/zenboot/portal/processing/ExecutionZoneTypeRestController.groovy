package org.zenboot.portal.processing

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.security.Role

class ExecutionZoneTypeRestController extends AbstractRestController {

    static allowedMethods = [listexectypes: 'GET', editexectype: 'PUT']

    def springSecurityService
    def accessService

    /**
     * execTypes returns a list of all existing executionZoneTypes.
     */
    def listexectypes = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            withFormat {
                xml {
                    def builder = new StreamingMarkupBuilder()
                    builder.encoding = 'UTF-8'

                    String executionZoneTypes = builder.bind {
                        executionZoneTypes {
                            ExecutionZoneType.list().sort().each {
                                executionZoneType it
                            }
                        }
                    }

                    def xml = XmlUtil.serialize(executionZoneTypes).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                    xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                    render contentType: "text/xml", xml
                }
                json {
                    render(contentType: "text/json") { ExecutionZoneType.list().sort() } as JSON
                }
            }
        } else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'Only admins are allowed to request these resources.')
        }
    }

    /**
     * The method changes the property values of an existing executionzonetype.
     */
    def editexectype = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            ExecutionZoneType execType
            Boolean hasError = Boolean.FALSE

            if (params.execTypeId && params.execTypeId.isInteger()) {
                execType = ExecutionZoneType.get(params.execTypeId as Long)

                if (execType) {
                    // do nothing
                } else {
                    this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No ExecutionZoneType with id ' + params.execTypeId + ' found.')
                    return
                }

            } else {
                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'ExecutionZoneType id (execTypeId) not set or wrong format.')
                return
            }

            request.withFormat {
                xml {
                    def xml = parseRequestDataToXML(request)
                    if (xml) {
                        def xmlParameters = xml[0].children
                        def parameters = [:]

                        xmlParameters.each { node ->
                            def name = ''
                            def value = ''
                            node.children.each { innerNode ->
                                if (innerNode.name == 'parameterName') {
                                    name = innerNode.text()
                                } else if (innerNode.name == 'parameterValue') {
                                    value = innerNode.text()
                                }
                            }
                            parameters.put(name, value)
                        }

                        parameters.each {
                            if (execType.hasProperty(it.key)) {
                                execType.properties[it.key] = it.value
                            } else {
                                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Property ' + it.key + ' not exists for UserNotifications.')
                                hasError = Boolean.TRUE
                                return
                            }
                        }
                    } else { hasError = Boolean.TRUE}
                }
                json {
                    def json = parseRequestDataToJSON(request)
                    if (json) {
                        if (json.parameters) {
                            json.parameters.each {
                                if (it.parameterName && it.parameterValue) {
                                    if (execType.hasProperty(it.parameterName)) {
                                        execType.properties[it.parameterName] = it.parameterValue
                                    } else {
                                        this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Property ' + it.parameterName + ' not exists for ExecutionZoneType.')
                                        hasError = Boolean.TRUE
                                        return
                                    }
                                } else {
                                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'paramterName or paramterValue is null or empty. Please check your data.')
                                    hasError = Boolean.TRUE
                                    return
                                }
                            }
                        }
                    } else { hasError = Boolean.TRUE}
                }
            }

            if (hasError) {
                return
            }

            if (execType.save(flush: true)) {
                this.renderRestResult(HttpStatus.OK, null, null, 'ExecutionZoneType changed.')
            } else {
                this.renderRestResult(HttpStatus.INTERNAL_SERVER_ERROR, null, null, 'An error occurred while saving the ExecutionZoneType.')
            }
        } else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'Only admins are allowed to request these resources.')
        }
    }
}
