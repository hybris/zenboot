<%@ page import="org.zenboot.portal.Customer"%>
<div class="control-group fieldcontain ${hasErrors(bean: customerInstance, field: 'email', 'error')} ">
	<label class="control-label" for="email">
		<g:message code="customer.email.label" default="Email" />
	</label>
	<div class="controls">
		<g:field type="email" name="email" value="${customerInstance?.email}" />

	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: customerInstance, field: 'creationDate', 'error')} ">
	<label class="control-label" for="creationDate">
		<g:message code="customer.creationDate.label" default="Creation Date" />
	</label>
	<div class="controls">
		<g:datePicker name="creationDate" precision="day" value="${customerInstance?.creationDate}" default="none" noSelection="['': '']" />

	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: customerInstance, field: 'hosts', 'error')} ">
	<label class="control-label" for="hosts">
		<g:message code="customer.hosts.label" default="Hosts" />
	</label>
	<div class="controls">

		<ul class="one-to-many unstyled">
			<g:each in="${customerInstance?.hosts?}" var="h">
				<li>
					<g:link controller="host" action="show" id="${h.id}">
						${h?.encodeAsHTML()}
					</g:link>
				</li>
			</g:each>
		</ul>

	</div>
</div>

