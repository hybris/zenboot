<g:each in="${steps}" var="q">
	%{-- <!-- we had issues where the wrong step opened, so addid a random-number to the currentTimeMillis --> --}%
	<g:render template="/scriptletBatch/scriptletItem" model="[q:q, itemId:System.currentTimeMillis()+'_'+new Random().nextInt(1000)]"></g:render>
</g:each>
