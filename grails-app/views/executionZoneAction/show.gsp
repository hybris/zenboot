<%@ page import="org.zenboot.portal.processing.ExecutionZoneAction"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'executionZoneAction.label', default: 'ExecutionZoneAction')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="show-executionZoneAction" class="content scaffold-show" role="main">
		<h2 class="page-header">
			<g:message code="default.show.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>
		
		<dl class="dl-horizontal">
			<g:if test="${executionZoneActionInstance?.id}">
				<dt>
					<g:message code="executionZoneAction.id.label" default="ID" />
				</dt>
				<dd>
					<g:fieldValue bean="${executionZoneActionInstance}" field="id" />
				</dd>
			</g:if>
			
			<g:if test="${executionZoneActionInstance?.scriptDir}">
				<dt>
					<g:message code="executionZoneAction.scriptDir.label" default="Script Dir" />
				</dt>
				<dd>
					<g:fieldValue bean="${executionZoneActionInstance}" field="scriptDir" />
				</dd>
			</g:if>
			<g:if test="${executionZoneActionInstance?.creationDate}">
				<dt>
					<g:message code="executionZoneAction.creationDate.label" default="Creation Date" />
				</dt>
				<dd>
					<g:formatDate date="${executionZoneActionInstance?.creationDate}" />
				</dd>
			</g:if>
			
			<g:if test="${executionZoneActionInstance?.executionZone}">
				<dt>
					<g:message code="executionZoneAction.executionZone.label" default="Execution Zone" />
				</dt>
				<dd>
					<g:link controller="executionZone" action="show" id="${executionZoneActionInstance?.executionZone?.id}">
							${executionZoneActionInstance?.executionZone?.type.name}
							<g:if test="${executionZoneActionInstance?.executionZone?.description}">
							(${executionZoneActionInstance?.executionZone?.description})
							</g:if>
						</g:link>
				</dd>
			</g:if>
			
			
			<g:if test="${executionZoneActionInstance?.scriptletBatches}">
				<dt>
					<g:message code="executionZoneAction.scriptletBatches.label" default="Scriptlet Batches" />
				</dt>
				<dd class="collapsable-list">
					<a class="collapsed" style="cursor: pointer">
							<g:message code="executionZoneAction.scriptletBatches.size" default="{0} batches defined" args="[executionZoneActionInstance.scriptletBatches.size()]" />
							<i class="icon-resize-full"></i>
						</a>
						<ul class="unstyled hide">
							<g:each in="${executionZoneActionInstance.scriptletBatches}" var="a" status="status">
								<li>
									<g:link controller="scriptletBatch" action="show" id="${a.id}">
										${a.description} (<g:formatDate type="datetime" style="MEDIUM" timeStyle="SHORT" date="${a.creationDate}" />)
									</g:link>
								</li>
							</g:each>
						</ul>
				</dd>
			</g:if>
			
			<g:if test="${executionZoneActionInstance?.processingParameters}">
				<dt>
					<g:message code="executionZoneAction.parameters.label" default="Parameters" />
				</dt>
				<dd>
					<table class="table table-striped exec-parameters-table" aria-labelledby="parameters-label">
							<thead>
								<tr>
									<th style="width: 45%">Key</th>
									<th style="width: 45%">Value</th>
								</tr>
							</thead>
							<tbody>
								<g:each in="${executionZoneActionInstance.processingParameters}" var="entry">
									<tr>
										<td>
											<g:textField name="parameters.key" value="${entry.name?.encodeAsHTML()}" readonly="true" />
										</td>
										<td>
											<g:textField name="parameters.value" value="${entry.value?.encodeAsHTML()}" readonly="true" />
										</td>
									</tr>
								</g:each>
							</tbody>
						</table>
				</dd>
			</g:if>
			
			<g:if test="${!executionZoneActionInstance?.runtimeAttributes.empty}">
				<dt>
					<g:message code="executionZoneAction.runtimeAttributes.label" default="RuntimeAttributes" />
				</dt>
				<dd>
					${executionZoneActionInstance.runtimeAttributes.join(", ")}
				</dd>
			
			</g:if>
			
		</dl>

		<g:form>
			<fieldset class="spacer buttons">
				<g:hiddenField name="id" value="${executionZoneActionInstance?.id}" />
				<g:link class="btn" action="show" controller="executionZone" params="[execId:executionZoneActionInstance.executionZone.id]">
					${message(code: 'default.button.cancel.label', default: 'Cancel')}
				</g:link>
				<%-- TODO: check for referential integrity before delete is allowed --%>
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" disabled="true" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>