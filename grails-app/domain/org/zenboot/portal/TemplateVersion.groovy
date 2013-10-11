package org.zenboot.portal

import java.sql.Timestamp;

class TemplateVersion implements Comparable {
    static auditable = true
    
    String content
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
        sort dateCreated: "desc"
    }

    static constraints = {
    }
}
