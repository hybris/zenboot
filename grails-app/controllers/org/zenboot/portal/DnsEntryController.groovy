package org.zenboot.portal

import org.springframework.dao.DataIntegrityViolationException

class DnsEntryController {

    static allowedMethods = [delete: "POST"]
    
    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [dnsEntryInstanceList: DnsEntry.list(params), dnsEntryInstanceTotal: DnsEntry.count()]
    }

    def show() {
        def dnsEntryInstance = DnsEntry.get(params.id)
        if (!dnsEntryInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'dnsEntry.label', default: 'DnsEntry'), params.id])
            redirect(action: "list")
            return
        }

        [dnsEntryInstance: dnsEntryInstance]
    }


    def delete() {
        def dnsEntryInstance = DnsEntry.get(params.id)
        if (!dnsEntryInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'dnsEntry.label', default: 'DnsEntry'), params.id])
            redirect(action: "list")
            return
        }

        try {
            dnsEntryInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'dnsEntry.label', default: 'DnsEntry'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'dnsEntry.label', default: 'DnsEntry'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
