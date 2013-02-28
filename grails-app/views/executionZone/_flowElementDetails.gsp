<strong>
	${element.file.name}
</strong>
<g:if test="${type}">
	<br />
	<small>
		${type}
	</small>
</g:if>
<hr />
<table>
	<tr>
		<td>Author:</td>
		<td>
			${element.metadata?.author}
		</td>
	<tr>
		<td>Description:</td>
		<td>
			${element.metadata?.description}
		</td>
	</tr>
</table>