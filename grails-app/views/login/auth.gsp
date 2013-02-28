<html>
<head>
<meta name='layout' content='main' />
<title>
	<g:message code="springSecurity.login.title" />
</title>
</head>
<body>
	<h2 class="page-header">
		<g:message code="springSecurity.login.header" />
	</h2>

	<g:if test='${flash.message}'>
		<div class='alert alert-error'>
			${flash.message}
		</div>
	</g:if>

	<g:form url='${postUrl}' method='POST' id='loginForm' class="well inline-form" autocomplete='off'>
		<ol class="property-list executionZone">
			<li class="fieldcontain">
				<span class="property-label">
					<g:message code="springSecurity.login.username.label" default="Username" />
				</span>
				<span class="property-value" aria-labelledby="username-label">
					<g:textField name="j_username" id="username" />
				</span>
			</li>

			<li class="fieldcontain">
				<span class="property-label">
					<g:message code="springSecurity.login.password.label" default="Password" />
				</span>
				<span class="property-value" aria-labelledby="password-label">
					<g:passwordField name="j_password" id="password" />
				</span>
			</li>

			<li class="fieldcontain">
				<span class="property-label">
					<g:message code="springSecurity.login.remember.me.label" default="Remember Me" />
				</span>
				<span class="property-value" aria-labelledby="rememberme-label">
					<g:checkBox name="${rememberMeParameter}" id="remember_me" checked="${hasCookie}" />
				</span>
			</li>
		</ol>
		<g:submitButton name="submit" class="btn btn-primary" value="${message(code: "springSecurity.login.button")}" />
	</g:form>

	<g:javascript>
		(function() {
			document.forms['loginForm'].elements['j_username'].focus();
		})();
	</g:javascript>
</body>
</html>
