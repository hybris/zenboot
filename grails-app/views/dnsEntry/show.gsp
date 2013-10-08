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

		<dl class="dl-horizontal">
			<g:if test="${dnsEntryInstance?.creationDate}">
				<dt>
					<g:message code="dnsEntry.creationDate.label" default="Creation Date" />
				</dt>
				<dd>
					<g:formatDate date="${dnsEntryInstance?.creationDate}" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hasProperty('dnsId') && dnsEntryInstance.dnsId}">
				<dt>
					<g:message code="dnsEntry.dnsId.label" default="Dns Id" />
				</dt>
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="dnsId" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.fqdn}">
				<dt>
					<g:message code="dnsEntry.fqdn.label" default="Fqdn" />
				</dt>
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="fqdn" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hostType}">
				<dt>
					<g:message code="dnsEntry.hostType.label" default="Host Type" />
				</dt>
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="hostType" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hasProperty('notes') && dnsEntryInstance.notes}">
				<dt>
					<g:message code="dnsEntry.notes.label" default="Notes" />
				</dt>
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="notes" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.owner}">
				<dt>
					<g:message code="dnsEntry.owner.label" default="Owner" />
				</dt>
				<dd>
					<g:link controller="host" action="show" id="${dnsEntryInstance?.owner?.id}">
							${dnsEntryInstance?.owner?.encodeAsHTML()}
						</g:link>
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hasProperty('priority') && dnsEntryInstance.priority}">
				<dt>
					<g:message code="dnsEntry.priority.label" default="Priority" />
				</dt>
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="priority" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hasProperty('publicIp') && dnsEntryInstance.publicIp}">
				<dt>
					<g:message code="dnsEntry.publicIp.label" default="Public Ip" />
				</dt>
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="publicIp" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hasProperty('state') && dnsEntryInstance.state}">
				<dt>
					<g:message code="dnsEntry.state.label" default="State" />
				</dt>	
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="state" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hasProperty('ttl') && dnsEntryInstance.ttl}">
				<dt>
					<g:message code="dnsEntry.ttl.label" default="Ttl" />
				</dt>
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="ttl" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hasProperty('updateDate') && dnsEntryInstance.updateDate}">
				<dt>
					<g:message code="dnsEntry.updateDate.label" default="Update Date" />
				</dt>
				<dd>
					<g:formatDate date="${dnsEntryInstance?.updateDate}" />
				</dd>
			</g:if>
			
			<g:if test="${dnsEntryInstance?.hasProperty('zoneId') && dnsEntryInstance.zoneId}">
				<dt>
					<g:message code="dnsEntry.zoneId.label" default="Zone Id" />
				</dt>
				<dd>
					<g:fieldValue bean="${dnsEntryInstance}" field="zoneId" />
				</dd>
			</g:if>
		</dl>


		<g:form>
			<fieldset class="buttons">
				<g:hiddenField name="id" value="${dnsEntryInstance?.id}" />
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>
