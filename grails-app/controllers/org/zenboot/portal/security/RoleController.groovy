package org.zenboot.portal.security

import org.zenboot.portal.processing.ExposedExecutionZoneAction

class RoleController extends grails.plugin.springsecurity.ui.RoleController {

    def accessService

    // pagination is missing in s2ui plugin for user tab so this will show max users on this tab
    def edit() {
        if (!params?.max) {
            params.max = Integer.MAX_VALUE
        }
        super.doEdit()
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

    def search() {
        if (!isSearch()) {
            // show the form
            return
        }

        boolean useOffset = params.containsKey('offset')
        params.sort = 'authority'
        if (!param('authority')) params.authority = 'ROLE_'

        def results = doSearch {
            like 'authority', delegate
        }

        if (results.totalCount == 1 && !useOffset) {
            params.name = results[0][authorityNameField]
            params.max = Integer.MAX_VALUE
            params.offset = 0
            params.remove 'sort'
            forward action: 'edit'
            return
        }

        renderSearch([results: results, totalCount: results.totalCount], 'authority')
    }
}
