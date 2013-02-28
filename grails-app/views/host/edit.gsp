<%@ page import="org.zenboot.portal.Host"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'host.label', default: 'Host')}" />
<title>
	<g:message code="default.edit.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="edit-host" class="content scaffold-edit" role="main">
		<h2 class="page-header">
			<g:message code="default.edit.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<g:hasErrors bean="${hostInstance}">
			<ul class="alert alert-error" role="alert">
				<g:eachError bean="${hostInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
						<g:message error="${error}" />
					</li>
				</g:eachError>
			</ul>
		</g:hasErrors>

		<g:link action="list">
			<i class="icon-list"></i>
			<g:message code="default.button.list.label" default="Back to overview" />
		</g:link>

		<g:form method="post">
			<g:hiddenField name="id" value="${hostInstance?.id}" />
			<g:hiddenField name="version" value="${hostInstance?.version}" />
			<fieldset class="form-horizontal">
				<g:render template="form" />
			</fieldset>
			<fieldset class="buttons spacer">
				<g:actionSubmit class="btn" action="show" value="${message(code: 'default.button.cancel.label', default: 'Cancel')}" />
				<g:actionSubmit class="btn btn-primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>
