package org.zenboot.portal.api

import groovyx.net.http.RESTClient
import spock.lang.Specification

abstract class ZenbootApiSpec extends Specification {
    def baseUrl = System.properties.get('grails.testing.functional.baseUrl') ?: 'http://localhost:8080/zenboot/'
    RESTClient http

    def setup() {
        http = new RESTClient(baseUrl)
        http.auth.basic 'admin', 'zenboot'
        http.setContentType("application/json")
        http.setHeaders Accept: 'application/json', 'Content-Type': 'application/json'
    }
}
