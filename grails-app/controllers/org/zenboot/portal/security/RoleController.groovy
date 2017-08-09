package org.zenboot.portal.security

import org.zenboot.portal.processing.ExposedExecutionZoneAction

class RoleController extends grails.plugin.springsecurity.ui.RoleController {

    def accessService

    // pagination is missing in s2ui plugin for user tab so this will show max 500 users on this tab
    def edit() {
        if (!params?.max) {
            params.put('max', 500)
        }
        super.edit()
    }

    def update() {
        super.update()
        accessService.refreshAccessCacheByRole(Role.findById(params.id))
    }

    def delete() {
        Role roleToDelete = Role.findById(params.id)
        ExposedExecutionZoneAction.getAll().findAll { it.roles.contains(roleToDelete)}.each {
            it.roles.remove(roleToDelete)

            if ( it.roles.size() == 0 ) {
                it.roles.add(Role.findByAuthority(Role.ROLE_ADMIN))
            }
            it.save(flush:true)
        }
        accessService.removeRoleFromCacheByRole(roleToDelete)
        super.delete()
    }

    def save() {
        doSave uiRoleStrategy.saveRole(params), {accessService.refreshAccessCacheByRole(Role.findByAuthority(params.authority))}
    }
}
