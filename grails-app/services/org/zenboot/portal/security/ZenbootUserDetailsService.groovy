package org.zenboot.portal.security

import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import grails.plugin.springsecurity.SpringSecurityUtils


class ZenbootUserDetailsService implements GrailsUserDetailsService {
    /**
     * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least one role, so
     * we give a user with no granted roles this one which gets past that restriction but
     * doesn't grant anything.
     */
    static final List NO_ROLES = [new SimpleGrantedAuthority(SpringSecurityUtils.NO_ROLE)]

    UserDetails loadUserByUsername(String username, boolean loadRoles)
        throws UsernameNotFoundException {
        return loadUserByUsername(username)
    }

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person.withTransaction { status ->

            Person person = Person.findByUsername(username)
            if (!person) throw new UsernameNotFoundException('User not found', username)

            def authorities = person.authorities.collect {
                new SimpleGrantedAuthority(it.authority)
            }

            return new ZenbootUserDetails(
                person.username, person.password, person.enabled,
                !person.accountExpired, !person.passwordExpired, !person.accountLocked,
                authorities ?: NO_ROLES, person.id, person.displayName, person.email
            )
        } as UserDetails
    }
}

