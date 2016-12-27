<%@ page import="org.zenboot.portal.ServiceUrl" %>
<%@ page import="org.zenboot.portal.security.Role"%>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'serviceUrl.label', default: 'ServiceUrl')}"/>
    <title>
        <g:message code="default.show.label" args="[entityName]"/>
    </title>
</head>
<body>
<div id="show-serviceUrl" class="content scaffold-show" role="main">
    <h2 class="page-header">
        <g:message code="default.show.label" args="[entityName]"/>
    </h2>
    <g:if test="${flash.message}">
        <div class="alert alert-info" role="status">${flash.message}</div>
    </g:if>
    <g:link action="list">
        <i class="icon-list"></i>
        <g:message code="default.button.list.label" default="Back to overview"/>
    </g:link>
    <dl class="dl-horizontal serviceUrl">

        <g:if test="${serviceUrlInstance?.owner}">
            <dt id="owner-label">
                <g:message code="serviceUrl.owner.label" default="Host"/>
            </dt>

            <dd aria-labelledby="owner-label">
                <g:link controller="host" action="show" id="${serviceUrlInstance?.owner?.id}">
                    ${serviceUrlInstance?.owner?.encodeAsHTML()}
                </g:link>
            </dd>

        </g:if>

        <g:if test="${serviceUrlInstance?.url}">
            <dt id="url-label">
                <g:message code="serviceUrl.url.label" default="Url"/>
            </dt>

            <dd aria-labelledby="url-label">
                <a href='${fieldValue(bean: serviceUrlInstance, field: "url")}'>
                    <g:fieldValue bean="${serviceUrlInstance}" field="url"/>
                </a>
            </dd>
        </g:if>

    </dl>
    <sec:ifAllGranted roles="${Role.ROLE_ADMIN}">
        <g:form>
            <fieldset class="spacer buttons">
                <g:hiddenField name="id" value="${serviceUrlInstance?.id}"/>
                <g:actionSubmit class="btn btn-danger" action="delete"
                                value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
            </fieldset>
        </g:form>
    </sec:ifAllGranted>
</div>
</body>
</html>