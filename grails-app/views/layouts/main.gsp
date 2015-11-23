<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>
	<g:layoutTitle default="Zenboot" />
</title>
<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
<r:require modules="application,bootstrap" />
<r:layoutResources />
</head>
<body>
	<div class="row-fluid" id="header">
		<div class="span2" id="logo">
			<g:link controller="home" action="index">
				<img src="${resource(dir: 'images', file: 'zenboot-logo.png')}" alt="Zenboot Logo" />
			</g:link>
		</div>
		<div class="span6" id="title"></div>
		<div class="span4" style="text-align: right; padding: 5px 10px 0 0; color: white;" id="authentication">
			<sec:ifLoggedIn>
				<g:message code="login.welcome" default="Welcome" />
				<sec:username /> (<g:link controller='logout' style="color:white;">Logout</g:link>)
			</sec:ifLoggedIn>
		</div>
	</div>

	<div class="row-fluid">
		<div class="span9 offset3">
			<sec:ifLoggedIn>
				<ul class="nav nav-tabs">
					<apNav:renderMenu group="tabs" var="item" mainMenu="true">
						<g:render template="/layouts/menuItem" model="[item:item]" />
					</apNav:renderMenu>
				</ul>
			</sec:ifLoggedIn>
		</div>
	</div>

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3">
				<ul class="nav nav-pills nav-tabs nav-stacked">
					<apNav:renderMenu group="menu" var="item">
						<g:render template="/layouts/menuItem" model="[item:item]" />
					</apNav:renderMenu>
				</ul>
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

	<r:layoutResources />

	<sec:ifLoggedIn>
	<script type="text/javascript">
		$(document).ready(function() {
			zenboot.startProcessQueue('<g:createLink controller="scriptletBatch" action="ajaxList" />', 5000)
		});
   </script>
	</sec:ifLoggedIn>
</body>
</html>