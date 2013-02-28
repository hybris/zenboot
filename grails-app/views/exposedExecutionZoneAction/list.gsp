<%@ page import="org.zenboot.portal.processing.ExposedExecutionZoneAction"%>
<%@ page import="org.zenboot.portal.security.Role"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction')}" />
<title>
	<g:message code="default.list.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="list-exposedExecutionZoneAction" class="content scaffold-list" role="main">
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
					<g:sortableColumn property="scriptDir" title="${message(code: 'exposedExecutionZoneAction.scriptDir.label', default: 'Script Dir')}" />
					<g:sortableColumn property="creationDate" title="${message(code: 'exposedExecutionZoneAction.creationDate.label', default: 'Creation Date')}" />
					<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
						<th style="width: 28%;">
							<g:message code="exposedExecutionZoneAction.parameters.label" default="Parameters" />
						</th>
					</sec:ifAllGranted>
					<g:sortableColumn property="url" title="${message(code: 'exposedExecutionZoneAction.url.label', default: 'Url')}" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${exposedExecutionZoneActionInstanceList}" status="i" var="exposedExecutionZoneActionInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>
							<g:link action="show" id="${exposedExecutionZoneActionInstance.id}">
								${fieldValue(bean: exposedExecutionZoneActionInstance, field: "scriptDir.name")}
							</g:link>
						</td>
						<td>
							<g:formatDate date="${exposedExecutionZoneActionInstance.creationDate}" />
						</td>
						<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
							<td>
								<g:render template="/executionZone/parametersInList" model="[parameters:exposedExecutionZoneActionInstance.processingParameters]"></g:render>
							</td>
						</sec:ifAllGranted>
						<td>
							<g:createLink uri="/rest/${fieldValue(bean: exposedExecutionZoneActionInstance, field: "url")}" absolute="true" />
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>

		<div class="pagination">
			<g:paginate total="${exposedExecutionZoneActionInstanceTotal}" />
		</div>
	</div>
</body>
</html>