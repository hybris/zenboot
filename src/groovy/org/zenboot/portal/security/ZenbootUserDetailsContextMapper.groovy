package org.zenboot.portal.security

import org.apache.commons.lang.RandomStringUtils
import org.springframework.security.core.authority.SimpleGrantedAuthority

import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper

/**
 * create as a db from an LDAP user on login
 *
 * LDAP users get the USER role assigned as a default
 */
class ZenbootUserDetailsContextMapper implements UserDetailsContextMapper {
    UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection ldapAuthorities) {
        try {
            def lowerCaseUsername = username.toLowerCase()

            // TODO these should be configurable
            def email = ctx.getAttributeSortedStringSet("mail")[0] ?: ""
            def displayName = ctx.getAttributeSortedStringSet("displaynameprintable")[0] ?: username
            def password = RandomStringUtils.randomAlphanumeric(30)

            Person person
            def roles = []
            Person.withTransaction {
                person = Person.findByUsername(lowerCaseUsername)
                if (!person) {
                    person = new Person(
                        username: lowerCaseUsername,
                        enabled: true,
                        password: password, // if LDAP is switched off, users should not be able to log in
                        displayName: displayName,
                        email: email
                    )
                    person.save(flush: true)
                    PersonRole.create(person, Role.findByAuthority(Role.ROLE_USER), true)
                } else {
                    person.displayName = displayName
                    person.email = email
                    person.save(flush: true)
                }

                roles = person.getAuthorities()
            }

            def authorities = roles.collect { new SimpleGrantedAuthority(it.authority) }

            def userDetails = new ZenbootUserDetails(
                lowerCaseUsername, password, person.enabled,
                !person.accountExpired, !person.passwordExpired, !person.accountLocked,
                authorities, person.id,
                displayName, email
            )
            return userDetails
        } catch (e) {
            log.error("failed to map user ${username}", e)
            throw(e);
        }
    }

    void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new IllegalStateException("Only retrieving data from AD is currently supported")
    }
}
