package org.zenboot.portal.processing

import org.zenboot.portal.ControllerUtils
import org.zenboot.portal.processing.ExecutionZoneService
import org.zenboot.portal.processing.meta.ParameterMetadata


@grails.validation.Validateable
abstract class AbstractExecutionZoneCommand {

    // DependencyInjection seems to be buggy
    // This is not injected, and so wedo it more complex ...
    // def executionZoneService

    Long execId
    File scriptDir
    boolean containsInvisibleParameters
    Map execZoneParameters
    Map parameters

    static constraints = {
        execId nullable:false
        scriptDir nullable:false, validator: { value, commandObj ->
            if (!value.exists()) {
                return "executionZone.failure.scriptDirNotExist"
            }
        }
    }


    boolean setParameters(Map parameters) {
        this.execZoneParameters = ControllerUtils.getParameterMap(parameters ?: [:], "key", "value")
        this.hasErrors()
        // in the case of restricted users, parameters will be a limited map and we need to fill
        // it up with all the defaultvalues and values set in the executionZone
        def paramMetadatas = this.getExecutionZoneService().getExecutionZoneParameters(ExecutionZone.get(this.execId), this.scriptDir)
        paramMetadatas.each { ParameterMetadata paramMetadata ->
            if (!this.execZoneParameters[paramMetadata.name]) {
              // This will also fill up if !paramMetadata.visible && !this.execZoneParameters[paramMetadata.name]
              this.execZoneParameters[paramMetadata.name] = paramMetadata.value
            }
        }
        // ... and some validation at the end ... no more empty values
        this.execZoneParameters.each { key, value ->
            if (!value) {
                this.errors.reject('executionZone.parameters.emptyValue', [key].asType(Object[]), 'Mandatory parameter is empty')
            }
        }
        return this.errors.hasErrors()
    }

    abstract ExecutionZoneService getExecutionZoneService();
    abstract AbstractExecutionZoneAction getExecutionZoneAction();
}
