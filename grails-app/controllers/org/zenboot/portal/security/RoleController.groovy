package org.zenboot.portal.security

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
        accessService.removeRoleFromCacheByRole(Role.findById(params.id))
        super.delete()
    }

    def save() {
        doSave uiRoleStrategy.saveRole(params), {accessService.refreshAccessCacheByRole(Role.findByAuthority(params.authority))}
    }
}
