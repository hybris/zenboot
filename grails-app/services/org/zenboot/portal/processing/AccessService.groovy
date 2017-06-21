package org.zenboot.portal.processing

import grails.plugin.springsecurity.SpringSecurityUtils
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role
import org.zenboot.portal.security.PersonRole

import java.util.concurrent.ConcurrentHashMap

@SuppressWarnings("GroovyUnusedDeclaration")
public class AccessService {
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

    // Might not be 100% Threadsafe but hopefully something above 99% ;-)
    public synchronized boolean userHasAccess(Person user, ExecutionZone zone) {
      if (accessCache == null) {
        this.log.info("initializing accessCache")
        accessCache = new ConcurrentHashMap<Long, HashMap>()
      }
      if (accessCache[user.id] == null) {
        this.log.info("user ${user} not found in cache, creating")

        accessCache[user.id] = new ConcurrentHashMap<Long, Boolean>()
      }
      if (accessCache[user.id][zone.id] == null) {
        def hasAccess = rolesHaveAccess(user.getAuthorities(), zone)
        // concurrency is quite unlikely here until users use multiple browsers
        // impact would be a clash with the invalidate-methods
        // 1. invalidation-method removed a user
        //    --> NullPointer
        try {
          accessCache[user.id][zone.id] = hasAccess
        } catch (NullPointerException npe) {
          return false
        }

      }
      try {
        def hasAccess = accessCache[user.id][zone.id]
        hasAccess || false
      } catch (NullPointerException npe) {
        return false
      }
    }

    public invalidateAccessCacheByZone(ExecutionZone zone) {
      if (zone && accessCache) {
        accessCache.each() { key, user ->
          user.remove(zone.id)
        }
      }
    }

    public invalidateAccessCacheByUser(Person user) {
      this.log.info("invalidating ${user} in accessCache")
      if (user && accessCache && accessCache[user.id]) {
        accessCache.remove(user.id)
      }
    }

    public invalidateAccessCacheByRole(Role role) {
      this.log.info("invalidating ${role} in accessCache")
      if (accessCache) {
        def users = PersonRole.findAllByRole(role).person
        users.each() { user ->
          invalidateAccessCacheByUser(user)
        }
      }
    }

    // synchronized as nervous finger protection (might be triggerable via UI)
    public synchronized warmAccessCacheAsync() {
      runAsync {
        this.log.info("Warming the accessCache")
        def execZones = ExecutionZone.findAll()
        execZones.each() { zone ->
          this.log.info("Warming accessCache for zone ${zone}")
          Person.findAll().each() { person ->
            this.log.debug("Warming accessCache for person ${person}")
            this.userHasAccess(person, zone)
            Thread.sleep(50)
          }
        }
        this.log.info("Finished Warming the accessCache")
      }
    }

    public clearAccessCache() {
      this.log.info("clearing the accessCache")
      accessCache = new ConcurrentHashMap<Long, HashMap>()
    }
}
