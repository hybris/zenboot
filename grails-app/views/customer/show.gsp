<%@ page import="org.zenboot.portal.Customer"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="show-customer" class="content scaffold-show" role="main">
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
			<g:if test="${customerInstance?.email}">
				<dt>
					<g:message code="customer.email.label" default="Email" />
				</dt>
				<dd>
					<g:fieldValue bean="${customerInstance}" field="email" />
				</dd>
			</g:if>
			
			<g:if test="${customerInstance?.creationDate}">
				<dt>
					<g:message code="customer.creationDate.label" default="Creation Date" />
				</dt>
				<dd>
					<g:formatDate date="${customerInstance?.creationDate}" />
				</dd>
			</g:if>
			
			<g:if test="${customerInstance?.hosts}">
				<dt>
					<g:message code="customer.hosts.label" default="Hosts" />
				</dt>
					<g:each in="${customerInstance.hosts}" var="h">
						<span class="property-value" aria-labelledby="hosts-label">
							<g:link controller="host" action="show" id="${h.id}">
								${h?.hostname} (${h?.environment})
							</g:link>
						</span>
					</g:each>
			</g:if>
			
		</dl>

		<g:form>
			<fieldset class="spacer buttons">
				<g:hiddenField name="id" value="${customerInstance?.id}" />
				<g:link class="btn btn-primary" action="edit" id="${customerInstance?.id}">
					<g:message code="default.button.edit.label" default="Edit" />
				</g:link>
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>