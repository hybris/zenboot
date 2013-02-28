<%@ page import="org.zenboot.portal.processing.ExecutionZoneType"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'executionZoneType.label', default: 'ExecutionZoneType')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="show-executionZoneType" class="content scaffold-show" role="main">
		<h2 class="page-header">
			<g:message code="default.show.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<g:link action="list">
			<i class="icon-list"></i>
			<g:message code="default.button.list.label" default="Back to overview" />
		</g:link>

		<ol class="property-list executionZoneType">
			<li class="fieldcontain">
				<span id="enabled-label" class="property-label">
					<g:message code="executionZoneType.enabled.label" default="Enabled" />
				</span>
				<span class="property-value" aria-labelledby="enabled-label">
					<g:fieldValue bean="${executionZoneTypeInstance}" field="enabled" />
				</span>
			</li>

			<g:if test="${executionZoneTypeInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label">
						<g:message code="executionZoneType.name.label" default="Name" />
					</span>
					<span class="property-value" aria-labelledby="name-label">
						<g:fieldValue bean="${executionZoneTypeInstance}" field="name" />
					</span>
				</li>
			</g:if>

			<g:if test="${executionZoneTypeInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label">
						<g:message code="executionZoneType.description.label" default="Description" />
					</span>
					<span class="property-value" aria-labelledby="description-label">
						<g:fieldValue bean="${executionZoneTypeInstance}" field="description" />
					</span>
				</li>
			</g:if>
		</ol>

		<g:form>
			<fieldset class="spacer buttons">
				<g:hiddenField name="id" value="${executionZoneTypeInstance?.id}" />
				<g:link class="btn btn-primary" action="edit" id="${executionZoneTypeInstance?.id}">
					<g:message code="default.button.edit.label" default="Edit" />
				</g:link>
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>