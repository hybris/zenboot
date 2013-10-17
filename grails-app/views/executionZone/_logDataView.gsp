<table class="table table-striped logData-table">
	<thead>
		<tr>
			<th style="width:15%">Date</th>
			<th style="width:15%">User</th>
			<th style="width:10%">Type</th>
			<th style="width:10%">Old Value</th>
			<th style="width:10%">New Value</th>
			<!--th>?</th>
			<th>?</th-->
		</tr>
	</thead>
	<tbody>
	<g:each in="${auditLogEvents}" var="auditLogEvent" status="i">
		<tr style="${(i % 2) == 0 ? 'background-color: #ddd;' : ''}">
			<!-- if eventName = 'INSERT' -->
			<td>${fieldValue(bean:auditLogEvent, field:'dateCreated')}</td>
			<td>${fieldValue(bean:auditLogEvent, field:'actor')}</td>
			<td>${fieldValue(bean:auditLogEvent, field:'propertyName')}</td>
			<td>${fieldValue(bean:auditLogEvent, field:'oldValue')}</td>
			<td>${fieldValue(bean:auditLogEvent, field:'newValue')}</td>
			<!--td>${/*
					parameters.find{ 
						it.id == auditLogEvent.persistedObjectId.toBigInteger()
					}?.value
				*/}--
			</td>
			<td>--</td-->
		</tr>
	</g:each>		
	</tbody>
</table>