<%@ page import="org.zenboot.portal.security.Role"%>

<table class="table table-striped parameters-table">
	<thead>
		<tr>
			<th style="width: 30%">Key</th>
			<th style="width: 30%">Value</th>
			<th style="width: 30%">Description</th>
			<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
				<th style="width: 5%">Expose</th>
				<th style="width: 5%">Publish</th>
		  </sec:ifAllGranted>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${parameters}" var="entry">
			<tr>
				<td>
					<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
						<g:textField name="parameters.key" value="${entry.name}" readonly="true" />
					</sec:ifNotGranted>
					<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
						<g:textField name="parameters.key" value="${entry.name}" />
					</sec:ifAllGranted>

				</td>
				<td>
					<g:textField name="parameters.value" value="${entry.value}" readonly="${readonly}" />
				</td>
				<td>
					<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
						<g:textField name="parameters.description" value="${entry.description}" readonly="true" />
					</sec:ifNotGranted>
					<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
						<g:textField name="parameters.description" value="${entry.description}" />
					</sec:ifAllGranted>
				</td>
				<td>
					<g:hiddenField name="parameters.exposed" value="${entry.exposed}" />
					<input type="checkBox" name="exposed" ${entry.exposed ? 'checked="checked"' : '' } <sec:ifNotGranted roles="${Role.ROLE_ADMIN}"> readonly="true" </sec:ifNotGranted> />
				</td>
				<td>
					<g:hiddenField name="parameters.published" value="${entry.published}" />
					<input type="checkBox" name="published" ${entry.published ? 'checked="checked"' : '' } <sec:ifNotGranted roles="${Role.ROLE_ADMIN}"> readonly="true" </sec:ifNotGranted> />
				</td>
				<td>
					<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
						<span title="Remove parameter" class="btn btn-mini remove-parameter-button">
							<i class="icon-minus-sign"></i>
						</span>
				</sec:ifAllGranted>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>

<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
	<div class="spacer">
	    <span title="Add parameter" class="btn btn-mini add-parameter-button">
	        <i class="icon-plus-sign"></i>
	    </span>

		<span title="Export parameter" class="btn btn-mini export-button">
			<i class="icon-share"></i>
		</span>

		<span title="Import parameter" class="btn btn-mini import-button">
			<i class="icon-edit"></i>
		</span>

		<div id="parameters-import" class="modal hide fade">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3>Import</h3>
			</div>
			<div class="modal-body">
				<p>
					<g:message code="executionZone.parameters.exportCopy" default="Please enter the parameters (key=value list)" />
				</p>
				<g:textArea name="parameters-import" style="width:98%; height:100%;" rows="7"></g:textArea>
			</div>
			<div class="modal-footer">
				<a class="btn modal-close-button">
					<g:message code="default.button.cancel.label" default="Cancel" />
				</a>
				<a class="btn btn-primary modal-import-button">
					<g:message code="default.button.import.label" default="Import" />
				</a>
			</div>
		</div>

		<div id="parameters-export" class="modal hide fade">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3>Export</h3>
			</div>
			<div class="modal-body">
				<p>
					<g:message code="executionZone.parameters.importCopy" default="Please copy the parameters." />
				</p>
				<g:textArea name="parameters-export" escapeHtml="false" style="width:98%; height:100%;" rows="7">
	{
	"parameters" : [
		<g:set var="last" value="${ parameters ? parameters.size() - 1 : 0 }" />
	  <g:each in="${parameters}" var="entry" status="i">
	  {
		   "name": "${ entry.name }",
		   "value": "${ entry.value }",
		   "description": "${ entry.description }"
	  } <g:if test="${ i != last }">,</g:if>
					</g:each>
	]}
				</g:textArea>
			</div>
			<div class="modal-footer">
				<a class="btn modal-close-button" data-dismiss="modal">
					<g:message code="default.button.close.label" default="Close" />
				</a>
			</div>
		</div>
	</div>
</sec:ifAllGranted>


<g:if test="${readonly=='false'}">
	<hr />
	<div class="row-fluid">
	    <g:textArea name="parameters.comments" value="" style="height: 150px; width: 100%; white-space: nowrap; overflow: auto;"  placeholder="${message(code: 'processingParameter.changeComment.label', default: 'Parameter Changes Comment')}" />
	</div>
</g:if>


<g:javascript>
$(document).ready(function() {
    zenboot.enableProcessingParameterButtons(function() {
        //fire resize event to refresh copy-button position
        $(window).trigger("resize");
    });

    $('.export-button').click(function() {
        $('#parameters-export').modal('toggle')
    });

    $('.import-button').click(function() {
        $('#parameters-import').modal('toggle')
    });

    $('.modal-close-button').click(function() {
        $('#parameters-import').modal('hide')
    });

    $('.modal-import-button').click(function() {
    		var parameters = $.parseJSON($('#parameters-import').find('textarea').val())["parameters"];
        if (parameters.length > 0) {
          zenboot.resetParameter();
	        for (i=0; i < parameters.length ; i++) {
                var entry=parameters[i];
                zenboot.addProcessingParameter(entry.name, entry.value, entry.description);
	        }
	        $(window).trigger("resize");
        }
        $('#parameters-import').modal('hide')
    });
});
</g:javascript>
