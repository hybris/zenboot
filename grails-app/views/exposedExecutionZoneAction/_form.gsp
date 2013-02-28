<%@page import="org.zenboot.portal.processing.CronjobExpression"%>
<%@page import="org.zenboot.portal.processing.ExecutionZone"%>
<%@ page import="org.zenboot.portal.processing.ExposedExecutionZoneAction"%>

<div class="control-group fieldcontain ${hasErrors(bean: exposedExecutionZoneActionInstance, field: 'scriptDir', 'error')} required">
	<label class="control-label" for="scriptDir">
		<g:message code="exposedExecutionZoneAction.scriptDir.label" default="Script Dir" />
	</label>
	<div class="controls">
		<g:if test="${exposedExecutionZoneActionInstance?.scriptDir}">
			<g:textField name="scriptDirName" value="${exposedExecutionZoneActionInstance.scriptDir.name}" readonly="true" />
			<g:hiddenField name="scriptDir" value="${exposedExecutionZoneActionInstance.scriptDir}" />
		</g:if>
		<g:else>
			<span class="required-indicator">*</span>
		</g:else>
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: exposedExecutionZoneActionInstance, field: 'cronExpression', 'error')} required">
    <label class="control-label" for="cronExpression">
        <g:message code="exposedExecutionZoneAction.cronExpression.label" default="Cron Expression" />
    </label>
    <div class="controls">
		<g:select name="cronExpression" from="${CronjobExpression}" optionKey="cronExpression" value="${exposedExecutionZoneActionInstance?.cronExpression}" readonly="false" />
		<span class="required-indicator">*</span>
	</div>
</div>

<g:if test="${exposedExecutionZoneActionInstance?.creationDate}">
	<div class="control-group fieldcontain ${hasErrors(bean: exposedExecutionZoneActionInstance, field: 'creationDate', 'error')} ">
		<label class="control-label" for="creationDate">
			<g:message code="exposedExecutionZoneAction.creationDate.label" default="Creation Date" />
		</label>
		<div class="controls">
			<g:datePicker name="creationDate" precision="day" value="${exposedExecutionZoneActionInstance?.creationDate}" default="none" noSelection="['': '']" disabled="true" />
		</div>
	</div>
</g:if>

<div class="control-group fieldcontain ${hasErrors(bean: exposedExecutionZoneActionInstance, field: 'executionZone', 'error')} ">
	<label class="control-label" for="executionZone">
		<g:message code="exposedExecutionZoneAction.executionZone.label" default="Execution Zone" />
	</label>
	<div class="controls">
		<g:select name="executionZone" from="${ExecutionZone.list()}" optionKey="id"  value="${exposedExecutionZoneActionInstance?.executionZone?.id}" readonly="true" style="width:40%"/>
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: exposedExecutionZoneActionInstance, field: 'processingParameters', 'error')} ">
	<label class="control-label" for="parameters">
		<g:message code="exposedExecutionZoneAction.parameters.label" default="Parameters" />
	</label>
	<div class="controls">
		<g:render template="/executionZone/showParameters" model="[parameters:exposedExecutionZoneActionInstance?.processingParameters]" />
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: exposedExecutionZoneActionInstance, field: 'roles', 'error')} ">
	<label class="control-label" for="roles">
		<g:message code="exposedExecutionZoneAction.roles.label" default="Roles" />
	</label>
	<div class="controls">
		<g:select name="roles" from="${org.zenboot.portal.security.Role.list()}" multiple="multiple" optionKey="id" optionValue="authority" size="5" value="${exposedExecutionZoneActionInstance?.roles*.id}" opclass="many-to-many" />
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: exposedExecutionZoneActionInstance, field: 'url', 'error')} ">
	<label class="control-label" for="url">
		<g:message code="exposedExecutionZoneAction.url.label" default="Url" />
	</label>
	<div class="controls">
		<g:createLink controller="exposedExecutionZoneAction" action="rest"  absolute="true" />/<g:textField name="url" value="${exposedExecutionZoneActionInstance?.url}" />
	</div>
</div>