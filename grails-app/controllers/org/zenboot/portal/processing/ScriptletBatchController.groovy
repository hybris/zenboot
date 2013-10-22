package org.zenboot.portal.processing

import grails.converters.JSON
import grails.gsp.PageRenderer

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus

class ScriptletBatchController {

    def PageRenderer groovyPageRenderer
    
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 15, 100)
        if (!params.sort) {
            params.sort = "creationDate"
        }
        if (!params.order) {
            params.order = "desc"
        }
        
        
        def batchInstanceList
        def batchInstanceCount
        def parameters = [:]
        if (params.execId) {
          def executionZoneInstance = ExecutionZone.get(params.execId)
          if (!executionZoneInstance) {
              flash.message = message(code: 'default.not.found.message', args: [message(code: 'executionZone.label', default: 'ExecutionZone'), params.execId])
              redirect(action: "list")
              return
          }
            parameters.execId = params.execId
            batchInstanceList = ScriptletBatch.findAllByExecutionZoneActionInList(executionZoneInstance.actions, params)
            batchInstanceCount = ScriptletBatch.findAllByExecutionZoneActionInList(executionZoneInstance.actions).size()
        } else {
            batchInstanceList = ScriptletBatch.list(params)
            batchInstanceCount = ScriptletBatch.count()
        }
        
        
        [scriptletBatchInstanceList: batchInstanceList, scriptletBatchInstanceTotal: batchInstanceCount, parameters:parameters]
    }
    
    def ajaxList() {
        params.max = 6
        params.sort = 'id'
        params.order = 'desc'
        params.offset = 0

        def result = ScriptletBatch.list(params)
                
        request.withFormat {
            json {
                def output = [queue:[]]
                for (q in result) {
                    output.queue << [
                        creationDate : q.creationDate,
                        description:q.description,
                        state:q.state.name(),
                        progress: q.getProgress()
                    ]
                }
                render output as JSON
            }
            html {
                [scriptletBatchInstanceList:result, scriptletBatchInstanceTotal:ScriptletBatch.count()]
            }
        }
    }

    def ajaxSteps = { GetScriptletBatchStepsCommand cmd ->
        if (cmd.hasErrors()) {
            return render(view:"/ajaxError", model:[result:cmd])
        }

        ScriptletBatch batch = cmd.getScriptletBatch()
        if (batch.isRunning()) {
            response.setStatus(HttpStatus.OK.value())
            request.withFormat {
                json {
                    def  result = []
                    batch.processables.each { Processable proc ->
                        result << [
                            markup: this.getScriptletBatchStepMarkup(proc),
                            status: proc.state.name()
                        ]
                    }
                    render result as JSON
                }
                html { render(template:'steps', model:[steps:batch.processables]) }
            }
        } else {
            response.setStatus(HttpStatus.GONE.value())
            response.flushBuffer()
        }
    }

	private getScriptletBatchStepMarkup(Processable proc) {
		def writer = new StringWriter()
		groovyPageRenderer.renderTo([template:'/scriptletBatch/steps', model:[steps:proc]], writer)
        return writer.toString()
	}

    def show() {
        def scriptletBatchInstance = ScriptletBatch.get(params.id)
        if (!scriptletBatchInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'scriptletBatch.label', default: 'scriptletBatch'), params.id])
            redirect(action: "list")
            return
        }

        [scriptletBatchInstance: scriptletBatchInstance]
    }

    def delete() {
        def scriptletBatchInstance = ScriptletBatch.get(params.id)
        if (!scriptletBatchInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'scriptletBatch.label', default: 'scriptletBatch'), params.id])
            redirect(action: "list")
            return
        }

        try {
            scriptletBatchInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'scriptletBatch.label', default: 'scriptletBatch'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'scriptletBatch.label', default: 'scriptletBatch'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}

class GetScriptletBatchStepsCommand {

    Long id

    static constraints = {
        id nullable:false, validator: { value, commandObj ->
            ScriptletBatch.get(commandObj.id) != null
        }
    }

    ScriptletBatch getScriptletBatch() {
        return ScriptletBatch.get(this.id)
    }
}
