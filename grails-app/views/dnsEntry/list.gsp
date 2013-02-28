<%@ page import="org.zenboot.portal.DnsEntry"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'dnsEntry.label', default: 'DnsEntry')}" />
<title>
	<g:message code="default.list.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="list-dnsEntry" class="content scaffold-list" role="main">
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
					<g:sortableColumn property="creationDate" title="${message(code: 'dnsEntry.creationDate.label', default: 'Creation Date')}" />
					<g:sortableColumn property="fqdn" title="${message(code: 'dnsEntry.fqdn.label', default: 'Fqdn')}" />
					<g:sortableColumn property="hostType" title="${message(code: 'dnsEntry.hostType.label', default: 'Host Type')}" />
					<th>
						<g:message code="dnsEntry.owner.label" default="Owner" />
					</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${dnsEntryInstanceList}" status="i" var="dnsEntryInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>
							<g:link action="show" id="${dnsEntryInstance.id}">
								${fieldValue(bean: dnsEntryInstance, field: "creationDate")}
							</g:link>
						</td>
						<td>
							${fieldValue(bean: dnsEntryInstance, field: "fqdn")}
						</td>
						<td>
							${fieldValue(bean: dnsEntryInstance, field: "hostType")}
						</td>
						<td>
							${fieldValue(bean: dnsEntryInstance, field: "owner")}
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>

		<div class="pagination">
			<g:paginate total="${dnsEntryInstanceTotal}" />
		</div>
	</div>
</body>
</html>