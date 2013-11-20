package org.zenboot.portal

import groovy.text.SimpleTemplateEngine

import java.nio.charset.Charset;
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import org.springframework.http.HttpStatus


import org.springframework.dao.DataIntegrityViolationException
import org.zenboot.portal.processing.*

class TemplateController {
    


    static allowedMethods = [save: "POST", update: "POST", delete: "POST", upload: "POST"]


    
    def index() {
      def executionZoneInstance 
      if(params.execId) {
        executionZoneInstance = ExecutionZone.get(params.execId)
      } 
      
      if(params.puppetEnvironment){
        executionZoneInstance = ExecutionZone.findByPuppetEnvironmentAndQualityStage(params.puppetEnvironment, params.qualityStage)
        if (!executionZoneInstance) {
            this.sendError(HttpStatus.NOT_FOUND, "No ${ExecutionZone.class.simpleName} found for environment '${params.puppetEnvironment}'")
            return
        }
      }
      
      def templates
      if (!executionZoneInstance) {
        templates = Template.findAll()
      }else{
        templates = executionZoneInstance.templates
      }
        
      render (contentType:"text/json"){
         templates: array{
            templates.each {
                template(id:it.id, 
                name:it.name)
            }
         }
      }
    }


    def save() {
        
        def executionZoneInstance
        
        if(params.puppetEnvironment){
          executionZoneInstance = ExecutionZone.findByPuppetEnvironmentAndQualityStage(params.puppetEnvironment, params.qualityStage)
          if (!executionZoneInstance) {
              this.sendError(HttpStatus.NOT_FOUND, "No ${ExecutionZone.class.simpleName} found for environment '${params.puppetEnvironment}'")
              return
          }
        } else {
          executionZoneInstance = ExecutionZone.get(params.executionZone.id)
        }
        
        def templateInstance = new Template(params)
        templateInstance.executionZone = executionZoneInstance
        if (!templateInstance.save(flush:true)) {
            this.sendError(HttpStatus.BAD_REQUEST, "Can't save template! Please add a commit message and check if the name is unique.")
            return
        }

        // test template for non existing parameters?
        def missingParameters = getTemplateMissingParameters(templateInstance)
        if ( missingParameters.size() ) {
            flash.warning = "Warning, missing parameters: " + missingParameters.join(", ")
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'template.label', default: 'Template'), templateInstance.id])
        redirect(action: "show", id: templateInstance.id)
    }
    
    def show() {
        def templateInstance = Template.get(params.id)
        if (!templateInstance) {
            this.sendError(HttpStatus.NOT_FOUND, "Not Template exists for this id")
            return
        }
        render (contentType:"text/json"){
            template name:templateInstance.name, 
                    url:createLink(mapping:'template', action: 'ajaxGetVersion', id:templateInstance.getTemplateObject().id),
                    templateUrl:createLink(mapping:'template',action: 'ajaxGetTemplate', id:templateInstance.getTemplateObject().id),
                    deleteTemplateUrl:createLink(mapping:'template',action: 'delete', id:templateInstance.id),
                    showFileUrl:createLink(controller: 'propertiesRest', action: 'showFile', id:templateInstance.id),
                    versions: array{
                        templateInstance.templateVersions.each {
                            version(id:it.id, 
                            create:it.dateCreated, 
                            url:createLink(mapping:'template',action: 'ajaxGetVersion', id:it.id),
                            user:it.user)
                        }
                    },
                    dateCreated:templateInstance.dateCreated, 
                    updateUrl:createLink(mapping:'template', action:'update', id:templateInstance.id),
                    message: flash.message,
                    warning: flash?.warning
        }
        return
    }
    
    def ajaxGetVersion() {
        def templateInstance = TemplateVersion.get(params.id)
        
        if (!templateInstance) {
            this.sendError(HttpStatus.NOT_FOUND, "No Version exists for this id")
            return
        }
        
        render (contentType:"text/json"){
          version(id:templateInstance.id, 
              create:templateInstance.dateCreated, 
              url:createLink(mapping:'template', action: 'ajaxGetTemplate', id:templateInstance.id), 
              commentUrl:createLink( mapping:'template', action: 'ajaxGetComment', id:templateInstance.id)
          )
        }
        
        return
    }
    
    def ajaxGetTemplate() {
        def templateInstance = TemplateVersion.get(params.id)
        
        if (!templateInstance) {
            this.sendError(HttpStatus.NOT_FOUND, "No Template exists for this id")
            return
        }
        
        render(text: templateInstance.content)
        return
    }
    
    def ajaxGetComment() {
        def templateInstance = TemplateVersion.get(params.id)
        
        if (!templateInstance) {
            this.sendError(HttpStatus.NOT_FOUND, "No Template exists for this id")
            return
        }
        
        render(text: templateInstance.comment)
        return
    }

    def checkParameters(){
    
      def executionZoneInstance = ExecutionZone.get(params.execId)
      if (!executionZoneInstance) {
        this.sendError(HttpStatus.NOT_FOUND, "No ExecutionZone exists for this id")
        return
      }
      def missingParameters = []
      
      executionZoneInstance.templates.each {      
        missingParameters = (missingParameters << getTemplateMissingParameters(it)).flatten().unique()
      }
      
      render (contentType:"text/json"){
        parameters: array{
          missingParameters.each {
            parameter(name: it)
          }
        }
      }
      return
      
    }



    def update() {
        flash.action = 'template'
        def templateInstance = Template.get(params.id)
        if (!templateInstance) {
            this.sendError(HttpStatus.NOT_FOUND, "No Template exists for this id")
            return
        }
        
        templateInstance.version++ // Workaround to force a object save. 
        
        templateInstance.properties = params
        if (!templateInstance.save(flush: true)) {
            this.sendError(HttpStatus.BAD_REQUEST, "Can't save template! Please add a commit message and check if the name is unique.")
            return
        }

        // test template for non existing parameters?
        def missingParameters = getTemplateMissingParameters(templateInstance)
        if ( missingParameters.size() ) {
            flash.warning = "Warning, missing parameters: " + missingParameters.join(", ")
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'template.label', default: 'Template'), templateInstance.id])
        redirect(action: "show", id: templateInstance.id)
    }
    
    def upload() {
        flash.action = 'template'
        def executionZoneInstance
        
        if(params.puppetEnvironment){
          executionZoneInstance = ExecutionZone.findByPuppetEnvironmentAndQualityStage(params.puppetEnvironment, params.qualityStage)
        } else {
          executionZoneInstance = ExecutionZone.get(params.execId)
        }
        
        if (!executionZoneInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZone.label', default: 'ExecutionZone'), params.executionZone.id])
            redirect(controller: "executionZone", action: "index")
            return
        }
        
        def f = request.getFile('importFile')
        if (f.empty) {
            flash.message = 'file cannot be empty'
            render(controller: "executionZone", action: "show", id: executionZoneInstance.id)
            return
        }
        
        try{
            new File(this.grailsApplication.config.zenboot.template.tempDir.toString()).deleteDir()
            new File(this.grailsApplication.config.zenboot.template.tempDir.toString() + "/tmp").mkdirs()
        } catch (all) {
            flash.message = "Can't create tmp dir."
            render(controller: "executionZone", action: "show", id: executionZoneInstance.id)
        }
        
        f.transferTo(new File(this.grailsApplication.config.zenboot.template.tempDir.toString() + "/import.zip"))
        
        def zipFile = new ZipFile(new File(this.grailsApplication.config.zenboot.template.tempDir.toString() + "/import.zip"))
        
        int files = 0
        int imported = 0
        zipFile.entries().each {
            files++
            if(!(it =~ /(\/\.)|(\/$)/)){
              def name = (it.name =~ /.*\//).replaceAll("")
              Template template = Template.findByName(name) 
              if(template && params.updateTemplates) {
                template.template = zipFile.getInputStream(it).text
                template.message = params.commitMessage
                template.version++ // Workaround to force a object save. 
              }else{
                template = new Template(name: name, template: zipFile.getInputStream(it).text, message: params.commitMessage, executionZone:executionZoneInstance)
              }
              
              if(template.save(flush:true)){
                  imported++
              }
            }
         }
        
        
        new File(this.grailsApplication.config.zenboot.template.tempDir.toString()).deleteDir()
        
        flash.message = message(code: 'template.imported', default: "{1} of {0} templates imported!", args: [files, imported])
        redirect(controller: "executionZone", action: "show", id: executionZoneInstance.id)
        return
    }
    
    def export() {
        def executionZoneInstance = ExecutionZone.get(params.execId)
        
        if (!executionZoneInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZone.label', default: 'ExecutionZone'), params.execId])
            redirect(controller: "executionZone", action: "index")
            return
        }
        
        try{
            new File(this.grailsApplication.config.zenboot.template.tempDir.toString()).deleteDir()
            new File(this.grailsApplication.config.zenboot.template.tempDir.toString()).mkdirs()
        } catch (all) {
            flash.message = "Can't create tmp dir."
            render(controller: "executionZone", action: "show", id: executionZoneInstance.id)
        }
        
        
        def exportFile = new File(this.grailsApplication.config.zenboot.template.tempDir.toString() + "/export.zip")
        def zipFile = new ZipOutputStream(new FileOutputStream(exportFile))
        
        
        if(executionZoneInstance.templates){
            executionZoneInstance.templates.each {
                zipFile.putNextEntry(new ZipEntry(it.name))
                zipFile.write(it.template.getBytes(Charset.forName("UTF-8")))
                zipFile.closeEntry();
            }
        } else {
            flash.message = "There are no Templates in this zone"
            render(controller: "executionZone", action: "show", id: executionZoneInstance.id)
        }
        
        
        
        zipFile.close()
        
        
        
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment;filename=${exportFile.getName()}")
        
        response.outputStream << exportFile.newInputStream() // Performing a binary stream copy
        
        new File(this.grailsApplication.config.zenboot.template.tempDir.toString()).deleteDir()
        return
    }

    def delete() {
      flash.action = 'template'
        def templateInstance = Template.get(params.id)
        if (!templateInstance) {
            this.sendError(HttpStatus.NOT_FOUND, "No Template exists for this id")
            return
        }

        try {
            templateInstance.delete(flush: true)
            this.sendError(HttpStatus.OK, "Template deleted.")
            return
        }
        catch (DataIntegrityViolationException e) {
            this.sendError(HttpStatus.BAD_REQUEST, "Can't delete template.")
            return
        }
    }

    private getTemplateMissingParameters(Template templateInstance) {
        def procParams = templateInstance.executionZone.getProcessingParameters()
        def binding = procParams.inject([:]) { Map map, ProcessingParameter procParam ->
            map[procParam.name?.toLowerCase()] = procParam.value
            return map
        }

        def templateOutput = new SimpleTemplateEngine().createTemplate(templateInstance?.template).make(binding.withDefault{"[MISSINGPARAMETER_$it]"})
        def missingParameters = []
        templateOutput.toString().eachMatch(/\[MISSINGPARAMETER_([^]]*)\]/) { missingParameters << it[1] }
        return missingParameters
    }

    private sendError(HttpStatus httpStatus, String errorMessage="") {
        response.setStatus(httpStatus.value())
        if (errorMessage) {
            response << errorMessage
        }
        response.flushBuffer()
    }
}
