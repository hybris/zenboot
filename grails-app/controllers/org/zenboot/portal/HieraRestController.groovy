package org.zenboot.portal

import grails.converters.JSON

import org.ho.yaml.Yaml
import org.springframework.http.HttpStatus
import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ProcessingParameter


class HieraRestController {

    def rest = {
        if (!params.puppetEnvironment) {
            this.sendError(HttpStatus.BAD_REQUEST, "Puppet environment missing")
            return
        }

        ExecutionZone execZone = ExecutionZone.findByPuppetEnvironment(params.puppetEnvironment)
        if (!execZone) {
            this.sendError(HttpStatus.NOT_FOUND, "No ${ExecutionZone.class.simpleName} found for environment '${params.puppetEnvironment}'")
            return
        }

        request.withFormat {
            def procParams = execZone.getProcessingParameters()
            def map = procParams.inject([:]) { Map map, ProcessingParameter procParam ->
                map[procParam.name?.toLowerCase()] = procParam.value
                return map
            }
            yaml {
                response << Yaml.dump(map)
            }
            json {
                render map as JSON
            }
            properties {
                render(template:   "parametersAsProperties", model: [map: map])
            }
        }
        
        response.flushBuffer()
    }

    private sendError(HttpStatus httpStatus, String errorMessage="") {
        response.setStatus(httpStatus.value())
        if (errorMessage) {
            response << errorMessage
        }
        response.flushBuffer()
    }
}