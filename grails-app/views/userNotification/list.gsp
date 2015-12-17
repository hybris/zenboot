<%@ page import="org.zenboot.portal.UserNotification"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'userNotification.label', default: 'userNotification')}" />
<title>
	<g:message code="default.list.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="list-userNotification" class="content scaffold-list" role="main">
		<h2 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<table class="table table-striped">
			<thead>
				<tr>
					<g:sortableColumn property="creationDate" title="${message(code: 'userNotification.creationDate.label', default: 'Creation Date')}" />
					<g:sortableColumn property="enabled" title="${message(code: 'userNotification.enabled.label', default: 'Enabled')}" />
					<g:sortableColumn property="name" title="${message(code: 'userNotification.message.label', default: 'Message')}" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${userNotificationInstanceList}" status="i" var="userNotificationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>
							${fieldValue(bean: userNotificationInstance, field: "creationDate")}
						</td>
						<td>
							<g:if test="${userNotificationInstance.enabled}">
								<i class="icon-ok"></i>
							</g:if>
							<g:else>
								<i class="icon-remove"></i></</g:else>
						</td>
						<td>
							<g:link action="show" id="${userNotificationInstance.id}">
								${fieldValue(bean: userNotificationInstance, field: "message")}
							</g:link>
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>

		<g:link class="btn btn-primary" action="create">
			${message(code: 'default.button.create.label', default: 'Cancel')}
		</g:link>

		<div class="pagination">
			<g:paginate total="${userNotificationInstanceTotal}" />
		</div>
	</div>
</body>
</html>
