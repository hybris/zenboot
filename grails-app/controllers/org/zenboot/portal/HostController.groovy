package org.zenboot.portal

import grails.plugin.springsecurity.annotation.Secured
import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus

class HostController extends AbstractRestController {

    def filterPaneService

    static allowedMethods = [update: "POST", delete: "POST", markHostBroken: "POST", markHostUnknown: "POST"]
    def executionZoneService

    def rest = {
        Host host = Host.findById(params.id)

        if (!host) {
            this.renderRestResult(HttpStatus.NOT_FOUND)
            return
        }
        this.renderRestResult(HttpStatus.OK, host)
        return
    }


    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def parameters = params.findAll { it.value instanceof String }

        [
                hostInstanceList : filterPaneService.filter(params, Host),
                hostInstanceTotal: filterPaneService.count(params, Host),
                parameters       : parameters,
                filterParams     : FilterPaneUtils.extractFilterParams(params),
                params           : params,
        ]
    }

    def show() {
        def hostInstance = Host.get(params.id)
        if (!hostInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'host.label', default: 'Host'), params.id])
            redirect(action: "list")
            return
        }

        def auditLogEvents = AuditLogEvent.findAllByClassNameAndPersistedObjectId("Host",params.id,[sort:"persistedObjectVersion",order: "desc"])

        [hostInstance: hostInstance,
         auditLogEvents: auditLogEvents]
    }

    def edit() {
        def hostInstance = Host.get(params.id)
        if (!hostInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'host.label', default: 'Host'), params.id])
            redirect(action: "list")
            return
        }

        [hostInstance: hostInstance]
    }

    def update() {
        def hostInstance = Host.get(params.id)
        if (!hostInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'host.label', default: 'Host'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (hostInstance.version > version) {
                hostInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'host.label', default: 'Host')] as Object[],
                          "Another user has updated this Host while you were editing")
                render(view: "edit", model: [hostInstance: hostInstance])
                return
            }
        }

        hostInstance.properties = params

        if (!hostInstance.save(flush: true)) {
            render(view: "edit", model: [hostInstance: hostInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'host.label', default: 'Host'), hostInstance.id])
        redirect(action: "show", id: hostInstance.id)
    }

    def delete() {
        def hostInstance = Host.get(params.id)
        if (!hostInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'host.label', default: 'Host'), params.id])
            redirect(action: "list")
            return
        }
        if (Boolean.valueOf(params.deleteEntity)) {
            try {
                hostInstance.delete(flush: true)
    			flash.message = message(code: 'default.deleted.message', args: [message(code: 'host.label', default: 'Host'), params.id])
                redirect(action: "list")
            }
            catch (DataIntegrityViolationException e) {
    			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'host.label', default: 'Host'), params.id])
                redirect(action: "show", id: params.id)
            }
        } else {
            hostInstance.state = HostState.DELETED;
            hostInstance.save(flush:true)
            redirect(action: "list")
        }
    }

    @Secured(['ROLE_USER'])
    def markHostBroken() {
        markHost(HostState.BROKEN)
    }

    @Secured(['ROLE_USER'])
    def markHostUnknown() {
        markHost(HostState.UNKNOWN)
    }

    private def markHost(HostState state) {
        Host hostInstance = Host.get(params.id)

        def executionZoneInstance = hostInstance.execZone

        if (!executionZoneService.userHasAccess(executionZoneInstance)) {
            return render(view: "/login/denied")
        }

        hostInstance.state = state;
        hostInstance.save(flush:true, failOnError: true)

        redirect(action: "show", id: params.id)
    }

}
