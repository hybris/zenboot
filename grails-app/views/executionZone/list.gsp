<%@ page import="org.zenboot.portal.processing.ExecutionZone"%>
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
    </div>

		<table class="table table-striped">
			<thead>
				<tr>
					<g:sortableColumn property="type.name" title="${message(code: 'executionZone.type.label', default: 'Type')}" />
					<g:sortableColumn property="creationDate" title="${message(code: 'executionZone.creationDate.label', default: 'Creation Date')}" />
					<g:sortableColumn property="puppetEnvironment" title="${message(code: 'executionZone.puppetEnvironment.label', default: 'Puppet-Env')}" />
					<g:sortableColumn property="qualityStage" title="${message(code: 'executionZone.qualityStage.label', default: 'Quality-Stage')}" />
					<g:sortableColumn style="width:20%" property="description" title="${message(code: 'executionZone.description.label', default: 'Description')}" />
					<th style="width: 45%">
						<g:message code="executionZone.parameters.label" default="Parameters" />
					</th>
					<g:sortableColumn property="enabled" title="${message(code: 'executionZone.enabled.label', default: 'Enabled')}" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${executionZoneInstanceList}" status="i" var="executionZoneInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}${executionZoneInstance.enabled ?: ' warning'}">
						<td>
							<g:link action="show" id="${executionZoneInstance.id}">
								${fieldValue(bean: executionZoneInstance, field: "type")}
							</g:link>
						</td>
						<td>
							<g:formatDate date="${executionZoneInstance.creationDate}" />
						</td>
                        <td>
                            ${fieldValue(bean: executionZoneInstance, field: "puppetEnvironment")}
                        </td>
                        <td>
                            ${fieldValue(bean: executionZoneInstance, field: "qualityStage")}
                        </td>
						<td>
							${fieldValue(bean: executionZoneInstance, field: "description")}
						</td>
						<td>
							<g:render template="parametersInList" model="[parameters:executionZoneInstance.processingParameters]"></g:render>
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
			<g:paginate total="${executionZoneInstanceTotal}" />
		</div>
	</div>
</body>
</html>
