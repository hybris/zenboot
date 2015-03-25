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

		<dl class="dl-horizontal">
			<dt>
				<g:message code="executionZoneType.enabled.label" default="Enabled" />
			</dt>
			<dd>
				<g:fieldValue bean="${executionZoneTypeInstance}" field="enabled" />
			</dd>

			<g:if test="${executionZoneTypeInstance?.name}">
				<dt>
					<g:message code="executionZoneType.name.label" default="Name" />
				</dt>
				<dd>
					<g:fieldValue bean="${executionZoneTypeInstance}" field="name" />
				</dd>
			</g:if>

			<g:if test="${executionZoneTypeInstance?.description}">
				<dt>
					<g:message code="executionZoneType.description.label" default="Description" />
				</dt>
				<dd>
					<g:fieldValue bean="${executionZoneTypeInstance}" field="description" />
				</dd>
			</g:if>
			<dt>
				<g:message code="executionZoneType.devMode.label" default="DevMode" />
			</dt>
			<dd>
				<g:fieldValue bean="${executionZoneTypeInstance}" field="devMode" />
			</dd>
		</dl>


		<g:form>
			<fieldset class="spacer buttons">
				<g:hiddenField name="id" value="${executionZoneTypeInstance?.id}" />
				<g:link class="btn btn-primary" action="edit" id="${executionZoneTypeInstance?.id}">
					<g:message code="default.button.edit.label" default="Edit" />
				</g:link>
			</fieldset>
		</g:form>
	</div>
</body>
</html>
