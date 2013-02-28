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

		<ol class="property-list customer">
			<g:if test="${customerInstance?.email}">
				<li class="fieldcontain">
					<span id="email-label" class="property-label">
						<g:message code="customer.email.label" default="Email" />
					</span>

					<span class="property-value" aria-labelledby="email-label">
						<g:fieldValue bean="${customerInstance}" field="email" />
					</span>

				</li>
			</g:if>

			<g:if test="${customerInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label">
						<g:message code="customer.creationDate.label" default="Creation Date" />
					</span>

					<span class="property-value" aria-labelledby="creationDate-label">
						<g:formatDate date="${customerInstance?.creationDate}" />
					</span>

				</li>
			</g:if>

			<g:if test="${customerInstance?.hosts}">
				<li class="fieldcontain">
					<span id="hosts-label" class="property-label">
						<g:message code="customer.hosts.label" default="Hosts" />
					</span>

					<g:each in="${customerInstance.hosts}" var="h">
						<span class="property-value" aria-labelledby="hosts-label">
							<g:link controller="host" action="show" id="${h.id}">
								${h?.hostname} (${h?.environment})
							</g:link>
						</span>
					</g:each>

				</li>
			</g:if>

		</ol>

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