package org.zenboot.portal.processing

import grails.validation.Validateable
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.ControllerUtils
import org.zenboot.portal.processing.meta.MetadataParameterComparator
import org.zenboot.portal.processing.meta.ParameterMetadata
import org.zenboot.portal.security.Role


class ExposedExecutionZoneActionController extends AbstractRestController implements ApplicationEventPublisherAware {

    def executionZoneService
    def applicationEventPublisher
    def grailsLinkGenerator
    def springSecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", rest: "POST"]

    def rest = {
        if (!params.url) {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, "Path not found")
            return
        }
        ExposedExecutionZoneAction exposedAction = ExposedExecutionZoneAction.findByUrl(params.url)
        //resolve exposed action
        if (!exposedAction) {
            this.renderRestResult(HttpStatus.NOT_FOUND)
            return
        }

        //check user roles
        if (!SpringSecurityUtils.ifAnyGranted(exposedAction.roles*.authority.join(','))) {
            this.renderRestResult(HttpStatus.FORBIDDEN)
            return
        }

        //check that all parameters are satisfied
        def actionParameters = executionZoneService.resolveExposedExecutionZoneActionParameters(exposedAction, params)
        if (!actionParameters.missingParameters.empty) {
            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, "Mandatory parameters are missing: ${actionParameters.missingParameters.join(', ')}")
            return
        }

        ExecutionZoneAction action = executionZoneService.createExecutionZoneAction(exposedAction, actionParameters.resolvedParameters)

        this.applicationEventPublisher.publishEvent(new ProcessingEvent(action, springSecurityService.currentUser))

        URI referral = new URI(this.grailsLinkGenerator.link(absolute:true, controller:'executionZoneAction', action:'rest', params:[id:action.id]))
        this.renderRestResult(HttpStatus.CREATED, null, referral)
    }

    def execute = { ExecuteExposedExecutionZoneActionCommand cmd ->
        cmd.parameters = params.parameters
        def resolvedParams = cmd.executionZoneService.resolveExposedExecutionZoneActionParameters(ExposedExecutionZoneAction.get(params.actionId), ControllerUtils.getParameterMap(params))
        cmd.exposedExecutionZoneActionParameters = resolvedParams.resolvedParameters
        resolvedParams.missingParameters.each { paramName ->
           cmd.errors.reject('executionZone.parameters.emptyValue', [paramName].asType(Object[]), 'Mandatory parameter is empty')
        }

        if (cmd.hasErrors()) {
            chain(action:'show', id:cmd.actionId, model:[cmd:cmd])
            return
        } else {
            ExecutionZoneAction action = cmd.getExecutionZoneAction()
            this.applicationEventPublisher.publishEvent(new ProcessingEvent(action, springSecurityService.currentUser))
            flash.message = message(code: 'default.created.message', args: [message(code: 'executionZoneAction.label', default: 'ExecutionZoneAction'), action.id])
        }
        redirect(action:"show", id:cmd.actionId)
    }

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        def executionZones
        def parameters = [:]
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        if (!params.sort) {
            params.sort = "creationDate"
        }
        if (!params.order) {
            params.order = "desc"
        }
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            executionZones = ExposedExecutionZoneAction.list(params)
        } else {
            executionZones = ExposedExecutionZoneAction.createCriteria().list(params) {
                roles {
                    or {
                        springSecurityService.currentUser.getAuthorities()*.authority.each { auth ->
                            eq('authority', auth)
                        }
                    }

                }
            }
        }
        [exposedExecutionZoneActionInstanceList: executionZones, exposedExecutionZoneActionInstanceTotal: ExposedExecutionZoneAction.count(), parameters: parameters]
    }

    def create() {
        //this method can be called in a chain from ExecutionZoneController.createExposedAction()
        [exposedExecutionZoneActionInstance: chainModel?.exposedExecutionZoneActionInstance ?: new ExposedExecutionZoneAction()]
    }

    def save = { SaveExposedExecutionZoneActionCommand cmd ->
        executionZoneService.setParameters(cmd, params.parameters)
        if (cmd.hasErrors()) {
            render(view:"create", model: [cmd:cmd, exposedExecutionZoneActionInstance:cmd.executionZoneAction])
            return
        }
        def exposedExecutionZoneActionInstance = cmd.executionZoneAction
        if (!exposedExecutionZoneActionInstance.save(flush: true)) {
            render(view: "create", model: [exposedExecutionZoneActionInstance: exposedExecutionZoneActionInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction'), exposedExecutionZoneActionInstance.id])
        redirect(action: "show", id: exposedExecutionZoneActionInstance.id)
    }

    def show() {
        def exposedExecutionZoneActionInstance = ExposedExecutionZoneAction.get(params.id)

        if (!exposedExecutionZoneActionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction'), params.id])
            redirect(action: "list")
            return
        }

        def metadataParams = executionZoneService.getExposedExecutionZoneActionParameters(exposedExecutionZoneActionInstance)

        [
            exposedExecutionZoneActionInstance: exposedExecutionZoneActionInstance,
            exposedExecutionZoneActionParameters:metadataParams.sort(new MetadataParameterComparator()),
            containsInvisibleParameters: metadataParams.any { ParameterMetadata metadataParam ->
                !metadataParam.visible
            }
        ]
    }

    def edit() {
        def exposedExecutionZoneActionInstance = ExposedExecutionZoneAction.get(params.id)
        if (!exposedExecutionZoneActionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction'), params.id])
            redirect(action: "list")
            return
        }

        [exposedExecutionZoneActionInstance: exposedExecutionZoneActionInstance]
    }

    def update = { UpdateExposedExecutionZoneActionCommand cmd ->
        executionZoneService.setParameters(cmd, params.parameters)
        cmd.params = params
        if (cmd.hasErrors()) {
            render(view:"edit", model: [cmd:cmd, exposedExecutionZoneActionInstance:cmd.executionZoneAction])
            return
        }

        def exposedExecutionZoneActionInstance = cmd.getExecutionZoneAction()

        if (!exposedExecutionZoneActionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction'), params.actionId])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (exposedExecutionZoneActionInstance.version > version) {
                exposedExecutionZoneActionInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction')] as Object[],
                "Another user has updated this ExposedExecutionZoneAction while you were editing")
                render(view: "edit", model: [exposedExecutionZoneActionInstance: exposedExecutionZoneActionInstance])
                return
            }
        }

        if (!exposedExecutionZoneActionInstance.save(flush: true)) {
            render(view: "edit", model: [exposedExecutionZoneActionInstance: exposedExecutionZoneActionInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction'), exposedExecutionZoneActionInstance.id])
        redirect(action: "show", id: exposedExecutionZoneActionInstance.id)
    }

    def delete() {
        def exposedExecutionZoneActionInstance = ExposedExecutionZoneAction.get(params.actionId)
        if (!exposedExecutionZoneActionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction'), params.actionId])
            redirect(action: "list")
            return
        }

        try {
            exposedExecutionZoneActionInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction'), params.actionId])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction'), params.actionId])
            redirect(action: "show", id: params.actionId)
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.applicationEventPublisher = eventPublisher
    }
}

class SaveExposedExecutionZoneActionCommand extends AbstractExecutionZoneCommand {

    def grailsLinkGenerator
    def params

    String url
    String cronExpression
    Set roles = []

    boolean validate() {
        boolean result = true
        if (roles.empty) {
            this.getErrors().reject('exposedExecutionZoneAction.create.rolesMissing', 'Roles missing')
            result = false
        }
        if (!url) {
            this.getErrors().reject('exposedExecutionZoneAction.create.urlMissing', 'URL is not defined')
            result = false
        }
        if (!ExecutionZone.get(this.execId)) {
            this.getErrors().reject('exposedExecutionZoneAction.create.zoneUnknown', 'ExecutionZone is unknown')
            result = false
        }
        try {
            URI absoluteUrl = new URI(this.grailsLinkGenerator.link(absolute:true, controller:'exposedExecutionZoneAction', uri: '/' + this.url))
        } catch (URISyntaxException exc) {
            this.getErrors().reject('exposedExecutionZoneAction.create.urlInvalid', [url].asType(Object[]), 'URL is invalid')
            result = false
        }
        return result
    }

    @Override
    ExposedExecutionZoneAction getExecutionZoneAction() {
        ExposedExecutionZoneAction exposedExcZnActn = new ExposedExecutionZoneAction(
            executionZone: ExecutionZone.get(this.execId),
            scriptDir: this.scriptDir,
            url:this.url,
            cronExpression:this.cronExpression
        )
        ControllerUtils.synchronizeProcessingParameterValues(this.execZoneParameters, exposedExcZnActn)
        roles.each {
            exposedExcZnActn.roles.add(Role.get(it))
        }
        return exposedExcZnActn
    }
}

class UpdateExposedExecutionZoneActionCommand extends SaveExposedExecutionZoneActionCommand {
    Long actionId

    @Override
    ExposedExecutionZoneAction getExecutionZoneAction() {
        ExposedExecutionZoneAction exposedExcZnActn = ExposedExecutionZoneAction.get(actionId)
        exposedExcZnActn.url = this.url
        exposedExcZnActn.cronExpression = this.cronExpression
        //set params
        ControllerUtils.synchronizeProcessingParameterValues(ControllerUtils.getParameterMap(params), exposedExcZnActn)
        //set roles
        exposedExcZnActn.roles.clear()
        roles.each {
            exposedExcZnActn.roles.add(Role.get(it))
        }
        return exposedExcZnActn
    }
}

class ExecuteExposedExecutionZoneActionCommand {

    def executionZoneService

    Long actionId
    Map exposedExecutionZoneActionParameters
    Map parameters

    static constraints = {
        actionId nullable:false
    }

    /*boolean validate() {
        def resolvedParams = executionZoneService.resolveExposedExecutionZoneActionParameters(ExposedExecutionZoneAction.get(this.actionId), ControllerUtils.getParameterMap(params))
        this.exposedExecutionZoneActionParameters = resolvedParams.resolvedParameters
        resolvedParams.missingParameters.each { paramName ->
            this.errors.reject('executionZone.parameters.emptyValue', [paramName].asType(Object[]), 'Mandatory parameter is empty')
        }
        return this.errors.hasErrors()
    }*/

    ExecutionZoneAction getExecutionZoneAction() {
        ExposedExecutionZoneAction exposedAction = ExposedExecutionZoneAction.get(this.actionId)
        return executionZoneService.createExecutionZoneAction(exposedAction, this.exposedExecutionZoneActionParameters)
    }
}
