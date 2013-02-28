package org.zenboot.portal.processing

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController


class ExecutionZoneActionController extends AbstractRestController {

    static allowedMethods = [delete: "POST", rest: "GET"]

    def rest = {
        ExecutionZoneAction action = ExecutionZoneAction.get(params.id)
        if (!action) {
            this.renderRestResult(HttpStatus.NOT_FOUND)
            return
        }

        ScriptletBatch batch = ScriptletBatch.findByExecutionZoneAction(action)
        if (!batch) {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, "Not ${ScriptletBatch.class.simpleName} exists for this action")
            return
        }

        request.withFormat {
            xml {
                render (contentType:"text/xml"){
                    status {
                        startDate batch.startDate
                        endDate batch.startDate
                        description batch.description
                        scriptletBatch(state:batch.state, progress:batch.progress, errorClass:batch.exceptionClass, errorMessage:batch.exceptionMessage, size:batch.countProcessables()) {
                            batch.processables.each {
                                processable(description:it.description, state:it.state, processTime:it.processTime)
                            }
                        }
                    }
                }
             }
            json {
                render (contentType:"text/json"){
                    def scriptletBatch = [state:batch.state.name(), progress:batch.progress, size:batch.countProcessables(), errorClass:batch.exceptionClass, errorMessage:batch.exceptionMessage, proessables:array {
                        batch.processables.each {
                            processable(description:it.description, state:it.state.name(), processTime:it.processTime)
                        }
                    }]
                    status startDate:batch.startDate, endDate:batch.startDate,  description:batch.description,  scriptletBatch: scriptletBatch
                }
            }
        }

        return
    }

    def show() {
        def executionZoneActionInstance = ExecutionZoneAction.get(params.id)
        if (!executionZoneActionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZoneAction.label', default: 'ExecutionZoneAction'), params.id])
            redirect(action: "list")
            return
        }

        [executionZoneActionInstance: executionZoneActionInstance]
    }

    def delete() {
        def executionZoneActionInstance = ExecutionZoneAction.get(params.id)
        if (!executionZoneActionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZoneAction.label', default: 'ExecutionZoneAction'), params.id])
            redirect(action: "list")
            return
        }

        try {
            executionZoneActionInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'executionZoneAction.label', default: 'ExecutionZoneAction'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'executionZoneAction.label', default: 'ExecutionZoneAction'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
