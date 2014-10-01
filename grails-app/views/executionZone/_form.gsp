<%@ page import="org.zenboot.portal.processing.ExecutionZone"%>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'type', 'error')} required">
	<label class="control-label" for="type">
		<g:message code="executionZone.type.label" default="Type" />
	</label>
	<div class="controls">
		<g:select id="type" name="type.id" from="${org.zenboot.portal.processing.ExecutionZoneType.list()}" optionKey="id" required="" value="${executionZoneInstance?.type?.id}" class="many-to-one" />
		<span class="required-indicator">*</span>
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'actions', 'error')} ">
	<label class="control-label" for="actions">
		<g:message code="executionZone.actions.label" default="Actions" />
	</label>
	<div class="controls collapsable-list">
		<a class="collapsed" style="cursor: pointer">
			<g:message code="executionZone.actions.size" default="{0} actions defined" args="[executionZoneInstance.actions.size()]" />
			<i class="icon-resize-full"></i>
		</a>
		<ul class="unstyled hide">
			<g:each in="${executionZoneInstance?.actions?}" var="a">
				<li>
					<g:link controller="executionZoneAction" action="show" id="${a.id}">
						${a?.encodeAsHTML()}
					</g:link>
				</li>
			</g:each>
		</ul>
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'puppetEnvironment', 'error')} ">
    <label class="control-label" for="puppetEnvironment">
        <g:message code="executionZone.puppetEnvironment.label" default="Puppet-Environment" />
    </label>
    <div class="controls">
        <g:textField name="puppetEnvironment" value="${executionZoneInstance?.puppetEnvironment}" />
        <br/>
        <small><g:message code="executionZone.puppetenvironment.comment" default="Will become part of the REST-url" /></small>
    </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'qualityStage', 'error')} ">
    <label class="control-label" for="qualityStage">
        <g:message code="executionZone.qualityStage.label" default="Quality-Stage" />
    </label>
    <div class="controls">
        <g:textField name="qualityStage" value="${executionZoneInstance?.qualityStage}" />
        <br/>
        <small><g:message code="executionZone.qualityStage.comment" default="Will become part of the REST-url" /></small>
    </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'description', 'error')} ">
    <label class="control-label" for="description">
        <g:message code="executionZone.description.label" default="Description" />
    </label>
    <div class="controls">
        <g:textField name="description" value="${executionZoneInstance?.description}" />
    </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'enabled', 'error')} ">
    <label class="control-label" for="enabled">
        <g:message code="executionZone.enabled.label" default="Enabled" />
    </label>
    <div class="controls">
        <g:checkBox name="enabled" value="${true}" checked="${executionZoneInstance?.enabled}" />
    </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'enableExposedProcessingParameters', 'error')} ">
	<label class="control-label" for="enableExposedProcessingParameters">
		<g:message code="executionZone.enableExposedProcessingParameters.label" default="Support exposed parameters" />
	</label>
	<div class="controls">
		<g:checkBox name="enableExposedProcessingParameters" value="${Boolean.TRUE}" checked="${executionZoneInstance?.enableExposedProcessingParameters}" />
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'hostLimit', 'error')} ">
		<label class="control-label" for="hostLimit">
				<g:message code="executionZone.hostLimit.label" default="Host Limit" />
		</label>
		<div class="controls">
				<g:textField name="hostLimit" value="${executionZoneInstance?.hostLimit}" />
		</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'enableAutodeletion', 'error')} ">
	<label class="control-label" for="enableAutodeletion">
		<g:message code="executionZone.enableAutodeletion.label" default="Enable Autodeletion" />
	</label>
	<div class="controls">
		<g:checkBox name="enableAutodeletion" value="${Boolean.FALSE}" checked="${executionZoneInstance?.enableAutodeletion}" />
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'defaultLifetime', 'error')} ">
		<label class="control-label" for="defaultLifetime">
				<g:message code="executionZone.defaultLifetime.label" default="Default Lifetime (mins)" />
		</label>
		<div class="controls">
				<g:textField name="defaultLifetime" value="${executionZoneInstance?.defaultLifetime}" />
				1 day = 1440 , 1 week = 10080 , 1 month = 40320, 6 month = 241929
		</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'processingParameters', 'error')} ">
	<label class="control-label" for="parameters">
		<g:message code="executionZone.parameters.label" default="Parameters" />
	</label>
	<div class="controls">
		<g:render template="showParameters" model="[parameters:executionZoneInstance.processingParameters]" />
	</div>
</div>
