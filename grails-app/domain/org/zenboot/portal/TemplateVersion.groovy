package org.zenboot.portal

import java.sql.Timestamp;

class TemplateVersion {
    static auditable = true
    
    String content
    Date dateCreated
    Date lastUpdated
    
    static belongsTo = [template: Template]

    static mapping = {
        content type: "text"
    }

    static constraints = {
    }
}
