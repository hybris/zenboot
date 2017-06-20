package org.zenboot.portal.processing

import grails.plugin.springsecurity.SpringSecurityUtils
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role
import org.zenboot.portal.security.PersonRole

@SuppressWarnings("GroovyUnusedDeclaration")
public class AccessService {
    def springSecurityService

    /* The accessCache is a dynamic datastructure looking like this:
       {1={1=false, 2=true, 3=false, 4=false}, 2={1=false, 2=false, 3=false, 4=false}}
       The fist level is a ExecutionZone.id, the second Level is a Person.id and
       the boolean is the access from that person to that zone
    */
    def accessCache

    private boolean roleHasAccess(Role role, ExecutionZone executionZone) {
        def expression = role.executionZoneAccessExpression
        try {
            return Eval.me("executionZone", executionZone, expression == null ? "" : expression)

        } catch (Exception e) {
            this.log.error("executionZoneAccessExpression '$expression' from role '$role' threw an exception", e)
            return false
        }
    }

    private boolean rolesHaveAccess(Set<Role> roles, ExecutionZone zone) {
      roles.any() {
        roleHasAccess(it, zone)
      }
    }

    public boolean userHasAccess(ExecutionZone zone) {
      SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) ||
        userHasAccess(springSecurityService.currentUser, zone)
    }

    public boolean userHasAccess(Person user, ExecutionZone zone) {
      if (accessCache == null) {
        this.log.info("initializing accessCache")
        accessCache = [:]
      }
      if (accessCache[zone.id] == null) {
        this.log.info("zone ${zone} not found in cache, creating")
        accessCache[zone.id] = [:]
      }
      if (accessCache[zone.id][user.id] == null) {
        def hasAccess = rolesHaveAccess(user.getAuthorities(), zone)
        accessCache[zone.id][user.id] = hasAccess
      } else {
        accessCache[zone.id][user.id]
      }
    }

    public invalidateAccessCacheByZone(ExecutionZone zone) {
      accessCache[zone.id] = null
    }

    public invalidateAccessCacheByUser(Person user) {
      this.log.info("invalidating ${user} in accessCache")
      if (accessCache != null) {
        accessCache.each() { key, zone ->
          zone.remove(user.id)
        }
      }
    }

    public invalidateAccessCacheByRole(Role role) {
      this.log.info("invalidating ${role} in accessCache")
      if (accessCache != null) {
        def users = PersonRole.findAllByRole(role).person
        users.each() {
          invalidateAccessCacheByUser(it)
        }
      }
    }

    public warmAccessCacheAsync() {
      runAsync {
        this.log.info("Warming the accessCache")
        def execZones = ExecutionZone.findAll()
        execZones.each() { zone ->
          this.log.info("Warming accessCache for zone ${zone}")
          Person.findAll().each() { person ->
            this.log.info("Warming accessCache for person ${person}")
            this.userHasAccess(person, zone)
            Thread.sleep(500)
          }
        }
      }
    }
}
