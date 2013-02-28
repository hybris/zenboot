<html>
<head>
<meta name="layout" content="main" />
</head>
<body>
	<g:set value="${request.contextPath}/user" var="url" />
	<g:render template="iframe" model="[url:url]" />
</body>
</html>