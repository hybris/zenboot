<span class="muted">
	<strong>
		<g:message code="executionZone.scriptletMetadata.file" default="Required by:" />
	</strong>
	${scriptlet.script?.path.split(System.properties['file.separator'])[-2]}/${scriptlet.script?.name}
	<br />

	<strong>
		<g:message code="executionZone.scriptletMetadata.description" default="Description:" />
	</strong>
	${scriptlet.description}
	<g:if test="${scriptlet.value}">
		<br />

		<g:if test="${scriptlet.overlay}">
			<span class="text-info overlay">
				<strong>
					<g:message code="executionZone.scriptletMetadata.overlayValue" default="Overlay-Value:" />
				</strong>
				<g:if test="${scriptlet.visible}">
					${scriptlet.value}
				</g:if>
				<g:else>
					<g:message code="executionZone.scriptletMetadata.overlayValueSecret" default="Overlay value is secret. Set this field only if overlay value should be overridden." />
				</g:else>
				<i class="icon-share-alt">
					<a class="tooltip accept-parameter-button" rel="${scriptlet.visible ? scriptlet.value : ''}" title="${message(code:'default.button.accept.label')}">&nbsp;</a>
				</i>
			</span>
			<br />
		</g:if>

		<g:if test="${scriptlet.defaultValue}">
			<span class="text-success scriptlet">
				<strong>
					<g:message code="executionZone.scriptletMetadata.defaultValue" default="Default-Value:" />
				</strong>
				<g:if test="${scriptlet.visible}">
					${scriptlet.defaultValue}
				</g:if>
				<g:else>
					<g:message code="executionZone.scriptletMetadata.defaultValueSecret" default="Default value is secret. Set this field only if default value should be overridden." />
				</g:else>
				<g:if test="${scriptlet.visible || (!scriptlet.visible && !scriptlet.overlay)}">
					<i class="icon-share-alt">
						<a class="tooltip accept-parameter-button" rel="${scriptlet.visible ? scriptlet.defaultValue : ''}" title="${message(code:'default.button.accept.label')}">&nbsp;</a>
					</i>
				</g:if>
				<g:else>
                    <i class="icon-ban-circle">
                        <a class="tooltip" title="${message(code:'default.button.acceptDenied.label')}">&nbsp;</a>
                    </i>
				</g:else>
			</span>
		</g:if>
	</g:if>
</span>