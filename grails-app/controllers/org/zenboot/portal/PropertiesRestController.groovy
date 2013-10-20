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
        
        Template templateInstance = Template.findByExecutionZoneAndNameIlike(execZone, params.propertyFile)
        if (!templateInstance) {
            this.sendError(HttpStatus.NOT_FOUND, "No '${params.propertyFile}' property template found for environment '${params.puppetEnvironment}'")
            return
        }
        
        def templateOutput
        try {
            templateOutput = new SimpleTemplateEngine().createTemplate(templateInstance?.template).make(binding)
            
            response.contentType = 'application/octet-stream'
            response.setHeader 'Content-disposition', "attachment; filename=\"templateInstance?.name\""
            response.outputStream << templateOutput.toString()
            response.outputStream.flush()

        } catch (MissingPropertyException ex) {
            this.sendError(HttpStatus.BAD_REQUEST, ex.message)
            return
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
