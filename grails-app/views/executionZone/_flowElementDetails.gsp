<strong>
	${element.file.name}
</strong>
<a class="zb-tooltip" onclick="javascript:$('#${element.file.name.replace(".","_")}_text').slideToggle()" title="${message(code:'scriptletBatch.button.showCode', default:'Show Code')}">
	<i class="icon-eye-close"></i>
</a>
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

<pre id="${element.file.name.replace(".","_")}_text" style="display: none;">${element.file.text}</pre>
