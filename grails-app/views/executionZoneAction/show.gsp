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

		<ol class="property-list executionZoneAction">
			<g:if test="${executionZoneActionInstance?.scriptDir}">
				<li class="fieldcontain">
					<g:message code="executionZoneAction.scriptDir.label" default="Script Dir" />
					</span>
					<span class="property-value" aria-labelledby="scriptDir-label">
						<g:fieldValue bean="${executionZoneActionInstance}" field="scriptDir" />
					</span>
				</li>
			</g:if>

			<g:if test="${executionZoneActionInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label">
						<g:message code="executionZoneAction.creationDate.label" default="Creation Date" />
					</span>
					<span class="property-value" aria-labelledby="creationDate-label">
						<g:formatDate date="${executionZoneActionInstance?.creationDate}" />
					</span>
				</li>
			</g:if>

			<g:if test="${executionZoneActionInstance?.executionZone}">
				<li class="fieldcontain">
					<span id="executionZone-label" class="property-label">
						<g:message code="executionZoneAction.executionZone.label" default="Execution Zone" />
					</span>
					<span class="property-value" aria-labelledby="executionZone-label">
						<g:link controller="executionZone" action="show" id="${executionZoneActionInstance?.executionZone?.id}">
							${executionZoneActionInstance?.executionZone?.type.name}
							<g:if test="${executionZoneActionInstance?.executionZone?.description}">
							(${executionZoneActionInstance?.executionZone?.description})
							</g:if>
						</g:link>
					</span>
				</li>
			</g:if>

			<g:if test="${executionZoneActionInstance?.scriptletBatches}">
				<li class="fieldcontain">
					<span id="executionZone-label" class="property-label">
						<g:message code="executionZoneAction.scriptletBatches.label" default="Scriptlet Batches" />
					</span>
					<div class="property-value collapsable-list" aria-labelledby="actions-label">
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
					</div>
				</li>
			</g:if>

			<g:if test="${executionZoneActionInstance?.processingParameters}">
				<li class="fieldcontain">
					<span id="parameters-label" class="property-label">
						<g:message code="executionZoneAction.parameters.label" default="Parameters" />
					</span>
					<div class="property-value">
						<table class="table table-striped parameters-table" aria-labelledby="parameters-label">
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
					</div>
				</li>
			</g:if>

			<g:if test="${!executionZoneActionInstance?.runtimeAttributes.empty}">
				<li class="fieldcontain">
					<span id="runtimeAttributes-label" class="property-label">
						<g:message code="executionZoneAction.runtimeAttributes.label" default="RuntimeAttributes" />
					</span>
					<span class="property-value">
						${executionZoneActionInstance.runtimeAttributes.join(", ")}
					</span>
				</li>
			</g:if>
		</ol>

		<g:form>
			<fieldset class="spacer buttons">
				<g:hiddenField name="id" value="${executionZoneActionInstance?.id}" />
				<g:link class="btn" action="show" controller="executionZone" params="[id:executionZoneActionInstance.executionZone.id]">
					${message(code: 'default.button.cancel.label', default: 'Cancel')}
				</g:link>
				<%-- TODO: check for referential integrity before delete is allowed --%>
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" disabled="true" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>