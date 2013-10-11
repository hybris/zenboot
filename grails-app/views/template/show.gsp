
<%@ page import="org.zenboot.portal.Template" %>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'template.label', default: 'Template')}" />
<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>
<body>
	<div id="show-template" class="content scaffold-show" role="main">
		<h2 class="page-header">
			<g:message code="default.show.label" args="[entityName]" />
		</h2>
		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">${flash.message}</div>
		</g:if>
		<g:link action="list">
			<i class="icon-list"></i>
			<g:message code="default.button.list.label" default="Back to overview" />
		</g:link>
		<ol class="property-list template">
			
				<g:if test="${templateInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="template.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${templateInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${templateInstance?.template}">
				<li class="fieldcontain">
					<span id="template-label" class="property-label"><g:message code="template.template.label" default="Template" /></span>
					
						<span class="property-value" aria-labelledby="template-label"><g:fieldValue bean="${templateInstance}" field="template"/></span>
					
				</li>
				</g:if>
			
		</ol>
		
						<g:each in="${templateInstanceList}" status="i" var="templateInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${templateInstance.id}">${fieldValue(bean: templateInstance, field: "name")}</g:link></td>
					</tr>
				</g:each>
		<g:if test="${templateInstance?.templateVersions}">
			<table>
				<tbody>
					<g:each in="${templateInstance?.templateVersions}" status="i" var="templateVersion">
						<tr>
							<td>
								${ templateVersion.dateCreated }
							</td>
							<td>
								${ templateVersion.content }
							</td>
						</tr>
					</g:each>
				</tbody>
			</table>
		</g:if>
		
		<g:form>
			<fieldset class="spacer buttons">
				<g:hiddenField name="id" value="${templateInstance?.id}" />
				<g:link class="btn btn-primary" action="edit" id="${templateInstance?.id}">
					<g:message code="default.button.edit.label" default="Edit" />
				</g:link>
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}"
					onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>