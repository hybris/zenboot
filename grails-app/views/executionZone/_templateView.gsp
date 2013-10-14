
<div class="row-fluid">
	<div class="span3">
		<g:select name="executionZone_templates" from="${executionZoneInstance?.templates}" optionKey="id" optionValue="name" size="3" style="height: 500px"/>
	</div>
	
	<div class="span9">
		<g:form name="templateForm" controller="Template" action="save">
			<fieldset>
				<g:hiddenField name="executionZone.id" value="${executionZoneInstance?.id}" optionKey="id" optionValue="dateCreated" class="pull-right" />
				<div class="row-fluid">
					<span class="3">	
						<g:textField name="name" value="${templateInstance?.name}" placeholder="${message(code: 'executionZone.name.label', default: 'Name')}"/>
					</span>
					<span class="3 pull-right">
						<g:select name="template_versions" from="${templateInstance?.templateVersions}" disabled="disabled" />
					</span>
				</div>
				<g:textArea name="template" value="${templateInstance?.template}" style="height: 400px; width: 100%; white-space: nowrap; overflow: auto;" placeholder="${message(code: 'executionZone.template.label', default: 'Template')}" />
			</fieldset>

			<fieldset class="buttons spacer pull-right">
				<a id="cancelbtn" class="btn btn-success" onclick="CancelTemplate()" style="display:none;">
					<g:message code="default.button.cancel.label" default="Cancel" />
				</a>
				<g:actionSubmit class="btn btn-success" action="save" value="${message(code: 'executionZone.button.save.label', default: 'Save')}" />
			</fieldset>
		</g:form>
	</div>
</div>

<g:javascript>
$('#template_versions').change(function(event) {
	zenboot.loadTemplate($('#template_versions option:selected').val());
});

$('#executionZone_templates').change(function(event) {
	zenboot.loadTemplateFrom('<g:createLink controller="template" action="ajaxGetTemplateParameters" />/' + $('#executionZone_templates option:selected').val());
});

function CancelTemplate(){
  	$('#templateParametersSpinner').hide();      	
  	$('#templateForm').attr("action", "${createLink(controller:"template", action: 'save')}");
	$("#templateForm input#name").val("");
	$("#template_versions").attr("disabled", "disabled");
	$("#templateForm textarea#template").html("");
	$("#templateForm :submit").attr("name", "save");
	$("#templateForm a#cancelbtn").hide();
	$("#executionZone_templates option:selected").removeAttr("selected");
}
</g:javascript>