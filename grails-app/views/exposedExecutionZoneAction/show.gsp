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
			<ol class="property-list exposedExecutionZoneAction">

				<g:if test="${exposedExecutionZoneActionInstance?.scriptDir}">
					<li class="fieldcontain">
						<span id="scriptDir-label" class="property-label">
							<g:message code="exposedExecutionZoneAction.scriptDir.label" default="Script Dir" />
						</span>
						<span class="property-value" aria-labelledby="scriptDir-label">
							<g:fieldValue bean="${exposedExecutionZoneActionInstance}" field="scriptDir" />
						</span>
					</li>
				</g:if>

				<g:if test="${exposedExecutionZoneActionInstance?.cronExpression}">
					<li class="fieldcontain">
						<span id="cronExpression-label" class="property-label">
							<g:message code="exposedExecutionZoneAction.cronExpression.label" default="Cron Expression" />
						</span>
						<span class="property-value" aria-labelledby="cronExpression-label">
							<g:fieldValue bean="${exposedExecutionZoneActionInstance}" field="cronExpression" />
						</span>
					</li>
				</g:if>

				<g:if test="${exposedExecutionZoneActionInstance?.creationDate}">
					<li class="fieldcontain">
						<span id="creationDate-label" class="property-label">
							<g:message code="exposedExecutionZoneAction.creationDate.label" default="Creation Date" />
						</span>
						<span class="property-value" aria-labelledby="creationDate-label">
							<g:formatDate date="${exposedExecutionZoneActionInstance?.creationDate}" />
						</span>
					</li>
				</g:if>

				<g:if test="${exposedExecutionZoneActionInstance?.executionZone}">
					<li class="fieldcontain">
						<span id="executionZone-label" class="property-label">
							<g:message code="exposedExecutionZoneAction.executionZone.label" default="Execution Zone" />
						</span>
						<span class="property-value" aria-labelledby="executionZone-label">
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
						</span>
					</li>
				</g:if>

				<g:if test="${!exposedExecutionZoneActionParameters?.empty}">
					<li class="fieldcontain">
						<span id="parameters-label" class="property-label">
							<g:message code="exposedExecutionZoneAction.parameters.label" default="Parameters" />
						</span>
						<span class="property-value" aria-labelledby="parameters-label">
							<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
								<g:render template="showParametersAdmin" model="[parameters:exposedExecutionZoneActionParameters]" />
							</sec:ifAllGranted>
							<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
								<g:render template="showParameters" model="[parameters:exposedExecutionZoneActionParameters]" />
							</sec:ifNotGranted>

						</span>
					</li>
				</g:if>

				<g:if test="${exposedExecutionZoneActionInstance?.roles}">
					<li class="fieldcontain">
						<span id="roles-label" class="property-label">
							<g:message code="exposedExecutionZoneAction.roles.label" default="Roles" />
						</span>
						<ol class="property-value unstyled" aria-labelledby="roles-label">
							<g:each in="${exposedExecutionZoneActionInstance.roles}" var="r">
								<li>
									${r?.encodeAsHTML()}
								</li>
							</g:each>
						</ol>
					</li>
				</g:if>

				<g:if test="${exposedExecutionZoneActionInstance?.url}">
					<li class="fieldcontain">
						<span id="url-label" class="property-label">
							<g:message code="exposedExecutionZoneAction.url.label" default="Url" />
						</span>
						<span class="property-value" aria-labelledby="url-label">
							<g:createLink controller="exposedExecutionZoneAction" action="rest" absolute="true" />/<g:fieldValue bean="${exposedExecutionZoneActionInstance}" field="url" />
						</span>
					</li>
				</g:if>
			</ol>

			<fieldset class="spacer buttons">
				<g:hiddenField name="id" value="${exposedExecutionZoneActionInstance?.id}" />
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