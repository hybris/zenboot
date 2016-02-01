<%@ page defaultCodec="none"%>
<%@ page import="org.zenboot.portal.processing.ExecutionZone"%>
<%@ page import="org.zenboot.portal.security.Role"%>
<%@ page import="org.zenboot.portal.HostState"%>
<%@ page import="org.joda.time.Period"%>

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

		<div class="accordion" id="execution-show-accordion">
			<div class="accordion-group">
				<div class="accordion-heading">
      		<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#showZone">
        		<g:message code="default.show.label" args="[entityName]" /> (${executionZoneInstance?.type} : ${executionZoneInstance?.description} )
      		</a>
    		</div>
    		<div id="showZone" class="accordion-body collapse">
    			<div class="accordion-inner">
						<g:form method="post"><!-- why is this a form??? -->
							<dl class="dl-horizontal">
								<g:if test="${executionZoneInstance?.type}">
									<dt>
										<g:message code="executionZone.type.label" default="Type" />
									</dt>
									<dd>
											<g:link controller="executionZoneType" action="show" id="${executionZoneInstance?.type?.id}">
												${executionZoneInstance?.type}
											</g:link>
									</dd>
								</g:if>

								<g:if test="${executionZoneInstance?.actions}">
									<dt>
										<g:message code="executionZone.actions.label" default="Actions" />
									</dt>
									<dd class="collapsable-list">
										<a class="collapsed" style="cursor: pointer">
											<g:message code="executionZone.actions.size" default="{0} actions defined" args="[executionZoneInstance.actions.size()]" />
											<i class="icon-resize-full"></i>
										</a>
										<ul class="unstyled hide">
											<g:each in="${executionZoneInstance.actions}" var="a" status="status">
												<li>
													<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
														<g:link controller="executionZoneAction" action="show" id="${a.id}">
															${a.toString()}
														</g:link>
													</sec:ifAllGranted>
													<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
														${a.toString()}
													</sec:ifNotGranted>
												</li>
											</g:each>
										</ul>
									</dd>
								</g:if>

								<g:if test="${executionZoneInstance?.hosts}">
									<dt>
										<g:message code="executionZone.hosts.label" default="Hosts" />
									</dt>
									<dd>
										<g:render template="hostList" model="['state': HostState.COMPLETED, 'executionZone': executionZoneInstance]" />
										<g:render template="hostList" model="['state': HostState.CREATED, 'executionZone': executionZoneInstance]" />
									</dd>
								</g:if>

								<g:if test="${executionZoneInstance?.creationDate}">
									<dt>
										<g:message code="executionZone.creationDate.label" default="Creation Date" />
									</dt>
									<dd>
										<g:formatDate date="${executionZoneInstance?.creationDate}" />
									</dd>
								</g:if>

				        <g:if test="${executionZoneInstance?.puppetEnvironment}">
				        	<dt>
				        		<g:message code="executionZone.puppetEnvironment.label" default="Puppet-Environment" />
				        	</dt>
				        	<dd>
				        		 <g:fieldValue bean="${executionZoneInstance}" field="puppetEnvironment" />
				        	</dd>
				        	<dt>
				        		<g:message code="executionZone.qualityStage.label" default="Quality-Stage" />
				        	</dt>
				        	<dd>
				        		<g:fieldValue bean="${executionZoneInstance}" field="qualityStage" />
				        	</dd>
				        </g:if>

				        <g:if test="${executionZoneInstance?.description}">
				        	<dt>
				        		<g:message code="executionZone.description.label" default="Description" />
				        	</dt>
				        	<dd>
				        		<g:fieldValue bean="${executionZoneInstance}" field="description" />
				        	</dd>
				        </g:if>

				        <dt>
				        	<g:message code="executionZone.enabled.label" default="Enabled" />
				        </dt>
								<dd>
									<g:checkBox name="enabled" checked="${executionZoneInstance.enabled}" disabled="true" />
								</dd>

								<g:if test="${executionZoneInstance?.enableExposedProcessingParameters}">
									<dt>
										<g:message code="executionZone.enableExposedProcessingParameters.label" default="Support exposed parameters" />
									</dt>
									<dd>
										<g:checkBox name="enableExposedProcessingParameters" checked="${executionZoneInstance.enableExposedProcessingParameters}" disabled="true" />
									</dd>

								</g:if>

								<dt>
									<g:message code="executionZone.hostLimit.label" default="Host Limit" />
								</dt>
								<dd>
									<g:fieldValue bean="${executionZoneInstance}" field="hostLimit" />
								</dd>

								<g:if test="${executionZoneInstance?.enableAutodeletion}">
									<dt>
										<g:message code="executionZone.enableAutodeletion.label" default="Enable Autodeletion" />
									</dt>
									<dd>
										<g:checkBox name="enableAutodeletion" checked="${executionZoneInstance.enableAutodeletion}" disabled="true" />
									</dd>
									<dt>
										<g:message code="executionZone.defaultLifetime.label" default="Default Lifetime" />
									</dt>
									<dd>
										<joda:formatPeriod value="${new Period(executionZoneInstance.defaultLifetime == null ? 0 : executionZoneInstance.defaultLifetime*1000*60)}" fields="months,weeks,days,hours,minutes" />
									</dd>
								</g:if>

							</dl>

							<g:hiddenField name="execId" value="${executionZoneInstance?.id}" />

							<fieldset class="spacer buttons">
								<filterpane:filterLink controller="ScriptletBatch" action="list" class="btn"
													   values="['executionZoneAction.executionZone.id': executionZoneInstance?.id]">
									<g:message code="executionZone.showExecutedActions.label" default="Log" />
                                </filterpane:filterLink>
								<filterpane:filterLink controller="Host" action="list" class="btn"
													   values="['execZone.id': executionZoneInstance?.id]">
									<g:message code="executionZone.showExecutedActions.label" default="Hosts" />
								</filterpane:filterLink>
							</fieldset>

						</g:form>
					</div>
				</div>
			</div>
			<div class="accordion-group">
				<div class="accordion-heading">
					<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#execZoneUrls">
						<g:message code="executionZone.execZoneUrls.label" default="Service Urls" />
					</a>
				</div>
				<div id="execZoneUrls" class="accordion-body collapse in">
					<div id="zoneUrls" class="accordion-inner">
						<g:each in="${executionZoneInstance.getActiveServiceUrls()}" var="serviceUrl">
							<a href="${serviceUrl.url}" target="_blank">${serviceUrl.url}</a> &nbsp; &nbsp;
						</g:each>
					</div>
				</div>
			</div>
			<div class="accordion-group">
				<div class="accordion-heading">
					<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#executeScript">
        		<g:message code="executionZone.execAction.label" default="Execute Script" />
      		</a>
				</div>
				 <div id="executeScript" class="accordion-body collapse ${flash.action == 'update' ? '' : 'in'}">
				 	<div id="scriptDirs" class="accordion-inner">
				 		<g:form method="post">

				 			<g:if test="${cmd}">
								<g:set value="${cmd.scriptDir}" var="selectedScriptDir" />
							</g:if>
							<g:elseif test="${!scriptDirs.empty}">
								<g:set value="${scriptDirs.iterator().next()}" var="selectedScriptDir" />
							</g:elseif>

							<ul class="nav nav-tabs" role="tablist">
							  <li class="<g:if test="${structuredScriptDirs.create.size() > 0}">active</g:if>"><a href="#sd-create" data-toggle="tab">Create</a></li>
							  <li><a href="#sd-update" data-toggle="tab">Update</a></li>
							  <li><a href="#sd-delete" data-toggle="tab">Delete</a></li>
								<li class="<g:if test="${structuredScriptDirs.create.size() == 0}">active</g:if>"><a href="#sd-misc" data-toggle="tab">Misc</a></li>
							</ul>

							<div class="tab-content">
							  <div class="tab-pane <g:if test="${structuredScriptDirs.create.size() > 0}">active</g:if>" id="sd-create">
									<g:render template="scriptDirs" model="['scriptDirs':structuredScriptDirs.create, type:'create', execId: executionZoneInstance?.id]"/>
								</div>
							  <div class="tab-pane" id="sd-update">
									<g:render template="scriptDirs" model="['scriptDirs':structuredScriptDirs.update, type:'update', execId: executionZoneInstance?.id]"/>
								</div>
							  <div class="tab-pane" id="sd-delete">
									<g:render template="scriptDirs" model="['scriptDirs':structuredScriptDirs.delete, type:'delete', execId: executionZoneInstance?.id]"/>
								</div>
							  <div class="tab-pane <g:if test="${structuredScriptDirs.create.size() == 0}">active</g:if>" id="sd-misc">
									<g:render template="scriptDirs" model="['scriptDirs':structuredScriptDirs.misc, type:'misc', execId: executionZoneInstance?.id]"/>
								</div>
							</div>


							<h3>
								<g:message code="executionZone.parameters.label" default="Parameters" />
							</h3>

							<span id="parametersSpinner" style="display:none" >
								<img src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
							</span>

							<div class="property-value" aria-labelledby="parameters-label" id="parameters">
								<g:include action="ajaxGetParameters" params="[scriptDir:selectedScriptDir, execId:executionZoneInstance?.id]" />
							</div>

				 			<g:hiddenField name="execId" value="${executionZoneInstance?.id}" />

				 			<hr />
				 			<div class="row-fluid">
  				 			<g:textArea name="comment" style="height: 150px; width: 100%; white-space: nowrap; overflow: auto;" placeholder="${message(code: 'executionZone.comment.label', default: 'Execution comment')}" />
				 			</div>

				 			<fieldset class="spacer buttons">

				 				<g:actionSubmit class="btn btn-success" action="execute" value="${message(code: 'executionZone.button.executeExecutionZone.label', default: 'Execute Zone')}" disabled="${!executionZoneInstance?.enabled}" />
				 				<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
									<g:actionSubmit class="btn btn-inverse" action="createExposedAction" value="${message(code: 'executionZone.button.createExposedAction.label', default: 'Expose Action')}" disabled="${!executionZoneInstance?.enabled}" />
								</sec:ifAllGranted>
				 			</fieldset>
				 		</g:form>
				 	</div>
				 </div>
			</div>
			<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#editExecZone">
	        				<g:message code="executionZone.editExecZone.label" default="Edit Execution Zone and Parameters" />
	      				</a>
					</div>
					 <div id="editExecZone" class="accordion-body collapse ${flash.action == 'update' ? 'in' : ''}">
					 	<div class="accordion-inner">

							<g:hasErrors bean="${executionZoneInstance}">
								<ul class="alert alert-error" role="alert">
									<g:eachError bean="${executionZoneInstance}" var="error">
										<li>
											<g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>
											<g:message error="${error}" />
										</li>
									</g:eachError>
								</ul>
							</g:hasErrors>

							<g:form method="post">
								<g:hiddenField name="id" value="${executionZoneInstance?.id}" />
								<g:hiddenField name="version" value="${executionZoneInstance?.version}" />
								<fieldset class="form-horizontal">
									<g:render template="form" />
								</fieldset>
								<fieldset class="buttons spacer">
									<g:actionSubmit class="btn btn-primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
								</fieldset>
							</g:form>
					 	</div>
					 </div>
				</div>
			</sec:ifAllGranted>
			<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#userEditParams">
									<g:message code="executionZone.userEditParams.label" default="Editing Execution Zone Parameters" />
								</a>
					</div>
					<div id="userEditParams" class="accordion-body collapse ${flash.action == 'update' ? 'in' : ''}">
						<div class="accordion-inner">
							<g:hasErrors bean="${executionZoneInstance}">
								<ul class="alert alert-error" role="alert">
									<g:eachError bean="${executionZoneInstance}" var="error">
										<li>
											<g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>
											<g:message error="${error}" />
										</li>
									</g:eachError>
								</ul>
							</g:hasErrors>

							<g:form method="post">
								<g:hiddenField name="id" value="${executionZoneInstance?.id}" />
								<g:hiddenField name="version" value="${executionZoneInstance?.version}" />
								<fieldset class="form-horizontal">
									<g:render template="showUserEditParams" />
								</fieldset>
								<fieldset class="buttons spacer">
									<g:actionSubmit class="btn btn-primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
								</fieldset>
							</g:form>
						</div>
					</div>
				</div>
			</sec:ifNotGranted>
			<div class="accordion-group">
				<div class="accordion-heading">
					<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#manageTemplates">
        				<g:message code="executionZone.manageTemplates.label" default="Manage Templates" />
		      		</a>
				</div>
				 <div id="manageTemplates" class="accordion-body collapse ${flash.action == 'template' ? 'in' : ''}">
				 	<div class="accordion-inner">
						<g:render template="templateView"></g:render>
				 	</div>
				 </div>
			</div>
			<div class="accordion-group">
				<div class="accordion-heading">
					<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#parameterLogData">
        				<g:message code="executionZone.ParametersLogs.label" default="Parameters Change Logs" />
		      		</a>
				</div>
				<div id="parameterLogData" class="accordion-body collapse">
					<div class="accordion-inner">
						<g:render template="parameterLogDataView" model="[auditLogEvents:executionZoneInstance?.getAuditLogEvents()]"></g:render>
				 	</div>
				</div>
			</div>
		</div>
	</div>

	<asset:script>
    $('#scriptDirs input:radio').change(function(event) {
        $.ajax({
            url : '<g:createLink action="ajaxGetParameters" params="[execId:executionZoneInstance?.id]" />&scriptDir=' + encodeURI($(this).val()),
            beforeSend : function() {
                $('#scriptDirs input:radio').attr("disabled", "disabled");
                $('#parameters').slideUp('fast');
                $('#parametersSpinner').fadeIn('fast');
            },
            success: function(data) {
            	$('#parametersSpinner').hide();
            	$('#parameters').html(data).slideDown('slow');
            	zenboot.enableParameterList();
            	$('#scriptDirs input:radio').removeAttr('disabled');
            },
            error: function(jqHXR, status, error) {
		        	$('#parametersSpinner').hide();
		        	$("#parameters").html('<div class="alert alert-error">Some ERROR occured: ' + error + '</div>').slideDown('slow');
		        	$('#scriptDirs input:radio').removeAttr('disabled');
            }
        });
    });
    $(document).ready(function() {
        zenboot.enableParameterList();
    });
	</asset:script>
</body>
</html>
