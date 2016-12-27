package org.zenboot.portal

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.dao.DataIntegrityViolationException

class ServiceUrlController {
    def filterPaneService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = params.max ?: 10
        [
                serviceUrlInstanceList: filterPaneService.filter(params, ServiceUrl),
                serviceUrlInstanceTotal: filterPaneService.count(params, ServiceUrl)
        ]
    }

    def show() {
        def serviceUrlInstance = ServiceUrl.get(params.id)
        if (!serviceUrlInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'serviceUrl.label', default: 'ServiceUrl'), params.id])
            redirect(action: "list")
            return
        }

        [serviceUrlInstance: serviceUrlInstance]
    }


    @Secured('ROLE_ADMIN')
    def delete() {
        def serviceUrlInstance = ServiceUrl.get(params.id)
        if (!serviceUrlInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'serviceUrl.label', default: 'ServiceUrl'), params.id])
            redirect(action: "list")
            return
        }

        try {
            serviceUrlInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'serviceUrl.label', default: 'ServiceUrl'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'serviceUrl.label', default: 'ServiceUrl'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
