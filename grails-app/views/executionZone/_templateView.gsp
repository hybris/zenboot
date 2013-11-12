<div id="template_messages">

</div>
<div class="row-fluid">
	<div class="span3">
		<g:select name="executionZone_templates" from="${executionZoneInstance?.templates}" optionKey="id" optionValue="name" size="3" style="height: 620px"/>
		<fieldset class="buttons spacer">
			<span title="Import Template" class="btn import-templates-button">
				<g:message code="default.button.import.label" default="Import" />
			</span>
			
			<g:link mapping="template" controller="Template" action="export" params="[execId: executionZoneInstance?.id ]" class="btn">
				<g:message code="default.button.export.label" default="Export" />
			</g:link>
		  <span title="Check Parameters" class="btn check-templates-button">
				<g:message code="default.button.check.label" default="Check Templates" />
			</span>
		</fieldset>
		</span>
	</div>
	
	
	<div class="span9">
		<g:form name="templateForm" controller="Template" action="save">
			<fieldset>
				<g:hiddenField name="executionZone.id" value="${executionZoneInstance?.id}" />
				<div class="row-fluid">
					<span class="3">	
						<g:textField name="name" value="${templateInstance?.name}" placeholder="${message(code: 'executionZone.name.label', default: 'Name')}" class="input-xlarge"/>
					</span>
					<span class="3 pull-right">
						<g:select name="template_versions" from="${templateInstance?.templateVersions}" disabled="disabled" class="input-xlarge" />
					</span>
				</div>
				<g:textArea name="template" value="${templateInstance?.template}" style="height: 400px; width: 100%; overflow: auto;" placeholder="${message(code: 'executionZone.template.label', default: 'Template')}" />
				<g:textArea name="message" value="${templateInstance?.message}" style="height: 150px; width: 100%; white-space: nowrap; overflow: auto;" placeholder="${message(code: 'executionZone.comment.label', default: 'Commit message')}" data-placeholder="${message(code: 'executionZone.comment.label', default: 'Commit message')}" />
			</fieldset>

			<fieldset class="buttons spacer pull-right">
				<a id="showFileButton" class="btn" disabled="disabled">
					<g:message code="default.button.showFile.label" default="Preview" />
				</a>
				<a id="cancelbtn" class="btn btn-success" onclick="zenboot.templateCancel('${createLink(mapping:'template', controller:'template', action: 'save')}')" disabled="disabled">
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
		<g:uploadForm action="upload" controller="Template" class="form-horizontal">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3><g:message code="executionZone.import.templates.label" default="Import Templates" /></h3>
			</div>
			<div class="modal-body">
				<g:hiddenField name="execId" value="${executionZoneInstance?.id}"  />
        <div class="control-group fieldcontain">
          <label class="control-label" for="importFile">
            <g:message code="executionZone.import.templates.label" default="Import templates" />
          </label>
          <div class="controls">
            <input type="file" name="importFile" />
          </div>
        </div>
        <div class="control-group fieldcontain">
          <label class="control-label" for="importMessage">
            <g:message code="executionZone.importMessage.templates.label" default="Commit message" />
          </label>
          <div class="controls">
            <g:textArea name="commitMessage" value="${message(code: 'executionZone.importComment.label', default: 'Import')}" />
          </div>
        </div>
        <div class="control-group fieldcontain">
          <label class="control-label" for="updateTemplates">
            <g:message code="executionZone.importUpdate.templates.label" default="Update existing templates" />
          </label>
          <div class="controls">
            <g:checkBox name="updateTemplates" value="${false}" />
          </div>
        </div>
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
		<g:form name="templateRemoveForm" action="delete" controller="Template">
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
	
	<div id="show-file" class="modal hide fade">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3 id="show-file-name"></h3>
			</div>
			<div class="modal-body">
  			<g:textArea name="show-file-field" value="" style="height: 300px; width: 96%; overflow: auto;" />
				<a class="btn modal-close-button" data-dismiss="modal">
					<g:message code="default.button.close.label" default="Close" />
				</a>	
			</div>

	</div>
	
	<div id="check-templates" class="modal hide fade">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3><g:message code="default.executionZone.checkParameters" default="Missing Parameters" /></h3>
			</div>
			<div class="modal-body">
  			<g:textArea name="check-templates-field" value="" style="height: 300px; width: 96%; overflow: auto;" />
				<a class="btn modal-close-button" data-dismiss="modal">
					<g:message code="default.button.close.label" default="Close" />
				</a>	
			</div>

	</div>


<g:javascript>
$('#template_versions').change(function(event) {
	zenboot.loadTemplate($('#template_versions option:selected').val());
});

$('#executionZone_templates').change(function(event) {
	zenboot.loadTemplateFrom('<g:createLink mapping="template" controller="template" action="show" />/' + $('#executionZone_templates option:selected').val());
});

$('.import-templates-button').click(function() {
    $('#template-import').modal('toggle')
});

$('.check-templates-button').click(function() {
  zenboot.templateCheck('<g:createLink mapping="template" controller="template" action="checkParameters" params="[execId: executionZoneInstance?.id]"/>');
});

$('.delete_template').click(function() {
    $('#template-remove').modal('toggle')
});

$('#templateForm').submit(function(event){
  zenboot.templateSave('<g:createLink mapping="template" controller="template" action="index" params="[execId: executionZoneInstance?.id]"/>');
  event.preventDefault();
});

$('#templateRemoveForm').submit(function(event){
  zenboot.templateRemove('<g:createLink mapping="template" controller="template" action="index" params="[execId: executionZoneInstance?.id]" />' );
  zenboot.templateCancel('<g:createLink mapping="template" controller="template" action="save" />');
  $('#template-remove').modal('toggle')
  event.preventDefault();
});

</g:javascript>
