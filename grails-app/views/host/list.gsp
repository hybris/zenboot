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


		<g:if test="${params.max > 20 && hostInstanceTotal > 20}">
			<div class="pagination">
				<g:paginate total="${hostInstanceTotal}" max="1" params="${parameters}"/>
			</div>
		</g:if>
		<filterpane:filterPane domain="Host" action="list" formMethod="get" associatedProperties="execZone.description, execZone.id" />
		<filterpane:filterButton text="Filter" />
		<filterpane:currentCriteria domainBean="Host" action="list" fullAssociationPathFieldNames="no" />

		<table class="table table-striped">
			<thead>
				<tr>
					<g:sortableColumn property="ipAddress" title="${message(code: 'host.ipAddress.label', default: 'Ip Address')}" params="${parameters}"/>
					<g:sortableColumn property="cname" title="${message(code: 'host.cname.label', default: 'Cname')}" params="${parameters}"/>
					<g:sortableColumn property="hostname.name" title="${message(code: 'host.hostname.label', default: 'Hostname')}"  params="${parameters}"/>
					<g:sortableColumn property="instanceId" title="${message(code: 'host.instanceId.label', default: 'Instance Id')}" defaultOrder="desc" params="${parameters}"/>
					<g:sortableColumn property="state" title="${message(code: 'host.state.label', default: 'State')}" params="${parameters}"/>
					<g:sortableColumn property="expiryDate" title="${message(code: 'host.expiryDate.label', default: 'Expiry Date')}" params="${parameters}"/>
					<g:sortableColumn property="datacenter" title="${message(code: 'host.datacenter.label', default: 'Datacenter')}" params="${parameters}"/>
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
							${fieldValue(bean: hostInstance, field: "datacenter")}
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>

		<div class="pagination">
			<g:paginate total="${hostInstanceTotal}" max="1" params="${parameters}"/>
		</div>
	</div>
</body>
</html>
