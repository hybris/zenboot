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


		<dl class="dl-horizontal">
			<g:if test="${scriptletBatchInstance?.state}">
				<dt>
					<g:message code="scriptletBatch.state.label" default="State" />
				</dt>
				<dd>
					<g:render template="state" model="[scriptletBatchInstance:scriptletBatchInstance]" />
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance?.user?.username}">
				<dt>
					<g:message code="scriptletBatch.user.label" default="Started by user" />
				</dt>
				<dd>
					<g:fieldValue bean="${scriptletBatchInstance?.user}" field="username" />
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance.getProcessTime() >= 0}">
				<dt>
					<g:message code="scriptletBatch.executiontime.label" default="Execution Time" />
				</dt>
				<dd>
					<g:formatNumber number="${scriptletBatchInstance.getProcessTime()/1000}" groupingUsed="true" minFractionDigits="3" />
					sec
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance?.description}">
				<dt>
					<g:message code="scriptletBatch.description.label" default="Description" />
				</dt>
				<dd>
					<g:fieldValue bean="${scriptletBatchInstance}" field="description" />
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance?.startDate}">
				<dt>
					<g:message code="scriptletBatch.startDate.label" default="Start Date" />
				</dt>
				<dd>
					<g:formatDate date="${scriptletBatchInstance?.startDate}" />
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance?.endDate}">
				<dt>
					<g:message code="scriptletBatch.endDate.label" default="End Date" />
				</dt>
				<dd>
					<g:formatDate date="${scriptletBatchInstance?.endDate}" />
				</dd>
			</g:if>
			<g:else>
			  <dt>
			    <g:message code="scriptletBatch.autorefresh" default="Autorefresh" />
			  </dt>
			  <dd>
			    <g:checkBox name="autorefresh" value="${true}" />
			  </dd>

			</g:else>

			<g:if test="${scriptletBatchInstance?.comment}">
			  <hr />
			  <dt>
			    <g:message code="scriptletBatch.comment.label" default="Execution comment" />
			  </dt>
			  <dd>
			    <g:fieldValue bean="${scriptletBatchInstance}" field="comment" />
			  </dd>

			</g:if>


			<g:if test="${scriptletBatchInstance?.executionZoneAction?.executionZone}">
				<hr />
				<dt>
					<g:message code="scriptletBatch.executionZone.label" default="Execution Zone" />
				</dt>
				<dd>
					<g:link controller="executionZone" action="show" id="${scriptletBatchInstance?.executionZoneAction.executionZone.id}">
						${scriptletBatchInstance?.executionZoneAction.executionZone.type.name}
						<g:if test="${scriptletBatchInstance?.executionZoneAction.executionZone.description}">
                               (${scriptletBatchInstance?.executionZoneAction.executionZone.description})
                           </g:if>
					</g:link>
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance?.executionZoneAction}">
				<dt>
					<g:message code="scriptletBatch.executionZoneAction.label" default="Execution Zone Action" />
				</dt>
				<dd>
					<g:link controller="executionZoneAction" action="show" id="${scriptletBatchInstance?.executionZoneAction.id}">
						${scriptletBatchInstance?.executionZoneAction.scriptDir.name} (<g:formatDate date="${scriptletBatchInstance?.executionZoneAction.creationDate}" type="datetime" timeStyle="SHORT" dateStyle="SHORT" />)
					</g:link>
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance?.host}">
				<dt>
					<g:message code="scriptletBatch.host.label" default="Host" />
				</dt>
				<dd>
					<g:link controller="Host" action="show" id="${scriptletBatchInstance?.host?.id}">
						${scriptletBatchInstance?.host.encodeAsHTML()}
					</g:link>
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance?.exceptionClass}">
				<hr />
				<dt>
					<g:message code="scriptletBatch.exceptionClass.label" default="Exception" />
				</dt>
				<dd>
					<strong>
						${scriptletBatchInstance.exceptionClass}:
					</strong>
					<g:if test="${scriptletBatchInstance?.exceptionMessage}">
						<div>
							${scriptletBatchInstance.exceptionMessage}
						</div>
					</g:if>
				</dd>
			</g:if>

			<g:if test="${scriptletBatchInstance?.processables}">
				<hr />

				<div style="padding-left:5%; border:thin">
					<ul class="unstyled" id="steps">
						<g:render template="steps" model="[steps:scriptletBatchInstance.processables]" />
					</ul>
				</div>
				<g:if test="${scriptletBatchInstance?.isRunning() || scriptletBatchInstance?.isWaiting()}">
					<span id="stepsSpinner" class="property-value">
						<img src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
					</span>
					<asset:script>
						$(window).load(function() {
                            var update = function() {
                                if (! $('#autorefresh').is(':checked')) {
                                    return;
                                }

                                $.ajax({
                                    url: '<g:createLink action="ajaxSteps" params="[scriptletId:scriptletBatchInstance?.id]" />',
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
                            }

                            var interval = setInterval(update, 1500);
                        });
					</asset:script>
				</g:if>
			</g:if>

		</dl>
		<div style="padding-left:5%; border:thin">
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${scriptletBatchInstance?.id}" />
					<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					<g:actionSubmit class="btn btn-primary" action="rerun" value="${message(code: 'default.button.rerun.label', default: 'Rerun')}" />
				</fieldset>
			</g:form>
		</div>
	</div>

	<asset:script>
	$(document).ready(function() {
        zenboot.enableTooltip();
	});
	</asset:script>
</body>
</html>
