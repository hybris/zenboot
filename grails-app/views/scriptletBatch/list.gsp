<%@ page import="org.zenboot.portal.processing.ScriptletBatch"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'scriptletBatch.label', default: 'ScriptletBatch')}" />
<title>
	<g:message code="default.list.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="list-scriptletBatch" class="content scaffold-list" role="main">
		<h2 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<table class="table table-striped" id="scriptlet-batch-list">
			<thead>
				<tr>
					<g:sortableColumn property="description" title="${message(code: 'scriptletBatch.description.label', default: 'Description')}" />
					<g:sortableColumn property="creationDate" title="${message(code: 'scriptletBatch.creationDate.label', default: 'Creation Date')}" />
					<g:sortableColumn property="endDate" title="${message(code: 'scriptletBatch.endDate.label', default: 'End Date')}" />
					<g:sortableColumn property="startDate" title="${message(code: 'scriptletBatch.startDate.label', default: 'Start Date')}" />
					<g:sortableColumn property="state" title="${message(code: 'scriptletBatch.state.label', default: 'State')}" />
					<g:sortableColumn property="executionZoneAction.executionZone" title="${message(code: 'scriptletBatch.zone.label', default: 'Zone')}" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${scriptletBatchInstanceList}" status="i" var="scriptletBatchInstance">
					<tr>
						<td>
							<g:link action="show" id="${scriptletBatchInstance.id}">
								${fieldValue(bean: scriptletBatchInstance, field: "description")}
							</g:link>
						</td>
						<td>
							<g:formatDate date="${scriptletBatchInstance.creationDate}" />
						</td>
						<td>
							<g:formatDate date="${scriptletBatchInstance.endDate}" />
						</td>
						<td>
							<g:formatDate date="${scriptletBatchInstance.startDate}" />
						</td>
						<td>
							${fieldValue(bean: scriptletBatchInstance, field: "state")}
						</td>
						<td>
							<g:link controller="executionZone" action="show" id="${scriptletBatchInstance?.executionZoneAction?.executionZone?.id}">
								${fieldValue(bean: scriptletBatchInstance, field: "executionZoneAction.executionZone.description")}
							</g:link>
						</td>

					</tr>
				</g:each>
			</tbody>
		</table>

		<fieldset class="buttons spacer">
			<filterpane:filterButton class="btn" text="Filter" />
		</fieldset>

		<filterpane:filterPane domain="ScriptletBatch" action="list" formMethod="get"
							   excludeProperties=""
							   associatedProperties="executionZoneAction.executionZone.description, executionZoneAction.executionZone.id, user.email, user.username, user.displayName"/>
		<filterpane:isFiltered>
			<h4>Current Filters:</h4>
			<filterpane:currentCriteria domainBean="ScriptletBatch" action="list" fullAssociationPathFieldNames="no"/>
		</filterpane:isFiltered>

		<div class="pagination">
			<filterpane:paginate total="${scriptletBatchInstanceTotal}" domainBean="ScriptletBatch"/>
		</div>
	</div>
</body>
</html>
