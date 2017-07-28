<!doctype html>
<html>
<head>
    <title>Welcome</title>
    <meta name="layout" content="main">
</head>
<body>
<div class="hero-unit">
    <g:set value="${request.contextPath}/overview" var="url" />
    <h1><g:message code="home.overview" default="Overview" /></h1>
    <br/>
    <p>zenboot has booted ${allHostsCount} hosts so far whereas ${completedHostsCount} are still running (${stillRunningRate}%).</p>
    <p>zenboot has done ${allExecZoneActionCount} executions
    whereas ${successfulExecZoneActionCount} have been successful (${successRate}%)</p>
    <p>${allActiveExecutionZoneCount} ExecutionZones have been created whereas the biggest has
    ${maxHostsExecutionZoneHostCount} hosts</p>
</div>
</body>
</html>