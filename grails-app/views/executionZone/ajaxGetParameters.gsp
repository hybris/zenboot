<table class="table exec-parameters-table">
	<thead>
		<tr>
			<th style="width: 45%">Key</th>
			<th style="width: 45%">Value</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<g:render template="parameterList" model="[executionZoneParameters:executionZoneParametersEmpty]" />
		<g:render template="parameterList" model="[executionZoneParameters:executionZoneParametersNonempty]" />
	</tbody>
</table>

<g:field type="hidden" value="${containsInvisibleParameters}" name="containsInvisibleParameters" />

<span title="Add parameter" class="btn btn-mini add-exec-parameter-button">
	<i class="icon-plus-sign"></i>
</span>
