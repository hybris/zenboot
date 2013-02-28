<g:if test="${element.plugin}">
	<div class="flow-element flow-element-plugin">
		<g:render template="flowElementDetails" model="[element:element.plugin, type: 'Plugin']" />
		<hr />
		<div>
			<i class="icon-star"></i>
			<span class="text-info">Plugin Pre-Processing</span>
		</div>
		<div class="flow-element flow-element-element">
			<g:render template="flowElementDetails" model="[element:element, type:'Scriptlet']" />
		</div>
		<div>
			<i class="icon-star"></i>
			<span class="text-info">Plugin Post-Processing</span>
		</div>
	</div>
</g:if>
<g:else>
	<div class="flow-element flow-element-element">
		<g:render template="flowElementDetails" model="[element:element, type:'Scriptlet']" />
	</div>
</g:else>