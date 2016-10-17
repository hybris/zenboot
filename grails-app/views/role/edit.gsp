<html>
	<head>
		<meta name="layout" content="${layoutUi}"/>
		<s2ui:title messageCode='default.edit.label' entityNameMessageCode='role.label' entityNameDefault='Role'/>
	</head>
	<body>
		<h3><g:message code='default.edit.label' args='[entityName]'/></h3>
		<s2ui:form type='update' beanName='role'>
			<s2ui:tabs elementId='tabs' height='400' data='${tabData}'>
				<s2ui:tab name='roleinfo' height='400'>
					<table>
					<tbody>
						<s2ui:textFieldRow name='authority' labelCodeDefault='Authority'/>
                        <s2ui:textFieldRow size='75' style="height:100px;" name='executionZoneAccessExpression'
                                           labelCode='executionZoneAccessExpression.authority.label' bean="${role}"
                                           labelCodeDefault='zoneAccessExpression' value="${role?.executionZoneAccessExpression}"/>
                        <tr class="prop">
                            <td/>
                            <td valign="top" class="name">
                                <sup>executionZone --> instance of org.zenboot.portal.processing.ExecutionZone</sup><br/>
                                <sup>example: executionZone.param('DOMAIN') ==~ /.*test.mycompany.com.*/</sup><br/><br/>
                            </td>
                        </tr>
                        <s2ui:textFieldRow size='75' style="height:100px;" name='parameterEditExpression'
                                           labelCode='parameterEditExpression.authority.label' bean="${role}"
										   labelCodeDefault='parameterEditExpression' value="${role?.parameterEditExpression}"/>
                        <tr class="prop">
                            <td/>
                            <td valign="top" class="name">
                                <sup>parameter --> instance of org.zenboot.portal.processing.ProcessingParameter</sup><br/>
                                <sup>parameterKey --> org.zenboot.portal.processing.ProcessingParameter.name (String)</sup><br/>
                                <sup>example: parameter.description == /.*usereditable.*/</sup><br/><br/>
                            </td>
                        </tr>
					</tbody>
					</table>
				</s2ui:tab>
				<s2ui:tab name='users' height='400'>
					<g:if test='${users.empty}'>
					<g:message code='spring.security.ui.role_no_users'/>
					</g:if>
					<table>
						<thead>
							<tr>
								<td><g:message code="username" default="Username"/></td>
								<td><g:message code='displayName' default='Display Name'/></td>
								<td><g:message code='email' default='Email'/></td>
							</tr>
						</thead>
						<tbody>
							<g:each var='u' in='${users}'>
								<tr>
									<g:each var='p' in='${["username", "displayName", "email"]}'>
										<td>
											<g:link controller='user' action='edit' id='${u.id}'>
												${uiPropertiesStrategy.getProperty(u, p)}
											</g:link>
											<br/>
										</td>
									</g:each>
								</tr>
							</g:each>
						</tbody>
					</table>
				</s2ui:tab>
			</s2ui:tabs>
			<div style='float:left; margin-top: 10px;'>
				<s2ui:submitButton/>
				<g:if test='${role}'>
				<s2ui:deleteButton/>
				</g:if>
			</div>
		</s2ui:form>
		<g:if test='${role}'>
		<s2ui:deleteButtonForm instanceId='${role.id}'/>
		</g:if>
	</body>
</html>
