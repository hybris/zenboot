package org.zenboot.portal

import java.sql.Timestamp
import org.zenboot.portal.processing.ExecutionZone;

class Template {
    static auditable = true
    
    String name
    String content
    Date dateCreated
    Date lastUpdated
    
    static belongsTo = [executionZone: ExecutionZone]
    static hasMany = [templateVersions: TemplateVersion]
    
    static mapping = {
        content type: "text"
        templateVersions cascade: "all-delete-orphan"
    }
    
    static constraints = {
        name(blank: false)
    }
}
