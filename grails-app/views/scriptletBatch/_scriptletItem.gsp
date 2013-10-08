<%@ page import="org.zenboot.portal.processing.Processable.ProcessState"%>
<li class="property-value scriptlet-item-detail" aria-labelledby="processables-label">
	<strong>
		${q?.description}
	</strong>
	<table class="table table-bordered" style="margin-top: 10px;">
		<thead>
			<tr>
				<th>Result</th>
				<th>Execution Time</th>
				<th style="width: 15%">Return Code</th>
				<th style="width: 15%">Log</th>
				<th style="width: 15%">Process Output</th>
				<th style="width: 15%">Process Error</th>
				<th style="width: 15%">Exception</th>
			</tr>
		<thead>
		<tbody>
			<tr>
				<td>
					<g:render template="/scriptletBatch/state" model="[scriptletBatchInstance:q]" />
				</td>
				<td>
					<g:formatNumber number="${q?.getProcessTime()/1000}" groupingUsed="true" minFractionDigits="3" />
					sec
				</td>
				<td>
					<g:if test="${q?.exitCode >= 0}">
						<g:if test="${q?.exitCode == 0}">
							<span class="badge">0</span>
						</g:if>
						<g:elseif test="${q?.exitCode == 1}">
							<span class="badge badge-warning">1</span>
						</g:elseif>
						<g:elseif test="${q?.exitCode == 2}">
							<span class="badge badge-important">2</span>
						</g:elseif>
					</g:if>
				</td>
				<td>
					<g:if test="${q?.logged}">
						<a class="zb-tooltip" onclick="javascript:$('#${itemId}_logged').slideToggle()" title="${message(code:'scriptletBatch.button.showLog', default:'Show Log')}">
							<i class="icon-bullhorn"></i>
						</a>
					</g:if>
				</td>
				<td>
					<g:if test="${q?.output}">
						<a class="zb-tooltip" data-toggle="tooltip" data-placement="top" onclick="javascript:$('#${itemId}_output').slideToggle()" title="${message(code:'scriptletBatch.button.showOutput', default:'Show Output')}">
							<i class="icon-comment"></i>
						</a>
					</g:if>
				</td>
				<td>
					<g:if test="${q?.error}">
						<a class="zb-tooltip" onclick="javascript:$('#${itemId}_error').slideToggle()" title="${message(code:'scriptletBatch.button.showError', default:'Show Error')}">
							<i class="icon-exclamation-sign"></i>
						</a>
					</g:if>
				</td>
				<td>
					<g:if test="${q?.exceptionMessage}">
						<a class="zb-tooltip" onclick="javascript:$('#${itemId}_exception').slideToggle()" title="${message(code:'scriptletBatch.button.showException', default:'Show Exception')}">
							<i class="icon-fire"></i>
						</a>
					</g:if>
				</td>
			</tr>
		</tbody>
	</table>

	<g:if test="${q?.state != ProcessState.WAITING}">
		<g:if test="${q?.logged}">
			<pre id="${itemId}_logged" aria-labelledby="processables-log" class="hide">${q?.logged}</pre>
		</g:if>
		<g:if test="${q?.output}">
			<pre id="${itemId}_output" class="alert alert-info hide" aria-labelledby="processables-output">${q?.output}</pre>
		</g:if>
		<g:if test="${q?.error}">
			<pre id="${itemId}_error" class="alert alert-warning hide" aria-labelledby="processables-error">${q?.error}</pre>
		</g:if>
		<g:if test="${q?.exceptionMessage}">
			<pre id="${itemId}_exception" class="alert alert-error hide" aria-labelledby="processables-exception">${q?.exceptionMessage}</pre>
		</g:if>
	</g:if>
</li>