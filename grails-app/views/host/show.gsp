<%@ page import="org.zenboot.portal.Host"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'host.label', default: 'Host')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="show-host" class="content scaffold-show" role="main">
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

		<ol class="property-list host">
			<li class="fieldcontain">
				<span id="type-label" class="property-label">
					<g:message code="host.type.label" default="Type" />
				</span>
				<span class="property-value" aria-labelledby="type-label">
					${hostInstance?.class.getSimpleName()}
				</span>
			</li>

			<g:if test="${hostInstance?.ipAddress}">
				<li class="fieldcontain">
					<span id="ipAddress-label" class="property-label">
						<g:message code="host.ipAddress.label" default="Ip Address" />
					</span>
					<span class="property-value" aria-labelledby="ipAddress-label">
						<g:fieldValue bean="${hostInstance}" field="ipAddress" />
					</span>
				</li>
			</g:if>

			<g:if test="${hostInstance?.macAddress}">
				<li class="fieldcontain">
					<span id="macAddress-label" class="property-label">
						<g:message code="host.macAddress.label" default="Mac Address" />
					</span>
					<span class="property-value" aria-labelledby="macAddress-label">
						<g:fieldValue bean="${hostInstance}" field="macAddress" />
					</span>
				</li>
			</g:if>

			<g:if test="${hostInstance?.hostname}">
				<li class="fieldcontain">
					<span id="hostname-label" class="property-label">
						<g:message code="host.hostname.label" default="Hostname" />
					</span>
					<span class="property-value" aria-labelledby="hostname-label">
						${hostInstance?.hostname?.encodeAsHTML()}
					</span>
				</li>
			</g:if>

			<g:if test="${hostInstance?.instanceId}">
				<li class="fieldcontain">
					<span id="instanceId-label" class="property-label">
						<g:message code="host.instanceId.label" default="Instance Id" />
					</span>
					<span class="property-value" aria-labelledby="instanceId-label">
						<g:fieldValue bean="${hostInstance}" field="instanceId" />
					</span>
				</li>
			</g:if>

			<g:if test="${hostInstance?.state}">
				<li class="fieldcontain">
					<span id="state-label" class="property-label">
						<g:message code="host.state.label" default="State" />
					</span>
					<span class="property-value" aria-labelledby="state-label">
						<g:fieldValue bean="${hostInstance}" field="state" />
					</span>
				</li>
			</g:if>

			<g:if test="${hostInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label">
						<g:message code="host.creationDate.label" default="Creation Date" />
					</span>
					<span class="property-value" aria-labelledby="creationDate-label">
						<g:formatDate date="${hostInstance?.creationDate}" />
					</span>
				</li>
			</g:if>

			<g:if test="${hostInstance?.dnsEntries}">
				<li class="fieldcontain">
					<span id="dnsEntries-label" class="property-label">
						<g:message code="host.dnsEntries.label" default="Dns Entries" />
					</span>
					<g:each in="${hostInstance.dnsEntries}" var="d">
						<span class="property-value" aria-labelledby="dnsEntries-label">
							<g:link controller="dnsEntry" action="show" id="${d?.id}">
								${d?.encodeAsHTML()}
							</g:link>
						</span>
					</g:each>
				</li>
			</g:if>

			<g:if test="${hostInstance?.expiryDate}">
				<li class="fieldcontain">
					<span id="expiryDate-label" class="property-label">
						<g:message code="host.expiryDate.label" default="Expiry Date" />
					</span>
					<span class="property-value" aria-labelledby="expiryDate-label">
						<g:formatDate date="${hostInstance?.expiryDate}" />
					</span>
				</li>
			</g:if>

			<g:if test="${hostInstance?.owner}">
				<li class="fieldcontain">
					<span id="owner-label" class="property-label">
						<g:message code="host.owner.label" default="Owner" />
					</span>
					<span class="property-value" aria-labelledby="owner-label">
						<g:link controller="customer" action="show" id="${hostInstance?.owner?.id}">
							${hostInstance?.owner?.encodeAsHTML()}
						</g:link>
					</span>
				</li>
			</g:if>
		</ol>

		<g:form name="hostForm">
			<fieldset class="buttons">
				<g:hiddenField name="id" value="${hostInstance?.id}" />
				<g:link class="btn btn-primary" action="edit" id="${hostInstance?.id}">
					<g:message code="default.button.edit.label" default="Edit" />
				</g:link>
				<g:actionSubmit id="deleteButton" class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'host.button.delete.confirm.message', default: 'Are you sure to delete host ${hostInstance}?', args:[hostInstance])}');" />
			</fieldset>
		</g:form>

		<g:if test="${params.delete}">
			<g:javascript>
		    $(document).ready(function() {
			    $('#deleteButton').click()
		    });
            </g:javascript>
		</g:if>
	</div>
</body>
</html>
