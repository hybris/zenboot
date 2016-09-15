package org.zenboot.portal.processing

import grails.plugin.springsecurity.SpringSecurityUtils
import org.zenboot.portal.security.Role

@SuppressWarnings("GroovyUnusedDeclaration")
public class AccessService {
    def springSecurityService

    public boolean roleHasAccess(Role role, ExecutionZone executionZone) {
        def expression = role.executionZoneAccessExpression
        try {
            return Eval.me("executionZone", executionZone, expression == null ? "" : expression)
        } catch (Exception e) {
            this.log.error("executionZoneAccessExpression '$expression' from role '$role' threw an exception", e)
            return false
        }
    }

    public boolean rolesHaveAccess(Set<Role> roles, ExecutionZone zone) {
        roles.find() {
            roleHasAccess(it, zone)
        }
    }

    public boolean userHasAccess(ExecutionZone zone) {
        SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) ||
            rolesHaveAccess(springSecurityService.currentUser.getAuthorities(), zone)
    }
}
