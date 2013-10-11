package org.zenboot.portal

import org.springframework.dao.DataIntegrityViolationException

class TemplateController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [templateInstanceList: Template.list(params), templateInstanceTotal: Template.count()]
    }

    def create() {
        [templateInstance: new Template(params)]
    }

    def save() {
        def templateInstance = new Template(params)
        if (!templateInstance.save()) {
            render(view: "create", model: [templateInstance: templateInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'template.label', default: 'Template'), templateInstance.id])
        redirect(controller: "executionZone", action: "show", id: templateInstance.executionZone.id)
    }
    
    def ajaxGetTemplateParameters() {
        def templateInstance = Template.get(params.id)
        
        if (!templateInstance) {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, "Not Template exists for this id")
            return
        }
        render (contentType:"text/json"){
            template name:templateInstance.name, templateUrl:createLink(action: 'ajaxGetTemplate', id:templateInstance.id),  dateCreated:templateInstance.dateCreated, updateUrl:createLink(action:'update', id:templateInstance.id)
        }
        return
    }
    
    def ajaxGetTemplate() {
        def templateInstance = Template.get(params.id)
        
        if (!templateInstance) {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, "Not Template exists for this id")
            return
        }
        
        render(text: templateInstance.template)
        return
    }

    def show() {
        def templateInstance = Template.get(params.id)
        if (!templateInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'template.label', default: 'Template'), params.id])
            redirect(action: "list")
            return
        }

        [templateInstance: templateInstance]
    }

    def edit() {
        def templateInstance = Template.get(params.id)
        if (!templateInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'template.label', default: 'Template'), params.id])
            redirect(action: "list")
            return
        }

        [templateInstance: templateInstance]
    }

    def update() {
        def templateInstance = Template.get(params.id)
        if (!templateInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'template.label', default: 'Template'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (templateInstance.version > version) {
                templateInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'template.label', default: 'Template')] as Object[],
                          "Another user has updated this Template while you were editing")
                render(view: "edit", model: [templateInstance: templateInstance])
                return
            }
        }

        templateInstance.properties = params

        if (!templateInstance.save()) {
            render(view: "edit", model: [templateInstance: templateInstance])
            return
        }
        
        templateInstance.addToTemplateVersions(new TemplateVersion(params)).save(flush: true)
        
		flash.message = message(code: 'default.updated.message', args: [message(code: 'template.label', default: 'Template'), templateInstance.id])
        redirect(action: "show", id: templateInstance.id)
    }

    def delete() {
        def templateInstance = Template.get(params.id)
        if (!templateInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'template.label', default: 'Template'), params.id])
            redirect(action: "list")
            return
        }

        try {
            templateInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'template.label', default: 'Template'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'template.label', default: 'Template'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
