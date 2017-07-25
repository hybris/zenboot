package org.zenboot.portal.processing

import grails.async.Promise
import grails.plugin.springsecurity.SpringSecurityUtils
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role
import org.zenboot.portal.security.PersonRole

import java.util.concurrent.ConcurrentHashMap

@SuppressWarnings("GroovyUnusedDeclaration")
class AccessService {
    def springSecurityService

    /* The accessCache is a dynamic datastructure looking like this:
       {1={1=false, 2=true, 3=false, 4=false}, 2={1=false, 2=false, 3=false, 4=false}}
       The fist level is a ExecutionZone.id, the second Level is a Person.id and
       the boolean is the access from that person to that zone

       Memory Footprint:
       ConcurrentHashMap about 128 byte
       Long 20 byte
       Boolean 16 byte
       64 bit system means X 1.8
       ConcurrentHasmaps<1000 Long, ConcurrentHashmap<1000 Long,Boolean>>

       ==> ((1000 * (128 + 20)) + (1000 * (20 + 16))) * 1.8
       ==> about 320kb for 1000 Users on 1000 Zones
    */
    def accessCache

    private boolean roleHasAccess(Role role, ExecutionZone executionZone) {
        def expression = role.executionZoneAccessExpression
        try {
            return Eval.me("executionZone", executionZone, expression == null ? "" : expression)

        } catch (Exception e) {
            log.error("executionZoneAccessExpression '$expression' from role '$role' threw an exception: " + e.message)
            return false
        }
    }

    private boolean rolesHaveAccess(Set<Role> roles, ExecutionZone zone) {
        roles.any() {
            roleHasAccess(it, zone)
        }
    }

    boolean userHasAccess(ExecutionZone zone) {
        SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) ||
                testIfUserHasAccess(springSecurityService.currentUser, zone)
    }

    // Might not be 100% Threadsafe but hopefully something above 99% ;-)
    private synchronized boolean testIfUserHasAccess(Person user, ExecutionZone zone) {

        if (user.getAuthorities().authority.contains(Role.ROLE_ADMIN)) {
            //no cache required for admin user
            return true
        }

        if (!accessCache) {
            log.info("initializing accessCache")
            accessCache = new ConcurrentHashMap<Long, HashMap>()
        }

        if (!accessCache[user.id]) {
            log.info("user ${user} not found in cache, creating")
            accessCache[user.id] = new ConcurrentHashMap<Long, Boolean>()
        }

        if (accessCache[user.id][zone.id] == null) {
            def hasAccess = rolesHaveAccess(user.getAuthorities(), zone)
            // concurrency is quite unlikely here until users use multiple browsers
            // impact would be a clash with the invalidate-methods
            // 1. invalidation-method removed a user
            //    --> NullPointer

            if (hasAccess != null) {
                accessCache[user.id][zone.id] = hasAccess
            }
            else {
                log.error("hasAccess is null, returning false")
                return false
            }
        }

        def hasAccess = accessCache[user.id][zone.id] ?: false

        return hasAccess
    }

    /* The cache invalidation methods are not implemented symetrically for
       practical reasons: Person and PersonRole use afterHooks but
       the zone-code-hooks not.
       On the other hand removing a zone is on the second level but
       removing a person removes potentially hundred of zones.
       Therefore:
        * refresh for for Person/Role
        * invalidate only for a zone
    */
    def invalidateAccessCacheByZone(ExecutionZone zone) {
        if (zone && accessCache) {
            log.info("invalidating ${zone} in accessCache")
            accessCache.each() { key, user ->
                user.remove(zone.id)
            }
        }
    }

    def refreshAccessCacheByUser(Person user) {

        if (user) {

            if (user.getAuthorities().authority.contains(Role.ROLE_ADMIN)) {
                //no cache required for admin user
                return
            }

            //remove existing user from cache
            accessCache?.remove(user.id)

            log.info("Refreshing ${user} in accessCache")
            log.info("user has roles " + user.getAuthorities())

            ExecutionZone.findAll().each { zone -> testIfUserHasAccess(user, zone) }

            log.info(accessCache[user.id].collect { it.value })
        }
        else {
            log.info("Cannot refresh access cache for null user")
        }
    }

    def removeUserFromCacheByUser(Person user) {
        if (user) {
            accessCache?.remove(user.id)
            log.info("User ${user} removed from cache")
        }
    }

    def refreshAccessCacheByRole(Role role) {

        if (role == null) {
            log.info("Cannot refresh access cache for null role")
            return
        }

        if (Role.ROLE_ADMIN == role.authority) {
            //no cache required for admin user
            return
        }

        log.info("Refreshing ${role} in accessCache")
        def users = PersonRole.findAllByRole(role).person
        users.each() { user -> refreshAccessCacheByUser(user) }
    }

    def removeRoleFromCacheByRole(Role role) {
        if (role) {
            def users = PersonRole.findAllByRole(role).person

            log.info("Removing role ${role} from cache. Updating affected users...")
            users.each { user ->
                //because the update in database will be done after this the role have to be removed by hand from the user object
                if (user.getAuthorities().remove(role)) {
                    log.info("Affected user: ${user}")
                    refreshAccessCacheByUser(user)
                }
            }
        }
    }

    // synchronized as nervous finger protection (might be triggerable via UI)
    def synchronized warmAccessCacheAsync() {
        runAsync {
            log.info("Warming the accessCache")
            //all roles with exepressions
            def cleanedRoles = Role.getAll().findAll { it.executionZoneAccessExpression && it.authority != Role.ROLE_ADMIN }
            def execZones = ExecutionZone.getAll()

            def rolesZoneAccess = new ConcurrentHashMap<Long, HashMap>()

            int i= 1
            cleanedRoles.each { role ->
                rolesZoneAccess[role.id] = new ConcurrentHashMap<Long, Boolean>()

                log.info('Testing role: ' + role.authority + ' | Progress role check: ' + i + ' / ' + cleanedRoles.size() )
                execZones.each {
                    if (roleHasAccess(role, it)) {
                        rolesZoneAccess[role.id][it.id] = true
                    }
                }
                i++
            }

            log.info('Role tests done.')

            if (!accessCache) {
                log.info("initializing accessCache")
                accessCache = new ConcurrentHashMap<Long, HashMap>()
            }

            log.info('Fill cache with collected data.')
            rolesZoneAccess.each { roleID, zoneMap ->
                PersonRole.findAllByRole(Role.findById(roleID)).each { personRole ->

                    if (!accessCache[personRole.person.id]) {
                        log.info("user ${personRole.person} not found in cache, creating")
                        accessCache[personRole.person.id] = new ConcurrentHashMap<Long, Boolean>()
                    }

                    zoneMap.each { zoneId, hasAccess ->
                        accessCache[personRole.person.id][zoneId] = hasAccess
                    }
                }
            }

            def persons = Person.getAll().findAll {
                !it.getAuthorities().contains(Role.findByAuthority(Role.ROLE_ADMIN))
            }

            persons.each { person ->

                if (!accessCache[person.id]) {
                    log.info("user ${person} not found in cache, creating")
                    accessCache[person.id] = new ConcurrentHashMap<Long, Boolean>()
                }

                execZones.each { zone ->
                    if(accessCache[person.id][zone.id] == null) {
                        accessCache[person.id][zone.id] = false
                    }
                }
            }

            log.info("Finished Warming the accessCache")
        }
    }

    def clearAccessCache() {
        log.info("clearing the accessCache")
        accessCache = new ConcurrentHashMap<Long, HashMap>()
    }
}
