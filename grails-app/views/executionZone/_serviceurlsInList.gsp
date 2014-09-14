<g:if test="${!serviceUrls.isEmpty()}">
	<div class="collapsable-list">
		<a class="collapsed" style="cursor: pointer">
			<g:message code="executionZone.serviceurls.size" default="{0} serviceUrls" args="[serviceUrls.size()]" />
			<i class="icon-resize-full"></i>
		</a>
		<ul class="unstyled hide">
			<g:each in="${serviceUrls}" var="serviceUrl">
				<li>
					<a href="${serviceUrl.url}">${serviceUrl.url}</a>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>
