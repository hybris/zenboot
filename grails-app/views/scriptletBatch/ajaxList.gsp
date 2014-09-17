<%@ page import="org.zenboot.portal.processing.ScriptletBatch"%>
<%@ page import="org.zenboot.portal.processing.Processable.ProcessState"%>
<%@ page import="org.zenboot.portal.security.Role"%>
<%@ page import="org.joda.time.Duration"%>


<table class="table table-bordered">
	<thead>
		<tr>
			<th>
				<g:message code="scriptletBatch.queue.label" default="Process Queue" />
			</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${scriptletBatchInstanceList}" status="i" var="scriptletBatchInstance">
			<tr>
				<td>
					<div>
						<g:render template="state" model="[scriptletBatchInstance:scriptletBatchInstance]" />

						<small>&nbsp;&nbsp;<g:formatDate type="time" style="SHORT" date="${scriptletBatchInstance.creationDate}" class=".h6"/>
						(<joda:formatPeriod value="${new Duration(scriptletBatchInstance.getProcessTime())}" fields="hours,minutes, seconds" />)</small>
					</div>
					<sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
						<g:link action="show" id="${scriptletBatchInstance.id}">
							${fieldValue(bean: scriptletBatchInstance, field: "description")}
						</g:link>
					</sec:ifAllGranted>
					<sec:ifNotGranted roles="${Role.ROLE_ADMIN}">
						${fieldValue(bean: scriptletBatchInstance, field: "description")}
					</sec:ifNotGranted>
					<g:if test="${scriptletBatchInstance.state == ProcessState.RUNNING}">
						<div class="progress active">
							<div class="bar" style="width: ${scriptletBatchInstance.getProgress()}%;"></div>
						</div>
					</g:if>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
