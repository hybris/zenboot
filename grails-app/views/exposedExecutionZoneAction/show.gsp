<%@ page import="org.zenboot.portal.processing.ExposedExecutionZoneAction"%>
<%@ page import="org.zenboot.portal.security.Role"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'exposedExecutionZoneAction.label', default: 'ExposedExecutionZoneAction')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="show-exposedExecutionZoneAction" class="content scaffold-show" role="main">
		<h2 class="page-header">
			<g:message code="default.show.label" args="[entityName]" />
		</h2>

		<g:hasErrors bean="${cmd}">
			<ul class="alert alert-error" role="alert">
				<g:eachError bean="${cmd}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
						<g:message error="${error}" />
					</li>
				</g:eachError>
			</ul>
		</g:hasErrors>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<g:link action="list">
			<i class="icon-list"></i>
			<g:message code="default.button.list.label" default="Back to overview" />
		</g:link>

		<g:form>
		
			<dl class="dl-horizontal">
				<g:if test="${exposedExecutionZoneActionInstance?.scriptDir}">
					<dt>
						<g:message code="exposedExecutionZoneAction.scriptDir.label" default="Script Dir" />
					</dt>
					<dd>
						<g:fieldValue bean="${exposedExecutionZoneActionInstance}" field="scriptDir" />
					</dd>
				</g:if>
				
				<g:if test="${exposedExecutionZoneActionInstance?.cronExpression}">
					<dt>
						<g:message code="exposedExecutionZoneAction.cronExpression.label" default="Cron Expression" />
					</dt>
					<dd>
						<g:fieldValue bean="${exposedExecutionZoneActionInstance}" field="cronExpression" />
					</dd>
				</g:if>
				
				<g:if test="${exposedExecutionZoneActionInstance?.creationDate}">
					<dt>
						<g:message code="exposedExecutionZoneAction.creationDate.label" default="Creation Date" />
					</dt>
					<dd>
						<g:formatDate date="${exposedExecutionZoneActionInstance?.creationDate}" />
					</dd>
				</g:if>
				
				<g:if test="${exposedExecutionZoneActionInstance?.executionZone}">
					<dt>
						<g:message code="exposedExecutionZoneAction.executionZone.label" default="Execution Zone" />
					</dt>
					<dd>
						<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
								<g:link controller="executionZone" action="show" id="${exposedExecutionZoneActionInstance?.executionZone?.id}">
									${exposedExecutionZoneActionInstance?.executionZone?.encodeAsHTML()}
								</g:link>
						</sec:ifAllGranted>
						<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
							${exposedExecutionZoneActionInstance?.executionZone?.encodeAsHTML()}
						</sec:ifNotGranted>
						<g:if test="${exposedExecutionZoneActionInstance?.executionZone?.description}">
							(${exposedExecutionZoneActionInstance?.executionZone?.description})
						</g:if>
					</dd>
				</g:if>
				
				<g:if test="${!exposedExecutionZoneActionParameters?.empty}">
					<dt>
						<g:message code="exposedExecutionZoneAction.parameters.label" default="Parameters" />
					</dt>
					<dd>
						<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
								<g:render template="showParametersAdmin" model="[parameters:exposedExecutionZoneActionParameters]" />
							</sec:ifAllGranted>
							<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
								<g:render template="showParameters" model="[parameters:exposedExecutionZoneActionParameters]" />
							</sec:ifNotGranted>
					</dd>
				</g:if>
				
				<g:if test="${exposedExecutionZoneActionInstance?.roles}">
					<dt>
						<g:message code="exposedExecutionZoneAction.roles.label" default="Roles" />
					</dt>
					<dd>
						<ol class="property-value unstyled" aria-labelledby="roles-label">
							<g:each in="${exposedExecutionZoneActionInstance.roles}" var="r">
								<li>
									${r?.encodeAsHTML()}
								</li>
							</g:each>
						</ol>
					</dd>
				</g:if>
				
				<g:if test="${exposedExecutionZoneActionInstance?.url}">
					<dt>
						<g:message code="exposedExecutionZoneAction.url.label" default="Url" />
					</dt>
					<dd>
						<g:createLink controller="exposedExecutionZoneAction" action="rest" absolute="true" />/<g:fieldValue bean="${exposedExecutionZoneActionInstance}" field="url" />
					</dd>
				</g:if>
			</dl>


			<fieldset class="spacer buttons">
				<g:hiddenField name="execId" value="${exposedExecutionZoneActionInstance?.id}" />
				<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
					<g:link class="btn btn-primary" action="edit" id="${exposedExecutionZoneActionInstance?.id}">
						<g:message code="default.button.edit.label" default="Edit" />
					</g:link>
					<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					<span style="margin-left: 20px;">&nbsp;</span>
				</sec:ifAllGranted>
				<g:actionSubmit class="btn btn-success" action="execute" value="${message(code: 'default.button.execute.label', default: 'Execute')}" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>