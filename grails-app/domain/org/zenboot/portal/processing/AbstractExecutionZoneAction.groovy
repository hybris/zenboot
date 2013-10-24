package org.zenboot.portal.processing

abstract class AbstractExecutionZoneAction {

    Date creationDate
    File scriptDir
    Set processingParameters = []

    static belongsTo = [executionZone:ExecutionZone]

    static hasMany = [processingParameters:ProcessingParameter]
    
    static constraints = { scriptDir nullable:false }

    def beforeInsert = {
        this.creationDate = new Date()
    }

    static mapping = {
        table "execution_zone_action"
        processingParameters cascade: "all-delete-orphan"
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
            existingParam.comment = param.comment
            existingParam.save()
        } else {
            this.processingParameters << param
        }
    }
}
