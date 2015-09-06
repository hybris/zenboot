<%@ page import="org.zenboot.portal.Host"%>
<div class="control-group fieldcontain">
	<label class="control-label" for="type">
		<g:message code="host.type.label" default="Type" />
	</label>
	<div class="controls">
		<g:textField name="type" value="${hostInstance?.class.getSimpleName()}" readonly="true" />
	</div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: hostInstance, field: 'ipAddress', 'error')} ">
	<label class="control-label" for="ipAddress">
		<g:message code="host.ipAddress.label" default="Ip Address" />
	</label>
	<div class="controls">
		<g:textField name="ipAddress" value="${hostInstance?.ipAddress}" readonly="true" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'cname', 'error')} ">
	<label class="control-label" for="cname">
		<g:message code="host.cname.label" default="Cname" />
	</label>
	<div class="controls">
		<g:textField name="cname" value="${hostInstance?.cname}" readonly="true" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'macAddress', 'error')} ">
	<label class="control-label" for="macAddress">
		<g:message code="host.macAddress.label" default="Mac Address" />
	</label>
	<div class="controls">
		<g:textField name="macAddress" value="${hostInstance?.macAddress}" readonly="true" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'datacenter', 'error')} ">
	<label class="control-label" for="datacenter">
		<g:message code="host.datacenter.label" default="Datacenter" />
	</label>
	<div class="controls">
		<g:textField name="datacenter" value="${hostInstance?.datacenter}" readonly="true" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'hostname', 'error')} required">
	<label class="control-label" for="hostname">
		<g:message code="host.hostname.label" default="Hostname" />
		<span class="required-indicator">*</span>
	</label>
	<div class="controls">
		<g:select id="hostname" name="hostname.id" from="${org.zenboot.portal.Hostname.list()}" optionKey="id" required="" value="${hostInstance?.hostname?.id}" class="many-to-one" readonly="true" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'instanceId', 'error')} ">
	<label class="control-label" for="instanceId">
		<g:message code="host.instanceId.label" default="Instance Id" />
	</label>
	<div class="controls">
		<g:textField name="instanceId" value="${hostInstance?.instanceId}" readonly="true" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'state', 'error')} required">
	<label class="control-label" for="state">
		<g:message code="host.state.label" default="State" />
		<span class="required-indicator">*</span>
	</label>
	<div class="controls">
		<g:select name="state" from="${org.zenboot.portal.HostState?.values()}" keys="${org.zenboot.portal.HostState.values()*.name()}" required="" value="${hostInstance?.state?.name()}" readonly="true" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'creationDate', 'error')} ">
	<label class="control-label" for="creationDate">
		<g:message code="host.creationDate.label" default="Creation Date" />
	</label>
	<div class="controls">
		<g:datePicker name="creationDate" precision="day" value="${hostInstance?.creationDate}" default="none" noSelection="['': '']" readonly="readonly" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'dnsEntries', 'error')} ">
	<label class="control-label" for="dnsEntries">
		<g:message code="host.dnsEntries.label" default="Dns Entries" />
	</label>
	<div class="controls">
		<ul class="one-to-many">
			<g:each in="${hostInstance?.dnsEntries?}" var="d">
				<li>
					<g:link controller="dnsEntry" action="show" id="${d.id}">
						${d?.encodeAsHTML()}
					</g:link>
				</li>
			</g:each>
		</ul>
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'expiryDate', 'error')} ">
	<label class="control-label" for="expiryDate">
		<g:message code="host.expiryDate.label" default="Expiry Date" />
	</label>
	<div class="controls">
		<g:datePicker name="expiryDate" precision="day" value="${hostInstance?.expiryDate}" default="none" noSelection="['': '']" />
	</div>
</div>

<div class="fieldcontain ${hasErrors(bean: hostInstance, field: 'owner', 'error')} ">
	<label class="control-label" for="owner">
		<g:message code="host.owner.label" default="Owner" />
	</label>
	<div class="controls">
		<g:select id="owner" name="owner.id" from="${org.zenboot.portal.Customer.list()}" optionKey="id" value="${hostInstance?.owner?.id}" class="many-to-one" noSelection="['null': '']" readonly="true" />
	</div>
</div>
