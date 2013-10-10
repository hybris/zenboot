<%@ page import="org.zenboot.portal.Template" %>



<div class="control-group fieldcontain ${hasErrors(bean: templateInstance, field: 'name', 'error')} ">
	<label class="control-label" for="name"><g:message code="template.name.label" default="Name" /></label>
    <div class="controls">
        <g:textField name="name" value="${templateInstance?.name}"/>
		
    </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: templateInstance, field: 'content', 'error')} ">
	<label class="control-label" for="content"><g:message code="template.template.label" default="Content" /></label>
    <div class="controls">
        <g:textArea name="content" value="${templateInstance?.content}"/>
    </div>
</div>

