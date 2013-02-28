<%@ page import="org.zenboot.portal.processing.Processable.ProcessState"%>
<g:if test="${scriptletBatchInstance.state == ProcessState.WAITING}">
	<span class="label">
		${fieldValue(bean: scriptletBatchInstance, field: "state")}
	</span>
</g:if>
<g:elseif test="${scriptletBatchInstance.state == ProcessState.RUNNING}">
	<span class="label label-info">
		${fieldValue(bean: scriptletBatchInstance, field: "state")}
	</span>
</g:elseif>
<g:elseif test="${scriptletBatchInstance.state == ProcessState.SUCCESS}">
	<span class="label label-success">
		${fieldValue(bean: scriptletBatchInstance, field: "state")}
	</span>
</g:elseif>
<g:elseif test="${scriptletBatchInstance.state == ProcessState.FAILURE}">
	<span class="label label-important">
		${fieldValue(bean: scriptletBatchInstance, field: "state")}
	</span>
</g:elseif>
<g:elseif test="${scriptletBatchInstance.state == ProcessState.CANCELED}">
	<span class="label label-inverse">
		${fieldValue(bean: scriptletBatchInstance, field: "state")}
	</span>
</g:elseif>