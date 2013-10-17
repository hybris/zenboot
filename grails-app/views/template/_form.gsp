<%@ page import="org.zenboot.portal.Template" %>


<g:hiddenField name="executionZone.id" value="${templateInstance?.executionZone.id}" />
<div class="control-group fieldcontain ${hasErrors(bean: templateInstance, field: 'name', 'error')} ">
	<label class="control-label" for="name"><g:message code="template.name.label" default="Name" /></label>
    <div class="controls">
        <g:textField name="name" value="${templateInstance?.name}"/>
		
    </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: templateInstance, field: 'template', 'error')} ">
	<label class="control-label" for="template"><g:message code="template.template.label" default="Template" /></label>
    <div class="controls">
        <g:textArea name="template" value="${templateInstance?.template}"/>
    </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: templateInstance, field: 'message', 'error')} ">
	<label class="control-label" for="message"><g:message code="template.message.label" default="Commit Message" /></label>
    <div class="controls">
        <g:textArea name="message" value=""/>
    </div>
</div>
