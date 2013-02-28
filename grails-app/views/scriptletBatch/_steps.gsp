<g:each in="${steps}" var="q">
	<g:render template="/scriptletBatch/scriptletItem" model="[q:q, itemId:System.currentTimeMillis()]"></g:render>
</g:each>