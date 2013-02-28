<%@ page import="org.zenboot.portal.processing.ScriptletBatch"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'scriptletBatch.label', default: 'ScriptletBatch')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
</head>
<body>
	<div id="show-scriptletBatch" class="content scaffold-show" role="main">
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

		<ol class="property-list scriptletBatch">
			<g:if test="${scriptletBatchInstance?.state}">
				<li class="fieldcontain">
					<span id="state-label" class="property-label">
						<g:message code="scriptletBatch.state.label" default="State" />
					</span>
					<span class="property-value" aria-labelledby="state-label">
						<g:render template="state" model="[scriptletBatchInstance:scriptletBatchInstance]" />
					</span>
				</li>
			</g:if>

			<g:if test="${scriptletBatchInstance.getProcessTime() >= 0}">
				<li class="fieldcontain">
					<span id="executiontime-label" class="property-label">
						<g:message code="scriptletBatch.executiontime.label" default="Execution Time" />
					</span>
					<span class="property-value" aria-labelledby="executiontime-label">
						<g:formatNumber number="${scriptletBatchInstance.getProcessTime()/1000}" groupingUsed="true" minFractionDigits="3" />
						sec
					</span>
				</li>
			</g:if>

			<g:if test="${scriptletBatchInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label">
						<g:message code="scriptletBatch.description.label" default="Description" />
					</span>
					<span class="property-value" aria-labelledby="description-label">
						<g:fieldValue bean="${scriptletBatchInstance}" field="description" />
					</span>
				</li>
			</g:if>

			<g:if test="${scriptletBatchInstance?.startDate}">
				<li class="fieldcontain">
					<span id="startDate-label" class="property-label">
						<g:message code="scriptletBatch.startDate.label" default="Start Date" />
					</span>
					<span class="property-value" aria-labelledby="startDate-label">
						<g:formatDate date="${scriptletBatchInstance?.startDate}" />
					</span>
				</li>
			</g:if>

			<g:if test="${scriptletBatchInstance?.endDate}">
				<li class="fieldcontain">
					<span id="endDate-label" class="property-label">
						<g:message code="scriptletBatch.endDate.label" default="End Date" />
					</span>
					<span class="property-value" aria-labelledby="endDate-label">
						<g:formatDate date="${scriptletBatchInstance?.endDate}" />
					</span>
				</li>
			</g:if>

			<g:if test="${scriptletBatchInstance?.executionZoneAction.executionZone}">
				<li class="fieldcontain">
					<hr />
					<span id="executionZone-label" class="property-label">
						<g:message code="scriptletBatch.executionZone.label" default="Execution Zone" />
					</span>
					<span class="property-value" aria-labelledby="executionZone-label">
						<g:link controller="executionZone" action="show" id="${scriptletBatchInstance?.executionZoneAction.executionZone.id}">
							${scriptletBatchInstance?.executionZoneAction.executionZone.type.name}
							<g:if test="${scriptletBatchInstance?.executionZoneAction.executionZone.description}">
                                (${scriptletBatchInstance?.executionZoneAction.executionZone.description})
                            </g:if>
						</g:link>
					</span>
				</li>
			</g:if>

			<g:if test="${scriptletBatchInstance?.executionZoneAction}">
				<li class="fieldcontain">
					<span id="executionZoneAction-label" class="property-label">
						<g:message code="scriptletBatch.executionZoneAction.label" default="Execution Zone Action" />
					</span>
					<span class="property-value" aria-labelledby="executionZoneAction-label">
						<g:link controller="executionZoneAction" action="show" id="${scriptletBatchInstance?.executionZoneAction.id}">
							${scriptletBatchInstance?.executionZoneAction.scriptDir.name} (<g:formatDate date="${scriptletBatchInstance?.executionZoneAction.creationDate}" type="datetime" timeStyle="SHORT" dateStyle="SHORT" />)
						</g:link>
					</span>
				</li>
			</g:if>

			<g:if test="${scriptletBatchInstance?.exceptionClass}">
				<li class="fieldcontain">
					<hr />
					<span id="exception-label" class="property-label">
						<g:message code="scriptletBatch.exceptionClass.label" default="Exception" />
					</span>
					<div class="property-value alert alert-error" aria-labelledby="exceptionClass-label">
						<strong>
							${scriptletBatchInstance.exceptionClass}:
						</strong>
						<g:if test="${scriptletBatchInstance?.exceptionMessage}">
							<div>
								${scriptletBatchInstance.exceptionMessage}
							</div>
						</g:if>
					</div>
				</li>
			</g:if>

			<g:if test="${scriptletBatchInstance?.processables}">
				<li class="fieldcontain">
					<hr />
					<span id="processables-label" class="property-label">
						<g:message code="scriptletBatch.processables.label" default="Steps" />
					</span>
					<div>
						<ul class="unstyled" id="steps">
							<g:render template="steps" model="[steps:scriptletBatchInstance.processables]" />
						</ul>
					</div>
					<g:if test="${scriptletBatchInstance?.isRunning()}">
						<span id="stepsSpinner" class="property-value">
							<img src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
						</span>
						<g:javascript>
					    $(document).ready(function() {
                            var interval = setInterval(function() {
					            $.ajax({
					                url : '<g:createLink action="ajaxSteps" params="[id:scriptletBatchInstance?.id]" />',
					                contentType: 'application/json',
					                dataType: 'json'
					            }).success(function(data) {
					               var steps = $("#steps li")
					            
					               if (steps.size() != data.length) {
					                   alert("Not able to update process list. Different number of steps between server-managed list and the shown list!")
					                   clearInterval(interval);
					                   return;
					               }

					               for (i = 0; i < data.length; i++) {
					                   //update running steps  
					                   if ($(steps[i]).find("span.label").hasClass("label-info") || data[i].status == "RUNNING") {
					                       $(steps[i]).replaceWith(data[i].markup);
					                   }
					               }
					            }).error(function() {
                                    clearInterval(interval);
					                window.location.reload();
					            });
					        }, 1500);
					    });
					    </g:javascript>
					</g:if>
				</li>
			</g:if>
		</ol>

		<g:form>
			<fieldset class="buttons">
				<g:hiddenField name="id" value="${scriptletBatchInstance?.id}" />
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
			</fieldset>
		</g:form>
	</div>

	<g:javascript>
	$(document).ready(function() {
        zenboot.enableTooltip();
	});
	</g:javascript>
</body>
</html>