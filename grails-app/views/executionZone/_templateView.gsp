
<div class="row-fluid">
	<div class="span3">
		<g:select name="executionZone_templates" from="${executionZoneInstance?.templates}" optionKey="id" optionValue="name" size="3" style="height: 450px"/>
		<fieldset class="buttons spacer">
			<span title="Import Template" class="btn import-templates-button">
				<g:message code="default.button.import.label" default="Import" />
			</span>
			
			<g:link controller="Template" action="export" params="[execId: executionZoneInstance?.id ]" class="btn">
				<g:message code="default.button.export.label" default="Export" />
			</g:link>
		</fieldset>
		</span>
	</div>
	
	
	<div class="span9">
		<g:form name="templateForm" controller="Template" action="save">
			<fieldset>
				<g:hiddenField name="executionZone.id" value="${executionZoneInstance?.id}" />
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
				<a id="cancelbtn" class="btn btn-success" onclick="CancelTemplate()" disabled="disabled">
					<g:message code="default.button.cancel.label" default="Cancel" />
				</a>
							
				<a id="delete_template" class="btn btn-danger delete_template" data-dismiss="modal" disabled="disabled">
					<g:message code="default.button.delete.label" default="Delete" />
				</a>	
				
				
				<g:actionSubmit class="btn btn-success" action="save" value="${message(code: 'executionZone.button.save.label', default: 'Save')}" />
			</fieldset>
		</g:form>
	</div>
</div>

	<div id="template-import" class="modal hide fade">
		<g:uploadForm action="upload" controller="Template">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3><g:message code="executionZone.import.templates.label" default="Import Templates" /></h3>
			</div>
			<div class="modal-body">
				<g:hiddenField name="execId" value="${executionZoneInstance?.id}"  />
				<g:message code="executionZone.import.templates.label" default="Import Templates" />: <br />
		    <input type="file" name="importFile" />
			</div>
			<div class="modal-footer">
				<a class="btn modal-close-button" data-dismiss="modal">
					<g:message code="default.button.close.label" default="Close" />
				</a>	
				<g:submitButton class="btn btn-success" name="${message(code: 'executionZone.button.import.label', default: 'Import')}" />
				
			</div>
		</g:uploadForm>
	</div>
	
		<div id="template-remove" class="modal hide fade">
		<g:form action="delete" controller="Template">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3><g:message code="default.button.delete.confirm.message" default="Are you sure?" /></h3>
			</div>
			<div class="modal-body">
				<a class="btn modal-close-button" data-dismiss="modal">
					<g:message code="default.button.close.label" default="Close" />
				</a>	
				<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}"/>
			</div>

		</g:form>
	</div>


<g:javascript>
$('#template_versions').change(function(event) {
	zenboot.loadTemplate($('#template_versions option:selected').val());
});

$('#executionZone_templates').change(function(event) {
	zenboot.loadTemplateFrom('<g:createLink controller="template" action="ajaxGetTemplateParameters" />/' + $('#executionZone_templates option:selected').val());
});

$('.import-templates-button').click(function() {
    $('#template-import').modal('toggle')
});

$('.delete_template').click(function() {
    $('#template-remove').modal('toggle')
});

function CancelTemplate(){
  $('#templateParametersSpinner').hide();      	
  $('#templateForm').attr("action", "${createLink(controller:"template", action: 'save')}");
	$("#templateForm input#name").val("");
	$('.delete_template').attr("disabled", "disabled");
	$("#template_versions").attr("disabled", "disabled");
	$("#templateForm textarea#template").html("");
	$("#templateForm :submit").attr("name", "save");
	$("#templateForm a#cancelbtn").attr("disabled", "disabled");
	$("#executionZone_templates option:selected").removeAttr("selected");
}
</g:javascript>