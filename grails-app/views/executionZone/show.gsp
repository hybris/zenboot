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
		
		<div class="accordion" id="execution-show-accordion">
			<div class="accordion-group">
				<div class="accordion-heading">
      		<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#showZone">
        		<g:message code="default.show.label" args="[entityName]" />
      		</a>
    		</div>
    		<div id="showZone" class="accordion-body collapse in">
    			<div class="accordion-inner">
						<g:form method="post">
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
															${a.scriptDir.name} (<g:formatDate type="datetime" style="MEDIUM" timeStyle="SHORT" date="${a.creationDate}"/>)
														</g:link>
													</sec:ifAllGranted>
													<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
														${a.scriptDir.name} (<g:formatDate type="datetime" style="MEDIUM" timeStyle="SHORT" date="${a.creationDate}"/>)
													</sec:ifNotGranted>
												</li>
											</g:each>
										</ul>
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
						
							</dl>
				
							<g:hiddenField name="execId" value="${executionZoneInstance?.id}" />
				
							<fieldset class="spacer buttons">
								<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" disabled="${!executionZoneInstance?.enabled}" />
							</fieldset>
						</g:form>
					</div>
				</div>
			</div>
			<div class="accordion-group">
				<div class="accordion-heading">
					<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#execScript">
        		<g:message code="executionZone.execAction.label" default="Execute Script" />
      		</a>
				</div>
				 <div id="execScript" class="accordion-body collapse">
				 	<div id="scriptDirs" class="accordion-inner">
				 		<g:form method="post">
				 			
				 			<g:if test="${cmd}">
								<g:set value="${cmd.scriptDir}" var="selectedScriptDir" />
							</g:if>
							<g:elseif test="${!scriptDirs.empty}">
								<g:set value="${scriptDirs.iterator().next()}" var="selectedScriptDir" />
							</g:elseif>
				 			
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
			<div class="accordion-group">
				<div class="accordion-heading">
					<a class="accordion-toggle" data-toggle="collapse" data-parent="#execution-show-accordion" href="#editExecZone">
        		<g:message code="executionZone.editExecZone.label" default="Edit Execution Zone and Parameters" />
      		</a>
				</div>
				 <div id="editExecZone" class="accordion-body collapse">
				 	<div class="accordion-inner">
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
		</div>
	</div>

	<g:javascript>
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
	</g:javascript>
</body>
</html>