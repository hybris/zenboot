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
      <g:if test="${!params.disabled}">
        <g:link action="list" params="[disabled: 'show']" class="btn btn-submit pull-right">
          <g:message code="executionZone.show.disabled" default="Show disabled" />
        </g:link>
      </g:if>
      <g:else>
        <g:link action="list" class="btn btn-submit pull-right">
          <g:message code="executionZone.show.enabled" default="Show enabled" />
        </g:link>
      </g:else>

			<g:if test="${!params.favs}">
				<g:link action="list" params="[favs: 'show']" class="btn btn-submit pull-right">
					<g:message code="executionZone.show.favs" default="Show Favs" />
				</g:link>
			</g:if>
			<g:else>
				<g:link action="list" class="btn btn-submit pull-right">
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
					<g:sortableColumn property="type.name" title="${message(code: 'executionZone.type.label', default: 'Type')}" />
<!--  		<g:sortableColumn property="puppetEnvironment" title="${message(code: 'executionZone.puppetEnvironment.label', default: 'Puppet-Env')}" />
					<g:sortableColumn property="qualityStage" title="${message(code: 'executionZone.qualityStage.label', default: 'Quality-Stage')}" />
--> 			<g:sortableColumn style="width:20%" property="description" title="${message(code: 'executionZone.description.label', default: 'Description')}" />
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

							<g:if test="${executionZoneInstance.userLiked(user)}">
								<g:remoteLink action="ajaxUserLike" id="${executionZoneInstance.id}" update="${executionZoneInstance.id}_fav">
									<div id="${executionZoneInstance.id}_fav">
										<i class="icon-star"></i>
									</div>
								</g:remoteLink>

							</g:if>
							<g:else>
								<g:remoteLink action="ajaxUserLike" id="${executionZoneInstance.id}" update="${executionZoneInstance.id}_fav">
									<div id="${executionZoneInstance.id}_fav">
										<i class="icon-star-empty"></i>
									</div>
								</g:remoteLink>
							</g:else>

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
			<g:link class="btn btn-primary" action="create">
				${message(code: 'default.button.create.label', default: 'Cancel')}
			</g:link>
		</fieldset>

		<div class="pagination">
			<g:paginate total="${executionZoneInstanceTotal}" max="1" params="${parameters}"/>
		</div>
	</div>
</body>
</html>
