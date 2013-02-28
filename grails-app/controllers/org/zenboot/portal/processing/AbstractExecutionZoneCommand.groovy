package org.zenboot.portal.processing

import org.zenboot.portal.ControllerUtils
import org.zenboot.portal.processing.meta.ParameterMetadata


abstract class AbstractExecutionZoneCommand {

    def executionZoneService

    Long id
    File scriptDir
    boolean containsInvisibleParameters
    Map execZoneParameters

    static constraints = {
        id nullable:false
        scriptDir nullable:false, validator: { value, commandObj ->
            if (!value.exists()) {
                return "executionZone.failure.scriptDirNotExist"
            }
        }
    }

    boolean validate() {
        this.execZoneParameters = ControllerUtils.getParameterMap(params)

        if (this.containsInvisibleParameters) {
            def paramMetadatas = this.executionZoneService.getExecutionZoneParameters(ExecutionZone.get(this.id), this.scriptDir)
            paramMetadatas.findAll { ParameterMetadata paramMetadata ->
                if (!paramMetadata.visible && !this.execZoneParameters[paramMetadata.name]) {
                    this.execZoneParameters[paramMetadata.name] = paramMetadata.value
                }
            }
        }

        this.execZoneParameters.each { key, value ->
            if (!value) {
                this.errors.reject('executionZone.parameters.emptyValue', [key].asType(Object[]), 'Mandatory parameter is empty')
            }
        }

        return this.errors.hasErrors()
    }

    abstract AbstractExecutionZoneAction getExecutionZoneAction();
}
