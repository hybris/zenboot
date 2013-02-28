<g:if test="${item.active}">
	<li class="active">
		<g:link action="${item.action}" params="${item.params}" controller="${item.controller}">
			${item.title}
		</g:link>
	</li>
</g:if>
<g:else>
	<li>
		<g:link action="${item.action}" params="${item.params}" controller="${item.controller}">
			${item.title}
		</g:link>
	</li>
</g:else>