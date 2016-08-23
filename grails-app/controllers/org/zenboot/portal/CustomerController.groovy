package org.zenboot.portal

import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.zenboot.portal.security.Role

class CustomerController extends AbstractRestController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", rest: "GET"]

    def rest = {
        Customer customer = Customer.findByEmail(params.id)

        if (!customer) {
            this.renderRestResult(HttpStatus.NOT_FOUND)
            return
        }

        switch (params.property) {
            case null:
            case 'password':
                if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
                    this.renderRestResult(HttpStatus.OK, customer)
                } else {
                    this.renderRestResult(HttpStatus.FORBIDDEN, null, null, "Accessible only by administrators")
                }
            break
            default:
                this.renderRestResult(HttpStatus.OK, customer[params.property])
            break
        }

        return
    }

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [customerInstanceList: Customer.list(params), customerInstanceTotal: Customer.count()]
    }

    def create() {
        [customerInstance: new Customer(params)]
    }

    def save() {
        def customerInstance = new Customer(params)
        if (!customerInstance.save(flush: true)) {
            render(view: "create", model: [customerInstance: customerInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'customer.label', default: 'Customer'), customerInstance.id])
        redirect(action: "show", id: customerInstance.id)
    }

    def show() {
        def customerInstance = Customer.get(params.id)
        if (!customerInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect(action: "list")
            return
        }

        [customerInstance: customerInstance]
    }

    def edit() {
        def customerInstance = Customer.get(params.id)
        if (!customerInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect(action: "list")
            return
        }

        [customerInstance: customerInstance]
    }

    def update() {
        def customerInstance = Customer.get(params.id)
        if (!customerInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (customerInstance.version > version) {
                customerInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                [message(code: 'customer.label', default: 'Customer')] as Object[],
                "Another user has updated this Customer while you were editing")
                render(view: "edit", model: [customerInstance: customerInstance])
                return
            }
        }

        customerInstance.properties = params

        if (!customerInstance.save(flush: true)) {
            render(view: "edit", model: [customerInstance: customerInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'customer.label', default: 'Customer'), customerInstance.id])
        redirect(action: "show", id: customerInstance.id)
    }

    def delete() {
        def customerInstance = Customer.get(params.id)
        if (!customerInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect(action: "list")
            return
        }

        try {
            customerInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
