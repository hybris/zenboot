package org.zenboot.portal.processing


import org.zenboot.portal.Template;

class ExecutionZone {
    static auditable = true
    
    Date creationDate
    ExecutionZoneType type
    String description
    String puppetEnvironment
    String qualityStage
    Set processingParameters = []
    Set actions = []
    boolean enabled = true
    boolean enableExposedProcessingParameters = true

    static hasMany = [actions:ExecutionZoneAction, processingParameters:ProcessingParameter, templates:Template]

    static constraints = {
        type nullable:false
    }

    static mapping = {
        actions sort: 'id', order: 'desc'
        puppetEnvironment index:'idx_exczn_pupenv'
        actions cascade: 'all-delete-orphan'
        processingParameters cascade: 'all-delete-orphan'
    }

    def beforeInsert = {
        this.creationDate = new Date()
    }

    boolean isEnabled() {
        return this.type?.enabled && this.enabled
    }

    ProcessingParameter getProcessingParameter(String key) {
        return this.processingParameters.find {
            it.name == key
        }
    }

    void addProcessingParameter(ProcessingParameter param) {
        ProcessingParameter existingParam = this.getProcessingParameter(param.name)
        if (existingParam) {
            existingParam.value = param.value
            existingParam.published = param.published
            existingParam.description = param.description
            existingParam.exposed = param.exposed
            existingParam.save()
        } else {
            this.processingParameters << param
        }
    }

}
