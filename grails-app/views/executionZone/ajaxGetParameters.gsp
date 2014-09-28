<table class="table exec-parameters-table">
	<thead>
		<tr>
			<th style="width: 45%">Key</th>
			<th style="width: 45%">Value</th>
			<th></th>
		</tr>
	</thead>
</table>

<g:render template="parameterList" model="[executionZoneParameters:executionZoneParametersEmpty]" />

<!-- Show Non-empty parameters only if needed -->
<a class="collapsed" style="cursor: pointer" data-toggle="collapse" data-target="#nonempty">
	<g:message code="executionZoneParametersNonempty.size()" default="{0} non-empty parameters" args="[executionZoneParametersNonempty.size()]" />
	<i class="icon-resize-full" onclick="$(this).toggleClass('icon-resize-full').toggleClass('icon-resize-small');"></i>
</a>

<div id="nonempty" class="collapse">
	<g:render template="parameterList" model="[executionZoneParameters:executionZoneParametersNonempty]" />

	<g:field type="hidden" value="${containsInvisibleParameters}" name="containsInvisibleParameters" />

	<span title="Add parameter" class="btn btn-mini add-exec-parameter-button">
		<i class="icon-plus-sign"></i>
	</span>
</div>
