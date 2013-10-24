package org.zenboot.portal.processing

class ProcessingParameterLog {

    String name
    String value
    String description
    String comment
    String user
    Date dateCreated
    Date lastUpdated

    static belongsTo = [processingParameter: ProcessingParameter]

    static mapping = {
        name type: 'text'
        value type: 'text'
        description type: 'text'
        comment type: 'text'
    }

    static constraints = {
    }

    def beforeInsert(){
        user = domainClass.grailsApplication.mainContext.springSecurityService.principal?.username
    }
}
