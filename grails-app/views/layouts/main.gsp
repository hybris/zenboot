<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
        <g:layoutTitle default="Zenboot" />
    </title>
    <asset:link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
    <asset:javascript src="fp.js"/>
    <asset:stylesheet src="fp.css"/>
    <asset:stylesheet href="application.css"/>
</head>
<body>
	<div class="row-fluid" id="header">
		<div class="span2" id="logo">
			<g:link uri="/tspa">
				<img src="${resource(dir: 'images', file: 'zenboot-logo.png')}" alt="Zenboot Logo" />
			</g:link>
		</div>

		<div class="span6" id="title"></div>

		<div class="span4" style="text-align: right; padding: 5px 10px 0 0; color: white;" id="authentication">
			<sec:ifLoggedIn>
				<g:message code="login.welcome" default="Welcome" />
				<sec:username />/<sec:loggedInUserInfo field='displayName'/> (<g:link controller='logout' style="color:white;">Logout</g:link>)
			</sec:ifLoggedIn>
		</div>
	</div>

	<g:each var="notification" in="${notifications}">
		<div class="alert alert-${notification.type.name().toLowerCase()}" role="alert">${notification.message}</div>
	</g:each>

	<div class="row-fluid">
		<div class="span9 offset3">
			<sec:ifLoggedIn>
				<nav:primary scope="base" class="nav nav-tabs"/>
			</sec:ifLoggedIn>
		</div>
	</div>

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3">
				<nav:secondary scope="base" class="nav nav-pills nav-tabs nav-stacked"/>
				<div id="processqueue">
					<sec:ifLoggedIn>
						<g:include action="ajaxList" controller="scriptletBatch" />
					</sec:ifLoggedIn>
				</div>
			</div>
			<div class="span9">
				<g:layoutBody />
			</div>
		</div>

		<hr />

		<div class="row-fluid">
			<div class="span12" id="footer">
				<div class="pull-right">
					Version
					<g:meta name="app.version" />
					built with Grails
					<g:meta name="app.grails.version" />
				</div>
			</div>
		</div>
	</div>

	<asset:javascript src="application.js"/>

	<sec:ifLoggedIn>
        <asset:script type="text/javascript">
            $(document).ready(function() {
                zenboot.startProcessQueue('<g:createLink controller="scriptletBatch" action="ajaxList" />', 5000)
            });
       </asset:script>
	</sec:ifLoggedIn>

	<asset:deferredScripts/>
</body>
</html>
