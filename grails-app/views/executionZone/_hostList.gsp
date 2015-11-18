<g:set var="hosts" value="${executionZone.hosts.findAll { it.state == state }}" />
<div class="collapsable-list">
    <a class="collapsed" style="cursor: pointer">
        <g:message code="no.of.hosts"
                   default="{0} {1} hosts"
                   args="[hosts.size(), state.toString().toLowerCase()]"/>
        <i class="icon-resize-full"></i>
    </a>
    <ul class="unstyled hide">
        <g:each in="${hosts}" var="host" status="status">
            <li>
                <g:link controller="host" action="show" id="${host.id}">
                    ${host.cname ?: 'NO_CNAME'}
                </g:link>
                (${host.hostname} in ${host.datacenter ?: 'UNKNOWN'}, created <prettytime:display date="${host.creationDate}" />)
            </li>
        </g:each>
    </ul>
</div>
