<%=packageName%>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<div id="create-${domainClass.propertyName}" class="content scaffold-create" role="main">
			<h2 class="page-header"><g:message code="default.create.label" args="[entityName]" /></h2>
			<g:if test="\${flash.message}">
			<div class="alert alert-info" role="status">\${flash.message}</div>
			</g:if>
			<g:hasErrors bean="\${${propertyName}}">
			<ul class="alert alert-error" role="alert">
				<g:eachError bean="\${${propertyName}}" var="error">
				<li <g:if test="\${error in org.springframework.validation.FieldError}">data-field-id="\${error.field}"</g:if>><g:message error="\${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
	        <g:link action="list">
	            <i class="icon-list"></i> <g:message code="default.button.list.label" default="Back to overview" />
	        </g:link>
			<g:form action="save" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>
				<fieldset class="form-horizontal">
					<g:render template="form"/>
				</fieldset>
	            <fieldset class="buttons spacer">
	                <g:link class="btn" action="list">\${message(code: 'default.button.cancel.label', default: 'Cancel')}</g:link>
	                <g:submitButton class="btn btn-primary" action="create" name="\${message(code: 'default.button.create.label', default: 'Create')}" />
	            </fieldset>
			</g:form>
		</div>
	</body>
</html>