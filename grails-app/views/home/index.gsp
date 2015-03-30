<!doctype html>
<html>
<head>
<title>Welcome</title>
<meta name="layout" content="main">
</head>
<body>
  <div class="hero-unit">
    <h1><g:message code="home.welcome" default="Welcome to Zenboot" /></h1>
    <br/>
    <p><g:message code="home.greeting" default="Your tool for system management." /></p>
    <p>zenboot has booted ${allHostsCount} hosts so far whereas ${completedHostsCount} are still running ()${stillRunningRate}%).</p>
    <p>zenboot has done ${allExecZoneActionCount} executions
      whereas ${successfulExecZoneActionCount} has been successfull (${successRate}%)</p>
    <p>${allActiveExecutionZoneCount} ExecutionZones has been created whereas the biggest has
      ${maxHostsExecutionZoneHostCount} hosts</p>
  </div>
</body>
</html>
