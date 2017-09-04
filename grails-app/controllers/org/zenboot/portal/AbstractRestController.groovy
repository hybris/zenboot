package org.zenboot.portal

import grails.converters.JSON
import grails.converters.XML

import org.springframework.http.HttpStatus


abstract class AbstractRestController {

    protected void renderRestResult(HttpStatus status, def value=null, URI referral=null, String message=null) {
        response.status = status.value
        def result = new RestResult(status:status.value, value:value, referral:referral, message:message)
        withFormat {
            json { render result as JSON }
            xml { render result as XML }
        }
    }
}
