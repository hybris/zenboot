<%@ page import="org.zenboot.portal.Customer"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
<title>
	<g:message code="default.list.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="list-customer" class="content scaffold-list" role="main">
		<h2 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<table class="table table-striped">
			<thead>
				<tr>
					<g:sortableColumn property="email" title="${message(code: 'customer.email.label', default: 'Email')}" />
					<g:sortableColumn property="creationDate" title="${message(code: 'customer.creationDate.label', default: 'Creation Date')}" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${customerInstanceList}" status="i" var="customerInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>
							<g:link action="show" id="${customerInstance.id}">
								${fieldValue(bean: customerInstance, field: "email")}
							</g:link>
						</td>
						<td>
							<g:formatDate date="${customerInstance.creationDate}" />
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>

		<div class="pagination">
			<g:paginate total="${customerInstanceTotal}" />
		</div>
	</div>
</body>
</html>