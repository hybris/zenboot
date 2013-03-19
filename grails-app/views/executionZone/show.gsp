<%@ page defaultCodec="none"%>
<%@ page import="org.zenboot.portal.processing.ExecutionZone"%>
<%@ page import="org.zenboot.portal.security.Role"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'executionZone.label', default: 'ExecutionZone')}" />
<title>
	<g:message code="default.show.label" args="[entityName]" />
</title>
<r:require module="epiceditor" />
</head>
<body>
	<div id="show-executionZone" class="content scaffold-show" role="main">
		<h2 class="page-header">
			<g:message code="default.show.label" args="[entityName]" />
		</h2>

		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

		<g:hasErrors bean="${cmd}">
			<div class="alert alert-error">
				<g:renderErrors bean="${cmd}" />
			</div>
		</g:hasErrors>

		<g:if test="${!executionZoneInstance?.enabled}">
			<div class="alert alert-warning">
				<g:message code="executionZone.type.disabled" default="Execution zone type is disabled" />
			</div>
		</g:if>

		<g:link action="list">
			<i class="icon-list"></i>
			<g:message code="default.button.list.label" default="Back to overview" />
		</g:link>

		<g:form method="post">
			<ol class="property-list executionZone">
				<g:if test="${executionZoneInstance?.type}">
					<li class="fieldcontain">
						<span id="type-label" class="property-label">
							<g:message code="executionZone.type.label" default="Type" />
						</span>
						<span class="property-value" aria-labelledby="type-label">
							<g:link controller="executionZoneType" action="show" id="${executionZoneInstance?.type?.id}">
								${executionZoneInstance?.type}
							</g:link>
						</span>
					</li>
				</g:if>

				<g:if test="${executionZoneInstance?.actions}">
					<li class="fieldcontain">
						<span id="actions-label" class="property-label">
							<g:message code="executionZone.actions.label" default="Actions" />
						</span>
						<div class="property-value collapsable-list" aria-labelledby="actions-label">
							<a class="collapsed" style="cursor: pointer">
								<g:message code="executionZone.actions.size" default="{0} actions defined" args="[executionZoneInstance.actions.size()]" />
								<i class="icon-resize-full"></i>
							</a>
							<ul class="unstyled hide">
								<g:each in="${executionZoneInstance.actions}" var="a" status="status">
									<li>
										<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
											<g:link controller="executionZoneAction" action="show" id="${a.id}">
												${a.scriptDir.name} (<g:formatDate type="datetime" style="MEDIUM" timeStyle="SHORT" date="${a.creationDate}"/>)
											</g:link>
										</sec:ifAllGranted>
										<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
											${a.scriptDir.name} (<g:formatDate type="datetime" style="MEDIUM" timeStyle="SHORT" date="${a.creationDate}"/>)
										</sec:ifNotGranted>
									</li>
								</g:each>
							</ul>
						</div>
					</li>
				</g:if>

				<g:if test="${executionZoneInstance?.creationDate}">
					<li class="fieldcontain">
						<span id="creationDate-label" class="property-label">
							<g:message code="executionZone.creationDate.label" default="Creation Date" />
						</span>
						<span class="property-value" aria-labelledby="creationDate-label">
							<g:formatDate date="${executionZoneInstance?.creationDate}" />
						</span>
					</li>
				</g:if>

                <g:if test="${executionZoneInstance?.puppetEnvironment}">
                    <li class="fieldcontain">
                        <span id="puppetEnvironment-label" class="property-label">
                            <g:message code="executionZone.puppetEnvironment.label" default="Puppet-Environment" />
                        </span>
                        <span class="property-value" aria-labelledby="puppetEnvironment-label">
                            <g:fieldValue bean="${executionZoneInstance}" field="puppetEnvironment" />
                        </span>
                    </li>
                </g:if>

				<g:if test="${executionZoneInstance?.description}">
					<li class="fieldcontain">
						<span id="description-label" class="property-label">
							<g:message code="executionZone.description.label" default="Description" />
						</span>
						<span class="property-value" aria-labelledby="description-label">
							<g:fieldValue bean="${executionZoneInstance}" field="description" />
						</span>
					</li>
				</g:if>

				<li class="fieldcontain">
					<span id="enabled-label" class="property-label">
						<g:message code="executionZone.enabled.label" default="Enabled" />
					</span>
					<span class="property-value" aria-labelledby="enabled-label">
						<g:checkBox name="enabled" checked="${executionZoneInstance.enabled}" disabled="true" />
					</span>
				</li>

				<g:if test="${executionZoneInstance?.enableExposedProcessingParameters}">
                    <li class="fieldcontain">
                        <span id="description-label" class="property-label">
                            <g:message code="executionZone.enableExposedProcessingParameters.label" default="Support exposed parameters" />
                        </span>
                        <span class="property-value" aria-labelledby="enableExposedProcessingParameters-label">
                            <g:checkBox name="enableExposedProcessingParameters" checked="${executionZoneInstance.enableExposedProcessingParameters}" disabled="true" />
                        </span>
                    </li>
                </g:if>

				<g:if test="${cmd}">
					<g:set value="${cmd.scriptDir}" var="selectedScriptDir" />
				</g:if>
				<g:elseif test="${!scriptDirs.empty}">
					<g:set value="${scriptDirs.iterator().next()}" var="selectedScriptDir" />
				</g:elseif>

				<li class="fieldcontain">
					<span id="scriptDirs-label" class="property-label">
						<g:message code="executionZone.scriptDirs.label" default="ScriptDirs" />
					</span>
					<div class="property-value" aria-labelledby="scriptDirs-label" id="scriptDirs">
						<g:each var="scriptDir" in="${scriptDirs}" status="i">
							<div style="margin-bottom: 10px;">
								<g:radio name="scriptDir" id="scriptdir-${i}" value="${scriptDir}" checked="${scriptDir == selectedScriptDir}" />
								${scriptDir.name}
								<g:remoteLink action="ajaxGetReadme" params="[scriptDir:scriptDir, editorId:"editor_${i}"]" update="scriptdir-${i}_readme" before="if (!zenboot.prepareAjaxLoading('scriptdir-${i}_readme', 'scriptdir-${i}_spinner')) return false" after="zenboot.finalizeAjaxLoading('scriptdir-${i}_readme', 'scriptdir-${i}_spinner');" asynchronous="false">
									<i class="icon-book"></i>
								</g:remoteLink>
								<g:remoteLink action="ajaxGetFlowChart" params="[scriptDir:scriptDir]" update="scriptdir-${i}_flow" before="if (!zenboot.prepareAjaxLoading('scriptdir-${i}_flow', 'scriptdir-${i}_spinner')) return false" after="zenboot.finalizeAjaxLoading('scriptdir-${i}_flow', 'scriptdir-${i}_spinner');" asynchronous="false">
									<i class="icon-search"></i>
								</g:remoteLink>
								<span id="scriptdir-${i}_spinner" class="hide">
									<img src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
								</span>
								<div id="scriptdir-${i}_readme" class="hide"></div>
								<div id="scriptdir-${i}_flow" class="hide flow-chart"></div>
							</div>
						</g:each>
					</div>
				</li>

				<li class="fieldcontain">
					<span id="parameters-label" class="property-label">
						<g:message code="executionZone.parameters.label" default="Parameters" />
					</span>
					<span id="parametersSpinner" class="property-value" style="display:none">
						<img src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
					</span>
					<div class="property-value" aria-labelledby="parameters-label" id="parameters">
						<g:include action="ajaxGetParameters" params="[scriptDir:selectedScriptDir, id:executionZoneInstance?.id]" />
					</div>
				</li>
			</ol>

			<g:hiddenField name="id" value="${executionZoneInstance?.id}" />

			<fieldset class="spacer buttons">
				<g:link class="btn btn-primary" action="edit" id="${executionZoneInstance?.id}">
					<g:message code="default.button.edit.label" default="Edit" />
				</g:link>
				<g:actionSubmit class="btn btn-success" action="execute" value="${message(code: 'executionZone.button.executeExecutionZone.label', default: 'Execute Zone')}" disabled="${!executionZoneInstance?.enabled}" />
				<span style="margin-left: 20px;">&nbsp;</span>
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" disabled="${!executionZoneInstance?.enabled}" />
				
				
				<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
					<g:actionSubmit class="btn btn-inverse" action="createExposedAction" value="${message(code: 'executionZone.button.createExposedAction.label', default: 'Expose Action')}" disabled="${!executionZoneInstance?.enabled}" />
				</sec:ifAllGranted>
			</fieldset>
		</g:form>
	</div>

	<g:javascript>
    $('#scriptDirs input:radio').change(function(event) {
        $.ajax({
            url : '<g:createLink action="ajaxGetParameters" params="[id:executionZoneInstance?.id]" />?scriptDir=' + encodeURI($(this).val()),
            beforeSend : function() {
                $('#scriptDirs input:radio').attr("disabled", "disabled")
                $('#parameters').slideUp('fast')
                $('#parametersSpinner').fadeIn('fast')
            },
            success: function(data) {
            	$('#parametersSpinner').hide()
            	$('#parameters').html(data).slideDown('slow');
            	$('#scriptDirs input:radio').removeAttr('disabled')
            },
            error: function(jqHXR, status, error) {
	        	$('#parametersSpinner').hide()
	        	$("#parameters").html('<div class="alert alert-error">' + error + '</div>');
	        	$('#scriptDirs input:radio').removeAttr('disabled')
            }
        });
    });
	</g:javascript>
</body>
</html>