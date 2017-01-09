<%@ page import="org.zenboot.portal.ServiceUrl" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'serviceUrl.label', default: 'ServiceUrl')}"/>
    <title>
        <g:message code="default.list.label" args="[entityName]"/>
    </title>
</head>
<body>
<div id="list-serviceUrl" class="content scaffold-list" role="main">
    <h2 class="page-header">
        <g:message code="default.list.label" args="[entityName]"/>
    </h2>
    <g:if test="${flash.message}">
        <div class="alert alert-info" role="status">${flash.message}</div>
    </g:if>
    <table class="table table-striped">
        <thead>
        <tr>
            <g:sortableColumn property="owner" title="${message(code: 'serviceUrl.owner.label', default: 'Host')}"/>
            <g:sortableColumn property="url" title="${message(code: 'serviceUrl.url.label', default: 'Url')}"/>
            <g:sortableColumn property="owner.execZone" title="${message(code: 'host.execZone.label', default: 'ExecutionZone')}"/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${serviceUrlInstanceList}" status="i" var="serviceUrlInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td>
                    <g:link action="show" id="${serviceUrlInstance.id}">${fieldValue(bean: serviceUrlInstance, field:
                        "owner")}
                    </g:link>
                </td>

                <td>
                    <a href='${fieldValue(bean: serviceUrlInstance, field: "url")}'>
                        <g:fieldValue bean="${serviceUrlInstance}" field="url"/>
                    </a>
                </td>
                <td>
                    <g:link controller="executionZone" action="show" id="${serviceUrlInstance?.owner?.execZone?.id}">
                        ${serviceUrlInstance?.owner?.execZone?.description}
                    </g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <fieldset class="buttons spacer">
        <filterpane:filterButton class="btn" text="Filter" />
    </fieldset>

    <filterpane:filterPane domain="ServiceUrl" action="list" formMethod="get"
                           associatedProperties="owner.cname, owner.hostname, owner.state, owner.execZone.id" />
    <filterpane:isFiltered>
        <h4>Current Filters:</h4>
        <filterpane:currentCriteria domainBean="ServiceUrl" action="list" fullAssociationPathFieldNames="no"/>
    </filterpane:isFiltered>

    <div class="pagination">
        <filterpane:paginate total="${serviceUrlInstanceTotal}"/>
    </div>
</div>
</body>
</html>