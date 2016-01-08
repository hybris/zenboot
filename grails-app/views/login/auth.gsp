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

	<g:form url='${postUrl}' method='POST' id='loginForm' class="well form-horizontal" autocomplete='off'>
    <div class="control-group">
      <!-- Username -->
      <label class="control-label"  for="username">
      	<g:message code="springSecurity.login.username.label" default="Username" />
      </label>
      <div class="controls">
        <g:textField name="j_username" id="username" />
      </div>
    </div>

    <div class="control-group">
      <!-- Password-->
      <label class="control-label" for="password">
      	<g:message code="springSecurity.login.password.label" default="Password" />
      </label>
      <div class="controls">
        <g:passwordField name="j_password" id="password" />
      </div>
    </div>
    
    <div class="control-group">
    	<label class="control-label" for="remember_me">
    		<g:message code="springSecurity.login.remember.me.label" default="Remember Me" />
    	</label>
    	<div class="controls">
    		<g:checkBox name="${rememberMeParameter}" id="remember_me" checked="${hasCookie}" />
    	</div>
    </div>		


		<g:submitButton name="submit" class="btn btn-primary" value="${message(code: "springSecurity.login.button")}" />
	</g:form>

	<asset:script>
		(function() {
			document.forms['loginForm'].elements['j_username'].focus();
		})();
	</asset:script>
</body>
</html>
