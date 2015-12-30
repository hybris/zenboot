<%@ page import="org.zenboot.portal.processing.ExecutionZone"%>
<%@ page import="org.zenboot.portal.HostState"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'executionZone.label', default: 'ExecutionZone')}" />
<title>
	<g:message code="default.list.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="list-executionZone" class="content scaffold-list" role="main">

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>


    <div class="row-fluid">
			<g:if test="${!params.favs}">
				<g:link action="list" params="${parameters + [favs: 'show']}" class="btn btn-submit pull-right">
					<g:message code="executionZone.show.favs" default="Show Favs" />
				</g:link>
			</g:if>
			<g:else>
				<g:link action="list" class="btn btn-submit pull-right" params="${parameters - ['favs': 'show']}">
					<g:message code="executionZone.show.all" default="Show All" />
				</g:link>
			</g:else>
    </div>

		<table class="table table-striped">
			<thead>
				<tr>
					<th>
						<g:message code="executionZone.favorite.label" default="Fav" />
					</th>
					<g:sortableColumn property="type.name" title="${message(code: 'executionZone.type.label', default: 'Type')}" params="${parameters}" />
<!--  		<g:sortableColumn property="puppetEnvironment" title="${message(code: 'executionZone.puppetEnvironment.label', default: 'Puppet-Env')}" />
					<g:sortableColumn property="qualityStage" title="${message(code: 'executionZone.qualityStage.label', default: 'Quality-Stage')}" /> -->
					<g:sortableColumn style="width:20%" property="description" title="${message(code: 'executionZone.description.label', default: 'Description')}" params="${parameters}" />
					<th>
						<g:message code="executionZone.parameters.label" default="Parameters" />
					</th>
					<th>
						<g:message code="executionZone.parameters.hosts" default="Hosts (Completed / NotDeleted)"/>
					</th>
					<th>
						<g:message code="executionZone.serviceurls.label" default="ServiceUrls" />
					</th>
					<g:sortableColumn property="creationDate" title="${message(code: 'executionZone.creationDate.label', default: 'Creation Date')}" />
					<g:sortableColumn property="enabled" title="${message(code: 'executionZone.enabled.label', default: 'Enabled')}" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${executionZoneInstanceList}" status="i" var="executionZoneInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}${executionZoneInstance.enabled ?: ' warning'}">
						<td>
							<g:remoteLink action="ajaxUserLike" id="${executionZoneInstance.id}" update="${executionZoneInstance.id}_fav">
								<div id="${executionZoneInstance.id}_fav">
									<g:if test="${executionZoneInstance.userLiked(user)}">
										<i class="icon-star"></i>
									</g:if>
									<g:else>
										<i class="icon-star-empty"></i>
									</g:else>
								</div>
							</g:remoteLink>
						</td>
						<td>
								${fieldValue(bean: executionZoneInstance, field: "type")}
						</td>
<!--        <td>  For now, let's disable this. Might be good to make that configurable
                ${fieldValue(bean: executionZoneInstance, field: "puppetEnvironment")}
            </td>
            <td>
                ${fieldValue(bean: executionZoneInstance, field: "qualityStage")}
            </td>
-->					<td>
						<g:link action="show" id="${executionZoneInstance.id}">
							${fieldValue(bean: executionZoneInstance, field: "description") ?: "NO_DESCRIPTION" }
						</g:link>
						</td>
						<td>
							<g:render template="parametersInList" model="[parameters:executionZoneInstance.processingParameters]"></g:render>
						</td>
						<td>
							${executionZoneInstance.getCompletedHosts().size()} / ${executionZoneInstance.getNonDeletedHosts().size()}
						</td>
						<td>
							<g:render template="serviceurlsInList" model="[serviceUrls:executionZoneInstance.getActiveServiceUrls()]"></g:render>
						</td>
						<td>
							<g:formatDate date="${executionZoneInstance.creationDate}" />
						</td>
						<td>
							<g:if test="${executionZoneInstance.enabled}">
								<i class="icon-ok"></i>
							</g:if>
							<g:else>
								<i class="icon-remove"></i>
							</g:else>
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>

		<fieldset class="buttons spacer">
			<filterpane:filterButton text="Filter" class="btn" />
			<g:link class="btn btn-primary" action="create">
				${message(code: 'default.button.create.label', default: 'Cancel')}
			</g:link>
		</fieldset>

		<filterpane:filterPane domain="ExecutionZone" action="list" formMethod="get"
							   listDistinct="true" uniqueCountColumn="id"
							   filterProperties="description, enabled, hosts, type, processingParameters"
							   associatedProperties="hosts.cname, type.name, processingParameters.name, processingParameters.value"/>
		<filterpane:isFiltered>
			<h4>Current Filters:</h4>
			<filterpane:currentCriteria class="list-group" action="list" domainBean="ExecutionZone"
										fullAssociationPathFieldNames="no"/>
		</filterpane:isFiltered>

		<div class="pagination">
			<filterpane:paginate total="${executionZoneInstanceTotal}" domainBean="ExecutionZone"/>
		</div>
	</div>
</body>
</html>
