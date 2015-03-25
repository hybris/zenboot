package org.zenboot.portal.processing

import org.springframework.dao.DataIntegrityViolationException


class ExecutionZoneTypeController {

    def executionZoneService
    def springSecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [executionZoneTypeInstanceList: ExecutionZoneType.list(params), executionZoneTypeInstanceTotal: ExecutionZoneType.count()]
    }

    def updateTypes() {
        executionZoneService.synchronizeExecutionZoneTypes()
        flash.message = message(code:"executionZoneType.list.synchronize", default:"List is synchronized")
        redirect(action:'list')
    }

    def create() {
        [executionZoneTypeInstance: new ExecutionZoneType(params)]
    }

    def save() {
        def executionZoneTypeInstance = new ExecutionZoneType(params)
        if (!executionZoneTypeInstance.save(flush: true)) {
            render(view: "create", model: [executionZoneTypeInstance: executionZoneTypeInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'executionZoneType.label', default: 'ExecutionZoneType'), executionZoneTypeInstance.id])
        redirect(action: "show", id: executionZoneTypeInstance.id)
    }

    def show() {
        def executionZoneTypeInstance = ExecutionZoneType.get(params.id)
        if (!executionZoneTypeInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZoneType.label', default: 'ExecutionZoneType'), params.id])
            redirect(action: "list")
            return
        }

        [executionZoneTypeInstance: executionZoneTypeInstance]
    }

    def edit() {
        def executionZoneTypeInstance = ExecutionZoneType.get(params.id)
        if (!executionZoneTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZoneType.label', default: 'ExecutionZoneType'), params.id])
            redirect(action: "list")
            return
        }

        [executionZoneTypeInstance: executionZoneTypeInstance]
    }

    def update() {
        def executionZoneTypeInstance = ExecutionZoneType.get(params.id)
        if (!executionZoneTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZoneType.label', default: 'ExecutionZoneType'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (executionZoneTypeInstance.version > version) {
                executionZoneTypeInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'executionZoneType.label', default: 'ExecutionZoneType')] as Object[],
                          "Another user has updated this ExecutionZoneType while you were editing")
                render(view: "edit", model: [executionZoneTypeInstance: executionZoneTypeInstance])
                return
            }
        }

        executionZoneTypeInstance.properties = params
        executionZoneTypeInstance.description = "lastly set devMode to "+ executionZoneTypeInstance.devMode + " by " + springSecurityService.currentUser.username + " at " + new Date()

        if (!executionZoneTypeInstance.save(flush: true)) {
            render(view: "edit", model: [executionZoneTypeInstance: executionZoneTypeInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'executionZoneType.label', default: 'ExecutionZoneType'), executionZoneTypeInstance.id])
        redirect(action: "show", id: executionZoneTypeInstance.id)
    }

    def delete() {
        def executionZoneTypeInstance = ExecutionZoneType.get(params.id)
        if (!executionZoneTypeInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZoneType.label', default: 'ExecutionZoneType'), params.id])
            redirect(action: "list")
            return
        }

        try {
            executionZoneTypeInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'executionZoneType.label', default: 'ExecutionZoneType'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'executionZoneType.label', default: 'ExecutionZoneType'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
