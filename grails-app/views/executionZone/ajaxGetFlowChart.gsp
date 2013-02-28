<g:if test="${flow.batchPlugin}">
	<div class="flow-element flow-element-batchplugin">
		<g:render template="flowElementDetails" model="[element:flow.batchPlugin, type:'Batch Plugin']" />
		<hr />
		<div>
			<i class="icon-star"></i>
			<span class="text-success">Batch-Plugin Pre-Processing</span>
		</div>
</g:if>
<g:each var="element" in="${flow.flowElements}">
	<g:render template="flowElement" model="[element:element]" />
</g:each>
<g:if test="${flow.batchPlugin}">
		<div>
			<i class="icon-star"></i>
			<span class="text-success">Batch-Plugin Post-Processing</span>
		</div>
	</div>
</g:if>

<div class="legend">
	<a onclick="$(this).siblings('.flow-element').fadeToggle();" style="cursor: pointer;">
		<g:message code="executionZone.flow.showLegend" />
	</a>
	<span class="flow-element flow-element-batchplugin hide">
		<g:message code="executionZone.flow.legendBatchPlugin" />
	</span>
	<span class="flow-element flow-element-plugin hide">
		<g:message code="executionZone.flow.legendPlugin" />
	</span>
	<span class="flow-element flow-element-element hide">
		<g:message code="executionZone.flow.legendScriptlet" />
	</span>
</div>