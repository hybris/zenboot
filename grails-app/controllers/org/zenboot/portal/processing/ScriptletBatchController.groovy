package org.zenboot.portal.processing

import grails.converters.JSON
import grails.gsp.PageRenderer

import grails.plugin.springsecurity.SpringSecurityUtils
import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role


import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus

import static grails.async.Promises.task
import static grails.async.Promises.waitAll

class ScriptletBatchController implements ApplicationEventPublisherAware{

    PageRenderer groovyPageRenderer
    def executionZoneService
    def accessService
    def springSecurityService
    def scriptletBatchService
    def filterPaneService
    def applicationEventPublisher


    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    @Override
    void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.applicationEventPublisher = eventPublisher
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 15, 30)
        if (!params.sort) {
            params.sort = "creationDate"
        }
        if (!params.order) {
            params.order = "desc"
        }

        def batches
        def batchCount
        def parameters = params.findAll { it.value instanceof String }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            def batchCountTask = task(countTaskCreator(params, ScriptletBatch))
            batches = filterPaneService.filter(params, ScriptletBatch)
            waitAll(batchCountTask)
            batchCount = batchCountTask.internalPromise.value
        } else {
            // Filter deactivated because we have atm around 60000 entries in this db column
            // Because for non admin user the page loads every time all entries which needs between 20 sec to 1 min
            // we decided to disable the filter for users. Now we get the allowed scriptletbatches via the execution zones
            // where the user has access. See RPI-2405

            //Past:
            // batches = filterPaneService.filter(params - [max: params.max, offset: params.offset], ScriptletBatch)
            // batches = scriptletBatchService.filterByAccessPermission(batches)

            //Now:
            // If currently logged in user not exists in the cache, refresh cache for this person
            if (!accessService.accessCache[springSecurityService.getCurrentUserId()]) {
                accessService.refreshAccessCacheByUser(Person.findById(springSecurityService.getCurrentUserId()))
            }

            // Get all execution zones where this currently logged in user has access and collect scriptletbatches from executionzoneactions
            def execList = accessService.accessCache[springSecurityService.getCurrentUserId()].findAll { it.value == true}
            batches = new ArrayList<ScriptletBatch>()
            execList.each { key, value ->
                if(checkFilter(params, key)) {
                    Set<ExecutionZoneAction> actions = ExecutionZone.get(key).actions
                    actions.each {batches.addAll(it.scriptletBatches)}
                }
            }

            // Get size for pagination
            batchCount = batches.size()

            // Sort result by sortablecolumn vom list.gsp
            switch (params.sort) {
                case 'user': params.order =='desc'? batches = batches.sort{it.user.displayName ?: it.user.username}.reverse() : batches.sort{it.user.displayName ?: it.user.username}
                    break
                case 'description': params.order =='desc'? batches = batches.sort{it.description}.reverse() : batches.sort{it.description}
                    break
                case 'creationDate': params.order =='desc'? batches = batches.sort{it.creationDate}.reverse() : batches.sort{it.creationDate}
                    break
                case 'endDate': params.order =='desc'? batches = batches.sort{it.endDate}.reverse() : batches.sort{it.endDate}
                    break
                case 'startDate': params.order =='desc'? batches = batches.sort{it.startDate}.reverse() : batches.sort{it.startDate}
                    break
                case 'state': params.order =='desc'? batches = batches.sort{it.state}.reverse() : batches.sort{it.state}
                    break
                case 'executionZoneAction.executionZone': params.order =='desc'? batches = batches.sort{it.executionZoneAction.executionZone}.reverse() : batches.sort{it.executionZoneAction.executionZone}
                    break
                default:
                    params.order =='desc'? batches = batches.sort{it.creationDate}.reverse() : batches.sort{it.creationDate}
                    break
            }
            //use a range do avoid displaying all at once
            batches = scriptletBatchService.getRange(batches, params)
        }

        [
            scriptletBatchInstanceList: batches,
            scriptletBatchInstanceTotal: batchCount,
            filterParams: FilterPaneUtils.extractFilterParams(params),
            parameters: parameters
        ]
    }

    static def checkFilter(params, key){
        return (params.filter == null || params.filter?.executionZoneAction?.executionZone?.id?.toInteger() == key)
    }

    private Closure countTaskCreator(params, ScriptletBatch) {
        return { filterPaneService.count(params, ScriptletBatch) }
    }

    def ajaxList() {
        params.max = 6
        params.sort = 'id'
        params.order = 'desc'
        params.offset = 0

        def criteria = ScriptletBatch.createCriteria()
        def result = criteria.list (max:params.max, offset: params.offset){
            not {
                ilike('description', 'cron%')
            }
            order(params.sort as String, params.order as String)
        }

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
                [scriptletBatchInstanceList:result]
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
            withFormat {
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
        def scriptletBatchInstance = ScriptletBatch.get(params.id as Long)
        if (!scriptletBatchInstance) {
			    flash.message = message(code: 'default.not.found.message', args: [message(code: 'scriptletBatch.label', default: 'scriptletBatch'), params.id])
          redirect(action: "list")
          return
        } else if (!SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
          if (!accessService.userHasAccess(scriptletBatchInstance.executionZoneAction.executionZone)) {
            flash.message = message(code: 'default.no.access.message', args: [message(code: 'scriptletBatch.label', default: 'scriptletBatch'), params.id])
            redirect(action: "list")
            return
          }
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

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {

          try {
              scriptletBatchInstance.delete(flush: true)
  			      flash.message = message(code: 'default.deleted.message', args: [message(code: 'scriptletBatch.label', default: 'scriptletBatch'), params.id])
              redirect(action: "list")
          }
          catch (DataIntegrityViolationException e) {
  			       flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'scriptletBatch.label', default: 'scriptletBatch'), params.id])
              redirect(action: "show", id: params.id)
          }
        } else {
          flash.message = message(code: 'default.not.allowed.message')
          redirect(action: "list")
        }
    }

    def rerun() {
        ScriptletBatch scriptletBatchInstance = ScriptletBatch.get(params.id)
        ExecutionZoneAction reRunAction = scriptletBatchInstance.getExecutionZoneAction()
        Map parameters = [:]
        reRunAction.processingParameters.each {
            parameters[it.name] = it.value
        }

        ExecutionZoneAction newAction = executionZoneService.createExecutionZoneAction(reRunAction.executionZone, reRunAction.scriptDir, parameters)
        applicationEventPublisher.publishEvent(new ProcessingEvent(newAction, springSecurityService.currentUser, "Rerun of previous executed action."))
        redirect(action: "show", params: [id: params.id])
    }
}

class GetScriptletBatchStepsCommand {

    Long scriptletId

    static constraints = {
        scriptletId nullable:false, validator: { value, commandObj ->
            ScriptletBatch.get(commandObj.scriptletId) != null
        }
    }

    ScriptletBatch getScriptletBatch() {
        return ScriptletBatch.get(this.scriptletId)
    }
}
