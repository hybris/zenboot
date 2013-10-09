
<%@ page import="org.zenboot.portal.Template" %>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'template.label', default: 'Template')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<div id="list-template" class="content scaffold-list" role="main">
		<h2 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
		</h2>
		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">${flash.message}</div>
		</g:if>
		<table class="table table-striped">
			<thead>
				<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'template.name.label', default: 'Name')}" />
					
						<% /*<g:sortableColumn property="template" title="${message(code: 'template.template.label', default: 'Template')}" />*/ %>
					
				</tr>
			</thead>
			<tbody>
				<g:each in="${templateInstanceList}" status="i" var="templateInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
						<td><g:link action="edit" id="${templateInstance.id}">${fieldValue(bean: templateInstance, field: "name")}</g:link></td>
					
						<% /*<td>${fieldValue(bean: templateInstance, field: "template")}</td>*/ %>
					
					</tr>
				</g:each>
			</tbody>
		</table>
		
		<fieldset class="buttons spacer">
			<g:link class="btn btn-primary" action="create">
				${message(code: 'default.button.create.label', default: 'Create')}
			</g:link>
		</fieldset>
		
		<div class="pagination">
			<g:paginate total="${templateInstanceTotal}" />
		</div>
	</div>
</body>
</html>