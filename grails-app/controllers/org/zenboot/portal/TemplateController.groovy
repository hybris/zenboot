package org.zenboot.portal

import org.springframework.dao.DataIntegrityViolationException

class TemplateController {

    def index() {
        redirect(action: "list", params: params)
    }
   
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [templateInstanceList: Template.list(params), templateInstanceTotal: Template.count()]
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
    
    def create() {
        return [templateInstance: new Template(params)]
    }
    
    def save() {
        Template templateInstance = new Template(params)
        if (!templateInstance.save(flush: true)) {
            render(view: "create", model: [templateInstance: templateInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'template.label', default: 'Template'), templateInstance.id])
        redirect(action: "list", id: templateInstance.id)
    }
    
    def update() {
        def templateInstance = Template.get(params.id)
        if (!templateInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'template.label', default: 'Template'), params.id])
            redirect(action: "list")
            return
        }

        templateInstance.properties = params
        if (!templateInstance.save(flush: true)) {
            render(view: "edit", model: [templateInstance: templateInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'template.label', default: 'Template'), templateInstance.id])
        redirect(action: "list", id: templateInstance.id)
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
            redirect(action: "list", id: params.id)
        }
    }
}
