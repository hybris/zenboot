package org.zenboot.portal

import org.zenboot.portal.processing.ExecutionZone;
import java.util.regex.Matcher
import java.util.regex.Pattern

class Template implements Comparable {
    static auditable = true
    
    String name
    String template
    Date dateCreated
    Date lastUpdated
    
    SortedSet templateVersions
    
    static belongsTo = [executionZone: ExecutionZone]
    static hasMany = [templateVersions: TemplateVersion]
    
    static transients = ['template','content']
    
    static mapping = {
        templateVersions cascade: "all-delete-orphan"
    }
    
    int compareTo(obj) {
        name <=> obj.name
    }
    
    static constraints = {
        name(blank: false)
        name validator: { val, obj ->
            def templateWithSameNameAndExecZone = Template.findByNameAndExecutionZone(val, obj.executionZone)
            return !templateWithSameNameAndExecZone || templateWithSameNameAndExecZone == obj
        }
    }
    
    String getTemplate(){
        TemplateVersion templateFile
        if(templateVersions){
            templateFile = templateVersions.last()
        } else {
            templateFile = new TemplateVersion()
        }
        return templateFile.content
    }
    
    TemplateVersion getTemplateObject(){
        TemplateVersion templateFile
        if(templateVersions){
            templateFile = templateVersions.last()
        } else {
            templateFile = new TemplateVersion()
        }
        return templateFile
    }
    
    void setTemplate(String template){
        this.template = template;
    }
        
    def afterUpdate(){
        saveTeamplateVersion()
    }
    
    def afterInsert(){
        saveTeamplateVersion()
    }
    
    def saveTeamplateVersion(){
        if(this.template){
            addToTemplateVersions(new TemplateVersion(content: this.template))
        }
        this.template = null
    }
    
    
    def importFile(String file){
        template = new File(file).getText()
        name = (file =~ /.*\//).replaceAll("")
    }
    
    def exportFile(String path){
        new File(path + name).write(getTemplate())
    }
    
}
