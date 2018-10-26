package org.zenboot.portal

import grails.converters.JSON
import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONException
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletRequest


abstract class AbstractRestController {

    protected void renderRestResult(HttpStatus status, def value=null, URI referral=null, String message=null) {
        response.status = status.value
        def result = new RestResult(status:status.value, value:value, referral:referral, message:message)
        withFormat {
            json { render result as JSON }
            xml { render result as XML }
        }
    }

    /**
     * The method parse and return the incoming data from the request into XML. Set BAD_REQUEST with parser error
     * if it is not possible to parse the data and the method returns null.
     * @param request - the HttpServletRequest which contains the data
     * @return XML ChildNode or null
     */
    protected def parseRequestDataToXML(HttpServletRequest request) {
        try {
            return request.XML
        }
        catch (ConverterException e) {
            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, e.message)
        }
    }

    /**
     * The method parse and return the incoming data from the request into JSON. Set BAD_REQUEST with parser error
     * if it is not possible to parse the data and the method returns null.
     * @param request - the HttpServletRequest which contains the data
     * @return JSONObject or null
     */
    protected def parseRequestDataToJSON(def request) {
        try {
            return new JSONObject(request.getReader().text)
        }
        catch (JSONException e) {
            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, e.getMessage())
        }
    }
}
