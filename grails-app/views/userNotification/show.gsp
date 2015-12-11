<%@ page import="org.zenboot.portal.UserNotification"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'userNotification.label', default: 'userNotification')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="show-userNotification" class="content scaffold-show" role="main">
		<h2 class="page-header">
			<g:message code="default.show.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<g:link action="list">
			<i class="icon-list"></i>
			<g:message code="default.button.list.label" default="Back to overview" />
		</g:link>

		<dl class="dl-horizontal">
			<dt>
				<g:message code="userNotification.enabled.label" default="Enabled" />
			</dt>
			<dd>
				<g:fieldValue bean="${userNotificationInstance}" field="enabled" />
			</dd>


			<dt>
				<g:message code="userNotification.message.label" default="Message" />
			</dt>
			<dd>
				<g:fieldValue bean="${userNotificationInstance}" field="message" />
			</dd>

			<g:if test="${auditLogEvents.size()>0}">
				<dt>
					<g:message code="host.scriptletBatches.label" default="auditLogEvents" />
				</dt>
				<dd class="collapsable-list">
					<a class="collapsed" style="cursor: pointer">
						<g:message code="auditLogEvents.size()" default="{0} auditLogEvents" args="[auditLogEvents.size()]" />
						<i class="icon-resize-full"></i>
					</a>
					<ul class="unstyled hide">
						<g:render contextPath="/auditLogEvent" template="list" model="['auditLogEventInstanceList': auditLogEvents,'auditLogEventInstanceTotal':auditLogEvents.size()]" />
					</ul>
				</dd>
			</g:if>



		</dl>


		<g:form>
			<fieldset class="spacer buttons">
				<g:hiddenField name="id" value="${userNotificationInstance?.id}" />
				<g:link class="btn btn-primary" action="edit" id="${userNotificationInstance?.id}">
					<g:message code="default.button.edit.label" default="Edit" />
				</g:link>
			</fieldset>
		</g:form>
	</div>
</body>
</html>
