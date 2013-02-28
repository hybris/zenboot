<%@ page import="org.zenboot.portal.DnsEntry"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'dnsEntry.label', default: 'DnsEntry')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="show-dnsEntry" class="content scaffold-show" role="main">
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

		<ol class="property-list dnsEntry">
			<g:if test="${dnsEntryInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label">
						<g:message code="dnsEntry.creationDate.label" default="Creation Date" />
					</span>
					<span class="property-value" aria-labelledby="creationDate-label">
						<g:formatDate date="${dnsEntryInstance?.creationDate}" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hasProperty('dnsId') && dnsEntryInstance.dnsId}">
				<li class="fieldcontain">
					<span id="dnsId-label" class="property-label">
						<g:message code="dnsEntry.dnsId.label" default="Dns Id" />
					</span>
					<span class="property-value" aria-labelledby="dnsId-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="dnsId" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.fqdn}">
				<li class="fieldcontain">
					<span id="fqdn-label" class="property-label">
						<g:message code="dnsEntry.fqdn.label" default="Fqdn" />
					</span>
					<span class="property-value" aria-labelledby="fqdn-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="fqdn" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hostType}">
				<li class="fieldcontain">
					<span id="hostType-label" class="property-label">
						<g:message code="dnsEntry.hostType.label" default="Host Type" />
					</span>
					<span class="property-value" aria-labelledby="hostType-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="hostType" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hasProperty('notes') && dnsEntryInstance.notes}">
				<li class="fieldcontain">
					<span id="notes-label" class="property-label">
						<g:message code="dnsEntry.notes.label" default="Notes" />
					</span>
					<span class="property-value" aria-labelledby="notes-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="notes" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.owner}">
				<li class="fieldcontain">
					<span id="owner-label" class="property-label">
						<g:message code="dnsEntry.owner.label" default="Owner" />
					</span>
					<span class="property-value" aria-labelledby="owner-label">
						<g:link controller="host" action="show" id="${dnsEntryInstance?.owner?.id}">
							${dnsEntryInstance?.owner?.encodeAsHTML()}
						</g:link>
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hasProperty('priority') && dnsEntryInstance.priority}">
				<li class="fieldcontain">
					<span id="priority-label" class="property-label">
						<g:message code="dnsEntry.priority.label" default="Priority" />
					</span>
					<span class="property-value" aria-labelledby="priority-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="priority" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hasProperty('publicIp') && dnsEntryInstance.publicIp}">
				<li class="fieldcontain">
					<span id="publicIp-label" class="property-label">
						<g:message code="dnsEntry.publicIp.label" default="Public Ip" />
					</span>
					<span class="property-value" aria-labelledby="publicIp-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="publicIp" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hasProperty('state') && dnsEntryInstance.state}">
				<li class="fieldcontain">
					<span id="state-label" class="property-label">
						<g:message code="dnsEntry.state.label" default="State" />
					</span>
					<span class="property-value" aria-labelledby="state-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="state" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hasProperty('ttl') && dnsEntryInstance.ttl}">
				<li class="fieldcontain">
					<span id="ttl-label" class="property-label">
						<g:message code="dnsEntry.ttl.label" default="Ttl" />
					</span>
					<span class="property-value" aria-labelledby="ttl-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="ttl" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hasProperty('updateDate') && dnsEntryInstance.updateDate}">
				<li class="fieldcontain">
					<span id="updateDate-label" class="property-label">
						<g:message code="dnsEntry.updateDate.label" default="Update Date" />
					</span>
					<span class="property-value" aria-labelledby="updateDate-label">
						<g:formatDate date="${dnsEntryInstance?.updateDate}" />
					</span>
				</li>
			</g:if>

			<g:if test="${dnsEntryInstance?.hasProperty('zoneId') && dnsEntryInstance.zoneId}">
				<li class="fieldcontain">
					<span id="zoneId-label" class="property-label">
						<g:message code="dnsEntry.zoneId.label" default="Zone Id" />
					</span>
					<span class="property-value" aria-labelledby="zoneId-label">
						<g:fieldValue bean="${dnsEntryInstance}" field="zoneId" />
					</span>
				</li>
			</g:if>
		</ol>

		<g:form>
			<fieldset class="buttons">
				<g:hiddenField name="id" value="${dnsEntryInstance?.id}" />
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>
