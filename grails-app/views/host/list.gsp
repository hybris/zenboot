<%@ page import="org.zenboot.portal.Host"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'host.label', default: 'Host')}" />
<title>
	<g:message code="default.list.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="list-host" class="content scaffold-list" role="main">
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
					<g:sortableColumn property="ipAddress" title="${message(code: 'host.ipAddress.label', default: 'Ip Address')}" />
					<g:sortableColumn property="cname" title="${message(code: 'host.cname.label', default: 'Cname')}" />
					<g:sortableColumn property="macAddress" title="${message(code: 'host.macAddress.label', default: 'Mac Address')}" />
					<g:sortableColumn property="hostname.name" title="${message(code: 'host.hostname.label', default: 'Hostname')}" />
					<g:sortableColumn property="instanceId" title="${message(code: 'host.instanceId.label', default: 'Instance Id')}" defaultOrder="desc"/>
					<g:sortableColumn property="state" title="${message(code: 'host.state.label', default: 'State')}" />
					<g:sortableColumn property="expiryDate" title="${message(code: 'host.state.label', default: 'Expiry Date')}" />
					<th>
						<g:message code="host.type.label" default="Type" />
					</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${hostInstanceList}" status="i" var="hostInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>
							<g:link action="show" id="${hostInstance.id}">
								${fieldValue(bean: hostInstance, field: "ipAddress")}
							</g:link>
						</td>
						<td>
							${fieldValue(bean: hostInstance, field: "cname")}
						</td>
                        <td>
							${fieldValue(bean: hostInstance, field: "macAddress")}
						</td>
						<td>
							${fieldValue(bean: hostInstance, field: "hostname")}
						</td>
						<td>
							${fieldValue(bean: hostInstance, field: "instanceId")}
						</td>
						<td>
							${fieldValue(bean: hostInstance, field: "state")}
						</td>
						<td>
							${fieldValue(bean: hostInstance, field: "expiryDate")}
						</td>
						<td>
							${hostInstance.class.getSimpleName()}
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>

		<div class="pagination">
			<g:paginate total="${hostInstanceTotal}" max="1" />
		</div>
	</div>
</body>
</html>
