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
