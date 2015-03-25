<%@page import="java.lang.Boolean"%>
<%@ page import="org.zenboot.portal.processing.ExecutionZoneType"%>
<div class="control-group fieldcontain ${hasErrors(bean: executionZoneTypeInstance, field: 'name', 'error')} ">
	<label class="control-label" for="enabled">
		<g:message code="executionZoneType.enabled.label" default="Enabled" />
	</label>
	<div class="controls">
		<g:select name="enabled" from="${[Boolean.FALSE, Boolean.TRUE]}" value="${executionZoneTypeInstance.enabled}" disabled="true" />
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneTypeInstance, field: 'name', 'error')} ">
	<label class="control-label" for="name">
		<g:message code="executionZoneType.name.label" default="Name" />
	</label>
	<div class="controls">
		<g:textField name="name" value="${executionZoneTypeInstance?.name}" />
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneTypeInstance, field: 'description', 'error')} ">
	<label class="control-label" for="description">
		<g:message code="executionZoneType.description.label" default="Description" />
	</label>
	<div class="controls">
		<g:textField name="description" value="${executionZoneTypeInstance?.description}" readonly="true"/>
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneTypeInstance, field: 'name', 'error')} ">
	<label class="control-label" for="devMode">
		<g:message code="executionZoneType.devMode.label" default="DevMode" />
	</label>
	<div class="controls">
		<g:select name="devMode" from="${[Boolean.FALSE, Boolean.TRUE]}" value="${executionZoneTypeInstance.devMode}" />
	</div>
</div>
