package org.zenboot.portal.processing

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.Customer
import org.zenboot.portal.Host
import org.zenboot.portal.security.Role

class CustomerRestController extends AbstractRestController {

    static allowedMethods = [listcustomers: "GET", editcustomers: "PUT"]

    def springSecurityService

    /**
     * The method return a list with customers. It is possible to specify the customer by identifers (email, customerId) a list of emails delimited by ',' (?identifier=my.email.com,my.email2.com,1,...).
     * If no identifier is set, the method returns a list of all customers.
     *
     * Admin permissions are required.
     */
    def listcustomers = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            List<Customer> customersCollection = []

            if (params.identifer) {
                List<String> identifiers = params.identifiers.split(',')
                identifiers.each {
                    Customer customer = it.isInteger() ? Customer.findById(it as Long) : Customer.findByEmail(it)
                    if (customer) {
                        customersCollection.add(customer)
                    } else {
                        this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No customer found with id/email ' + it + ' .')
                        return
                    }
                }
            } else {
                customersCollection.addAll(Customer.getAll())
            }

            withFormat {
                xml {
                    def builder = new StreamingMarkupBuilder()
                    builder.encoding = 'UTF-8'
                    String customersXML = builder.bind {
                        customers {
                            customersCollection.each { Customer customerData ->
                                customer {
                                    id customerData.id
                                    email customerData.email
                                    creationDate customerData.creationDate
                                    hosts {
                                        customerData.hosts.each { Host customerHost ->
                                            host customerHost.cname
                                        }
                                    }
                                }
                            }
                        }
                    }
                    def xml = XmlUtil.serialize(customersXML).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                    xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                    render contentType: "text/xml", xml
                }
                json {
                    def customers = []
                    customersCollection.each { Customer customerData ->
                        def customer = [:]
                        customer.put('id', customerData.id)
                        customer.put('email', customerData.email)
                        customer.put('creationDate', customerData.creationDate)
                        def hosts = []
                        customerData.hosts.each { Host customerHost ->
                            hosts.add(customerHost.cname)
                        }
                        customer.put('hosts', hosts)
                        customers.add(customer)
                    }
                    render customers as JSON
                }
            }
        } else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'Only admins are allowed to request these resources.')
        }
    }

    /**
     * The method changes the properties of an existing customer identified by id or email.
     */
    def editcustomers = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            Customer customer
            def parameters = [:]
            Boolean hasError = Boolean.FALSE

            if (params.identifier) {
                customer = params.identifier.isInteger() ? Customer.findById(params.identifier as Long) : Customer.findByEmail(params.identifier)
            } else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No customer found with id/email ' + params.identifier + ' .')
                return
            }

            request.withFormat {
                xml {
                    def xml = parseRequestDataToXML(request)
                    if (xml) {
                        def xmlParameters = xml[0].children

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
                    } else { hasError = Boolean.TRUE }
                }
                json {
                    def json = parseRequestDataToJSON(request)
                    if (json) {
                        if (json.parameters) {
                            json.parameters.each {
                                if (it.parameterName && it.parameterValue) {
                                    parameters[it.parameterName] = it.parameterValue
                                } else {
                                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'paramterName or paramterValue is null or empty. Please check your data.')
                                    hasError = Boolean.TRUE
                                    return
                                }
                            }
                        }
                    } else { hasError = Boolean.TRUE }
                }
            }

            parameters.each { key, value ->
                if (customer.hasProperty(key)) {
                    if ('CREATIONDATE' == key.toUpperCase()) {
                        //creationdate should not be changed.
                    } else {
                        customer.properties[key] = value
                    }
                } else {
                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Property ' + it.key + ' not exists for UserNotifications.')
                    hasError = Boolean.TRUE
                    return
                }
            }

            if (hasError) {
                return
            }

            if (customer.save(flush: true)) {
                this.renderRestResult(HttpStatus.OK, null, null, 'Customer changed.')
            } else {
                this.renderRestResult(HttpStatus.INTERNAL_SERVER_ERROR, null, null, 'An error occurred while saving the host.')
            }
        } else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'Only admins are allowed to request these resources.')
        }
    }
}
