
<div class="row-fluid">
	<div class="span3">
		<g:select name="executionZone_templates" from="${executionZoneInstance?.templates}" optionKey="id" optionValue="name" size="3" style="height: 500px"/>
	</div>
	
	<div class="span9">
		<g:form name="templateForm" controller="Template" action="save">
			<fieldset>
				<g:hiddenField name="executionZone.id" value="${executionZoneInstance?.id}" />
				<g:textField name="name" value="${templateInstance?.name}" />
				<g:textArea name="template" value="${templateInstance?.template}" style="height: 400px; width: 100%; white-space: nowrap; overflow: auto;"/>
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
$('#executionZone_templates').change(function(event) {
	zenboot.loadTemplateFrom('<g:createLink controller="template" action="ajaxGetTemplateParameters" />/' + $('#executionZone_templates option:selected').val());
});

function CancelTemplate(){
  	$('#templateParametersSpinner').hide();      	
  	$('#templateForm').attr("action", "${createLink(controller:"template", action: 'save')}");
	$("#templateForm input#name").val("");
	$("#templateForm textarea#template").html("");
	$("#templateForm :submit").attr("name", "save");
	$("#templateForm a#cancelbtn").hide();
	$("#executionZone_templates option:selected").removeAttr("selected");
}
</g:javascript>