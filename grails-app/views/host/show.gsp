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

		
		<dl class="dl-horizontal">
			<dt>
				<g:message code="host.type.label" default="Type" />
			</dt>
			<dd>
				${hostInstance?.class.getSimpleName()}
			</dd>
			
			<g:if test="${hostInstance?.ipAddress}">
				<dt>
					<g:message code="host.ipAddress.label" default="Ip Address" />
				</dt>
				<dd>
					<g:fieldValue bean="${hostInstance}" field="ipAddress" />
				</dd>
			</g:if>
			
			<g:if test="${hostInstance?.cname}">
				<dt>
					<g:message code="host.cname.label" default="Cname" />
				</dt>
				<dd>
					<g:fieldValue bean="${hostInstance}" field="cname" />
				</dd>
			</g:if>

			<g:if test="${hostInstance?.macAddress}">
				<dt>
					<g:message code="host.macAddress.label" default="Mac Address" />
				</dt>
				<dd>
					<g:fieldValue bean="${hostInstance}" field="macAddress" />
				</dd>
			</g:if>

			<g:if test="${hostInstance?.hostname}">
				<dt>
					<g:message code="host.hostname.label" default="Hostname" />
				</dt>
				<dd>
					${hostInstance?.hostname?.encodeAsHTML()}
				</dd>
			</g:if>
			
			<g:if test="${hostInstance?.instanceId}">
				<dt>
					<g:message code="host.instanceId.label" default="Instance Id" />
				</dt>
				<dd>
					<g:fieldValue bean="${hostInstance}" field="instanceId" />
				</dd>
			</g:if>
			
			<g:if test="${hostInstance?.state}">
				<dt>
					<g:message code="host.state.label" default="State" />
				</dt>
				<dd>
					<g:fieldValue bean="${hostInstance}" field="state" />
				</dd>			
			</g:if>
			
			<g:if test="${hostInstance?.creationDate}">
				<dt>
					<g:message code="host.creationDate.label" default="Creation Date" />
				</dt>
				<dd>
					<g:formatDate date="${hostInstance?.creationDate}" />
				</dd>
			</g:if>
			
			<g:if test="${hostInstance?.dnsEntries}">
				<dt>
					<g:message code="host.dnsEntries.label" default="Dns Entries" />
				</dt>
				<dd>
					<g:each in="${hostInstance.dnsEntries}" var="d">
						<span class="property-value" aria-labelledby="dnsEntries-label">
							<g:link controller="dnsEntry" action="show" id="${d?.id}">
								${d?.encodeAsHTML()}
							</g:link>
						</span>
					</g:each>
				</dd>
			</g:if>
			
			<g:if test="${hostInstance?.expiryDate}">
				<dt>
					<g:message code="host.expiryDate.label" default="Expiry Date" />
				</dt>
				<dd>
					<g:formatDate date="${hostInstance?.expiryDate}" />
				</dd>
			</g:if>
			
			<g:if test="${hostInstance?.owner}">
				<dt>
					<g:message code="host.owner.label" default="Owner" />
				</dt>
				<dd>
					<g:link controller="customer" action="show" id="${hostInstance?.owner?.id}">
							${hostInstance?.owner?.encodeAsHTML()}
						</g:link>
				</dd>
			</g:if>
		</dl>


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
