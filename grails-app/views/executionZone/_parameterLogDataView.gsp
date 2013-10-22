<div style="width: 100%; height: 250px; overflow: auto;">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Date</th>
                <th>User</th>
                <th>Name</th>
                <th>Value</th>
                <th>Description</th>
                <th>Comment</th>
            </tr>
        </thead>
        <tbody>
        <g:each in="${auditLogEvents}" var="auditLogEvent" status="i">
            <tr>
                <td><g:formatDate date="${auditLogEvent.dateCreated}" /></td>
                <td>${fieldValue(bean:auditLogEvent, field:'user')}</td>
                <td>${fieldValue(bean:auditLogEvent, field:'name')}</td>
                <td>${fieldValue(bean:auditLogEvent, field:'value')}</td>
                <td>${fieldValue(bean:auditLogEvent, field:'description')}</td>
                <td>${fieldValue(bean:auditLogEvent, field:'comment')}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>