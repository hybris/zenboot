package org.zenboot.portal

import org.springframework.dao.DataIntegrityViolationException

class UserNotificationController {


    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [userNotificationInstanceList: UserNotification.list(params), userNotificationInstanceTotal: UserNotification.count()]
    }

    def show() {
        def userNotificationInstance = UserNotification.get(params.id)
        if (!userNotificationInstance) {
          flash.message = message(code: 'default.not.found.message', args: [message(code: 'UserNotification.label', default: 'UserNotification'), params.id])
          redirect(action: "list")
          return
        }
        def auditLogEvents = AuditLogEvent.findAllByClassNameAndPersistedObjectId("UserNotification",params.id,[sort:"persistedObjectVersion",order: "desc"])

        [userNotificationInstance: userNotificationInstance,auditLogEvents: auditLogEvents]
    }

    def create() {
        [userNotificationInstance: new UserNotification(params)]
    }


    def save() {
        def userNotificationInstance = new UserNotification(params)
        if (!userNotificationInstance.save(flush: true)) {
            render(view: "create", model: [userNotificationInstance: userNotificationInstance])
            return
        }

		    flash.message = message(code: 'default.created.message', args: [message(code: 'userNotification.label', default: 'UserNotification'), userNotificationInstance.id])
        redirect(action: "show", id: userNotificationInstance.id)
    }

    def edit() {
        def userNotificationInstance = UserNotification.get(params.id)
        if (!userNotificationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'UserNotification.label', default: 'UserNotification'), params.id])
            redirect(action: "list")
            return
        }

        [userNotificationInstance: userNotificationInstance]
    }

    def update() {
        def userNotificationInstance = UserNotification.get(params.id)
        if (!userNotificationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'UserNotification.label', default: 'UserNotification'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (userNotificationInstance.version > version) {
                userNotificationInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'userNotificationInstance.label', default: 'UserNotification')] as Object[],
                          "Another user has updated this UserNotification while you were editing")
                render(view: "edit", model: [userNotificationInstance: userNotificationInstance])
                return
            }
        }

        userNotificationInstance.properties = params

        if (!userNotificationInstance.save(flush: true)) {
            render(view: "edit", model: [userNotificationInstance: userNotificationInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'UserNotification.label', default: 'UserNotification'), userNotificationInstance.id])
        redirect(action: "show", id: userNotificationInstance.id)
    }

    def delete() {
        def userNotificationInstance = UserNotification.get(params.id)
        if (!userNotificationInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'UserNotification.label', default: 'UserNotification'), params.id])
            redirect(action: "list")
            return
        }

        try {
            userNotificationInstance.delete(flush: true)
      flash.message = message(code: 'default.deleted.message', args: [message(code: 'UserNotification.label', default: 'UserNotification'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
      flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'UserNotification.label', default: 'UserNotification'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
