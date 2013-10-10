package org.zenboot.portal

import groovy.text.SimpleTemplateEngine
import org.springframework.http.HttpStatus;

import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ProcessingParameter

class PropertiesRestController {

    def rest = {       
        if (!params.puppetEnvironment) {
            this.sendError(HttpStatus.BAD_REQUEST, "Puppet environment missing")
            return
        }

        ExecutionZone execZone = ExecutionZone.findByPuppetEnvironmentAndQualityStage(params.puppetEnvironment, params.qualityStage)
        if (!execZone) {
            this.sendError(HttpStatus.NOT_FOUND, "No ${ExecutionZone.class.simpleName} found for environment '${params.puppetEnvironment}'")
            return
        }

        def procParams = execZone.getProcessingParameters()
        def binding = procParams.inject([:]) { Map map, ProcessingParameter procParam ->
            map[procParam.name?.toLowerCase()] = procParam.value
            return map
        }
        
        Template template = Template.findByExecutionZoneAndName(execZone, params.propertyFile)
        if (!template.count()) {
            this.sendError(HttpStatus.NOT_FOUND, "No '${params.propertyFile}' property template found for environment '${params.puppetEnvironment}'")
            return
        }
        def templateData = template.getContent()
        def templateOutput = new SimpleTemplateEngine().createTemplate(templateData).make(binding)
        
        response << templateOutput.toString()
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