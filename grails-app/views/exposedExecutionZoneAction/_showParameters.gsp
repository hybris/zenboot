<table class="table exec-parameters-table">
    <thead>
        <tr>
            <th style="width: 45%">Key</th>
            <th style="width: 45%">Value</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${parameters}" var="entry" status="i">
            <tr>
                <td>
                    <g:textField name="parameters.key" value="${entry.name}" readonly="true"/>
                </td>
                <td>
                    <div class="control-group">
                        <g:if test="${entry.visible || (!entry.visible && entry.value.empty)}">
                            <g:textField name="parameters.value" value="${entry.value}" readonly="${entry.overlay}"/>
                        </g:if>
                        <g:else>
                            <g:field type="password" name="parameters.value" readonly="${!entry.value.empty}" placeholder="${message(code:'executionZone.scriptletMetadata.secretValue', default:"Secret value")}" />
                            <span class="muted">
                                <g:message code="exposedExecutionZoneAction.scriptletMetadata.secretField" default="Field contains secret value" />
                            </span>
                        </g:else>
                    </div>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>

<g:field type="hidden" value="${containsInvisibleParameters}" name="containsInvisibleParameters" />