<%@page import="java.lang.Boolean"%>
<%@ page import="org.zenboot.portal.UserNotification"%>

<div class="control-group fieldcontain ${hasErrors(bean: userNotificationInstance, field: 'enabled', 'error')} ">
	<label class="control-label" for="enabled">
		<g:message code="userNotification.enabled.label" default="Enabled" />
	</label>
	<div class="controls">
		<g:select name="enabled" from="${[Boolean.FALSE, Boolean.TRUE]}" value="${userNotificationInstance.enabled}" disabled="false" />
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: userNotificationInstance, field: 'message', 'error')} ">
	<label class="control-label" for="message">
		<g:message code="UserNotification.message.label" default="Message" />
	</label>
	<div class="controls">
		<g:textField name="message" value="${userNotificationInstance?.message}" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'type', 'error')} required">
	<label class="control-label" for="type">
		<g:message code="host.type.label" default="type" />
		<span class="required-indicator">*</span>
	</label>
	<div class="controls">
		<g:select name="type" from="${org.zenboot.portal.NotificationType.values()}" keys="${org.zenboot.portal.NotificationType.values()*.name()}" required="" value="${type?.state?.name()}" readonly="false" />
	</div>
</div>
