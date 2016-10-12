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
            // toLowerCase(), because LDAP is not context-sensitive, but probably the DB is
            // so let's not end up with 2 Person depending on how the person logs in
            Person person = Person.findByUsername(username.toLowerCase())

            // Are you interested in that bloody LDAP-Object?!
            println("DISPLAYNAME: "+ctx.getAttributeSortedStringSet("DISPLAYNAMEPRINTABLE")[0])

            def roles = []
            Person.withTransaction {
                if (!person) {
                    person = new Person(
                        username: username.toLowerCase(),
                        // probably would be cool to do something like this:
                        // fullName: ctx.getAttributeSortedStringSet("DISPLAYNAMEPRINTABLE")[0],
                        enabled: true,
                        password: RandomStringUtils.randomAlphanumeric(30) // if LDAP is switched off, users should not be able to log in
                        displayname: ctx.getAttributeSortedStringSet("DISPLAYNAMEPRINTABLE")[0]
                    )
                    person.save(flush: true)
                    PersonRole.create(person, Role.findByAuthority(Role.ROLE_USER), true)
                } else {
                    // TODO update details (e.g. firstname, lastname, email) when we support these

                }

                roles = person.getAuthorities()
            }

            def authorities = roles.collect { new SimpleGrantedAuthority(it.authority) }

            def userDetails = new ZenbootUserDetails(username.toLowerCase()'', person.enabled, true, true, true, authorities, person.id)
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
