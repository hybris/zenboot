package org.zenboot.portal.security


import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.vote.RoleHierarchyVoter
import org.springframework.security.core.Authentication

class AdminRoleVoter extends RoleHierarchyVoter {

    AdminRoleVoter(RoleHierarchy roleHierarchy) {
        super(roleHierarchy)
    }

    @Override
    int vote(Authentication auth, Object obj, Collection<ConfigAttribute> config) {
        if (auth.getAuthorities()*.authority.contains(Role.ROLE_ADMIN)) {
            return ACCESS_GRANTED
        } else if(!auth.getAuthorities()*.authority.intersect(config).empty) {
            return ACCESS_GRANTED
        } else {
            return super.vote(auth, obj, config)
        }
    }
}
