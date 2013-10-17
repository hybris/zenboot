package org.zenboot.portal

class TemplateVersion implements Comparable {
    static auditable = true
    
    String content
    String user
    String comment
    Date dateCreated
    Date lastUpdated
    
    static belongsTo = [template: Template]
    
    int compareTo(obj) {
        if(!dateCreated){
            dateCreated = new Date()
        }
        dateCreated.compareTo(obj.dateCreated)
    }

    static mapping = {
        content type: "text"
        comment type: "text"
    }

    static constraints = {
    }
    
    def beforeInsert(){
        user = domainClass.grailsApplication.mainContext.springSecurityService.principal?.username
    }
}
