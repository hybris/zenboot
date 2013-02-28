<%@ page import="org.zenboot.portal.processing.ExposedExecutionZoneAction"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction')}" />
<title>
	<g:message code="default.create.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="create-exposedExecutionZoneAction" class="content scaffold-create" role="main">
		<h2 class="page-header">
			<g:message code="default.create.label" args="[entityName]" />
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

		<g:form action="save">
			<fieldset class="form-horizontal">
				<g:render template="form" />
			</fieldset>
			<fieldset class="buttons spacer">
				<g:if test="${exposedExecutionZoneActionInstance.executionZone}">
					<g:link class="btn" action="show" controller="executionZone" params="[id:exposedExecutionZoneActionInstance.executionZone.id]">
						${message(code: 'default.button.cancel.label', default: 'Cancel')}
					</g:link>
				</g:if>
				<g:else>
					<g:link class="btn" action="list">
						${message(code: 'default.button.cancel.label', default: 'Cancel')}
					</g:link>
				</g:else>
				<g:submitButton class="btn btn-primary" action="create" name="${message(code: 'default.button.create.label', default: 'Create')}" />
			</fieldset>
		</g:form>
	</div>

	<g:javascript>
	$(document).ready(function() {
	    //Exposed action data get lost if the page will be reloaded (data were transfered in request scope)
		if ($('input[name=scriptDir]').val() !== "" && $('.alert').size() === 0) {
		    $(window).bind('beforeunload', function() {
		        return "If you reload this page, the exposed action data will get lost and you need to re-expose it again"    
		    });
			$('.btn').not('.btn-mini').click(function() {
				$(window).unbind('beforeunload')
			});
            $('.nav a').click(function() {
                $(window).unbind('beforeunload')
            });
		}
	});
	</g:javascript>
</body>
</html>