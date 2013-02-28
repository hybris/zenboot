<g:if test="${parameters.isEmpty()}">
	<g:message code="executionZone.parameters.empty" default="No parameters defined" />
</g:if>
<g:else>
	<div class="collapsable-list">
		<a class="collapsed" style="cursor: pointer">
			<g:message code="executionZone.parameters.size" default="{0} parameters defined" args="[parameters.size()]" />
			<i class="icon-resize-full"></i>
		</a>
		<ul class="unstyled hide">
			<g:each in="${parameters}" var="entry">
				<li>
					<pre>${entry.name.encodeAsHTML()} = ${entry.value.encodeAsHTML()}</pre>
				</li>
			</g:each>
		</ul>
	</div>
</g:else>