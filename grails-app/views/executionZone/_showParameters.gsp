<table class="table table-striped parameters-table">
	<thead>
		<tr>
			<th style="width: 30%">Key</th>
			<th style="width: 30%">Value</th>
			<th style="width: 30%">Description</th>
			<th style="width: 5%">Expose</th>
			<th style="width: 5">Publish</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${parameters}" var="entry">
			<tr>
				<td>
					<g:textField name="parameters.key" value="${entry.name}" readonly="${readonly}" />
				</td>
				<td>
					<g:textField name="parameters.value" value="${entry.value}" readonly="${readonly}" />
				</td>
				<td>
					<g:textField name="parameters.description" value="${entry.description}" readonly="${readonly}" />
				</td>
				<td>
					<g:hiddenField name="parameters.exposed" value="${entry.exposed}" />
					<input type="checkBox" name="exposed" ${entry.exposed ? 'checked="checked"' : '' } />
				</td>
				<td>
					<g:hiddenField name="parameters.published" value="${entry.published}" />
					<input type="checkBox" name="published" ${entry.published ? 'checked="checked"' : '' } />
				</td>
				<td>
					<span title="Remove parameter" class="btn btn-mini remove-parameter-button">
						<i class="icon-minus-sign"></i>
					</span>
					<span title="Add parameter" class="btn btn-mini add-parameter-button">
						<i class="icon-plus-sign"></i>
					</span>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>

<span title="Add parameter" class="btn btn-mini add-parameter-button">
	<i class="icon-plus-sign"></i>
</span>

<div class="spacer">
	<span title="Export parameter" class="btn btn-mini copy-button">
		<i class="icon-share"></i>
		<g:textArea name="parameters-export" escapeHtml="false" class="hide">
			<g:each in="${parameters}" var="entry">
				${entry.name + "=" + entry.value + "\n"}
			</g:each>
		</g:textArea>
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
</div>

<r:require module="zclip" />

<g:javascript>
$(document).ready(function() {
    zenboot.enableProcessingParameterButtons(function() {
        //fire resize event to refresh copy-button position
        $(window).trigger("resize");
    });

    zenboot.enableCopyButton(
        "<g:resource dir="js/zclip" file="ZeroClipboard.swf" />",
        "<g:message code="executionZone.parameters.exportClipboard" default="Copied content to your clipboard" />",
        true
    );

    $('.import-button').click(function() {
        $('#parameters-import').modal('toggle')
    });

    $('.modal-close-button').click(function() {
        $('#parameters-import').modal('hide')
    });

    $('.modal-import-button').click(function() {
        var lines = $('#parameters-import').find('textarea').val().trim().split("\n");
        if (lines.length > 0) {
            zenboot.resetParameter();
	        for (i=0; i<lines.length ; i++) {
                var entry=lines[i].split("=");
                zenboot.addProcessingParameter(entry.shift().trim(), entry.join("=").trim());
	        }
	        $(window).trigger("resize");
        }
        $('#parameters-import').modal('hide')
    });
});
</g:javascript>