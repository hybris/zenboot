<table class="table parameters-table">
	<thead>
		<tr>
			<th style="width: 45%">Key</th>
			<th style="width: 45%">Value</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${executionZoneParameters}" var="entry" status="i">
			<tr>
				<td>
					<g:textField name="parameters.key" value="${entry.name}" />
				</td>
				<td>
					<div class="control-group ${entry.overlay ? 'info' : entry.value ? 'success' : ''}">
						<g:if test="${entry.visible || (!entry.visible && entry.value.empty)}">
							<g:textField name="parameters.value" value="${entry.value}" />
						</g:if>
						<g:else>
							<g:field type="password" name="parameters.value" placeholder="${message(code:'executionZone.scriptletMetadata.secretValue', default:"Secret value")}" />
							<span class="muted">
								<g:message code="executionZone.scriptletMetadata.secretField" default="Set this field only if secret value should be overridden" />
							</span>
						</g:else>
					</div>
				</td>
				<td>
					<span title="Remove parameter" class="btn btn-mini remove-parameter-button">
						<i class="icon-minus-sign"></i>
					</span>
					<span title="Add parameter" class="btn btn-mini add-parameter-button">
						<i class="icon-plus-sign"></i>
					</span>
					<span title="Show details" class="btn btn-mini details-parameter-button">
						<i class="icon-question-sign"></i>
					</span>
				</td>
			</tr>

			<tr>
				<td colspan="3" class="scriptlet-metadata">
					<g:render template="scriptletMetadata" model="[scriptlet:entry]" />
				</td>
			</tr>
		</g:each>
	</tbody>
</table>

<g:field type="hidden" value="${containsInvisibleParameters}" name="containsInvisibleParameters" />

<span title="Add parameter" class="btn btn-mini add-parameter-button">
	<i class="icon-plus-sign"></i>
</span>

<script type="text/javascript">
    $(document).ready(function() {
        zenboot.enableParameterList();
    })
</script>