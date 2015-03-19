<%@ page import="org.zenboot.portal.processing.ExecutionZone"%>

<div class="control-group fieldcontain ${hasErrors(bean: executionZoneInstance, field: 'processingParameters', 'error')} ">
  <label class="control-label" for="parameters">
    <g:message code="executionZone.parameters.label" default="Parameters" />
  </label>
  <div class="controls">
    <g:render template="showParameters" model="[parameters:userEditableFilteredParameters]" />
  </div>
</div>
