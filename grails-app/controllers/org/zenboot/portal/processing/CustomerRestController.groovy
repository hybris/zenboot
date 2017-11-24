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

    static allowedMethods = [listCustomers: "GET"]

    def springSecurityService

    /**
     * The method return a list with customers. It is possible to specify the customer by email or a list of emails delimited by ',' (?email=my.email.com,my.email2.com,...). It is also possible
     * to get specify the customer by id or a list of ids delimted by ',' (?customerId=1,2,...).
     * If neither email nor customerId is set, the method returns a list of all customers.
     *
     * Admin permissions are required.
     */
    def listCustomers = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            List<Customer> customersCollection = []
            if (params.email) {
                if (params.email.contains(',')) {
                    List<String> emails = params.email.split(',')
                    emails.each {
                        Customer customer = Customer.findByEmail(it)
                        if (customer) {
                            customersCollection.add(customer)
                        } else {
                            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No customer with email ' + it + ' found.')
                            return
                        }
                    }
                } else {
                    Customer customer = Customer.findByEmail(params.email as String)
                    if (customer) {
                        customersCollection.add(customer)
                    } else {
                        this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No customer with email ' + params.email + ' found.')
                        return
                    }
                }
            } else if (params.customerId) {
                if (params.customerId.contains(',')) {
                    List<String> iDs = params.customerId.split(',')
                    iDs.each {
                        if (it.isInteger()) {
                            Customer customer = Customer.findById(it as Long)
                            if (customer) {
                                customersCollection.add(customer)
                            } else {
                                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No customer with id ' + it + ' found.')
                                return
                            }

                        } else {
                            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The customerId param is invalid. ' +
                                    'It has to be a Long value or a list of Long values delimited by ","')
                            return
                        }
                    }
                } else {
                    if (params.customerId.isInteger()) {
                        Customer customer = Customer.findById(params.customerId as Long)
                        if (customer) {
                            customersCollection.add(customer)
                        } else {
                            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No customer with id ' + params.customerId + ' found.')
                            return
                        }
                    } else {
                        this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The customerId param is invalid. ' +
                                'It has to be a Long value or a list of Long values delimited by ","')
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
                        customer.put('id',customerData.id)
                        customer.put('email', customerData.email)
                        customer.put('creationDate', customerData.creationDate)
                        def hosts = []
                        customerData.hosts.each {Host customerHost ->
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
}
