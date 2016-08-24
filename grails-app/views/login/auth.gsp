<g:set var='securityConfig' value='${applicationContext.springSecurityService.securityConfig}'/>
<html>
	<head>
		<meta name="layout" content="main"/>
		<s2ui:title messageCode='spring.security.ui.login.title'/>
		<asset:stylesheet src='spring-security-ui-auth.css'/>
	</head>
	<body>
        <h2 class="page-header"><g:message code='spring.security.ui.login.signin'/></h2>
        <s2ui:form type='login' focus='username' class='well form-horizontal'>
            <div>
                <table>
                    <div class="control-group">
                        <label class="control-label" for="username">
                            <g:message code='spring.security.ui.login.username'/>
                        </label>
                        <div class="controls">
                            <input type="text" name="${securityConfig.apf.usernameParameter}" id="username" size="20"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label  class="control-label" for="password">
                            <g:message code='spring.security.ui.login.password'/>
                        </label>
                        <div class="controls">
                            <input type="password" name="${securityConfig.apf.passwordParameter}" id="password" size="20"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for='remember_me'>
                            <g:message code='spring.security.ui.login.rememberme'/>
                        </label>
                        <div class="controls">
                            <input type="checkbox" class="checkbox" name="${securityConfig.rememberMe.parameter}" id="remember_me" checked="checked"/>
                        </div>
                    </tr>
                </table>
                <g:submitButton name="submit" class="btn btn-primary" id='loginButton' value='${message(code: "springSecurity.login.button")}'/>
            </div>
        </s2ui:form>
	</body>
</html>
