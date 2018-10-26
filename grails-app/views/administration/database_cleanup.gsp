<html>
<head>
<meta name="layout" content="main" />
</head>
<body>
  <h2>Description</h2>
  <p>This removes all execution zone actions which have no scriptlet batches (including processing parameters) to cleanup database.</p>

  <g:if test="${totalItems}">
      <p>Total items removed: ${totalItems}</p>
  </g:if>

  <g:form action="database_cleaned">
    <fieldset class="buttons spacer">
      <g:submitButton class="btn btn-primary" action="clear" name="${message(code: 'default.button.clear.label', default: 'Start cleanup')}" />
    </fieldset>
  </g:form>
</body>
</html>
