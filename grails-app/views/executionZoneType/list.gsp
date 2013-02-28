<%@ page import="org.zenboot.portal.processing.ExecutionZoneType"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'executionZoneType.label', default: 'ExecutionZoneType')}" />
<title>
	<g:message code="default.list.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="list-executionZoneType" class="content scaffold-list" role="main">
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
					<g:sortableColumn property="name" title="${message(code: 'executionZoneType.name.label', default: 'Name')}" />
					<g:sortableColumn property="description" title="${message(code: 'executionZoneType.description.label', default: 'Description')}" />
					<g:sortableColumn property="enabled" title="${message(code: 'executionZoneType.enabled.label', default: 'Enabled')}" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${executionZoneTypeInstanceList}" status="i" var="executionZoneTypeInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>
							<g:link action="show" id="${executionZoneTypeInstance.id}">
								${fieldValue(bean: executionZoneTypeInstance, field: "name")}
							</g:link>
						</td>
						<td>
							${fieldValue(bean: executionZoneTypeInstance, field: "description")}
						</td>
						<td>
							<g:if test="${executionZoneTypeInstance.enabled}">
								<i class="icon-ok"></i>
							</g:if>
							<g:else>
								<i class="icon-remove"></i></</g:else>
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>

		<g:link class="btn btn-primary" action="updateTypes">
			<i class="icon-refresh icon-white"></i>
			<g:message code="default.button.synchronize.label" default="Synchronisieren" />
		</g:link>

		<div class="pagination">
			<g:paginate total="${executionZoneTypeInstanceTotal}" />
		</div>
	</div>
</body>
</html>