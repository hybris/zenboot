<%@ page import="org.zenboot.portal.processing.ExposedExecutionZoneAction"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction')}" />
<title>
	<g:message code="default.edit.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="edit-exposedExecutionZoneAction" class="content scaffold-edit" role="main">
		<h2 class="page-header">
			<g:message code="default.edit.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<g:hasErrors bean="${exposedExecutionZoneActionInstance}">
			<ul class="alert alert-error" role="alert">
				<g:eachError bean="${exposedExecutionZoneActionInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
						<g:message error="${error}" />
					</li>
				</g:eachError>
			</ul>
		</g:hasErrors>
        <g:hasErrors bean="${cmd}">
            <ul class="alert alert-error" role="alert">
                <g:eachError bean="${cmd}" var="error">
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
			<g:hiddenField name="id" value="${exposedExecutionZoneActionInstance?.id}" />
			<g:hiddenField name="version" value="${exposedExecutionZoneActionInstance?.version}" />
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
