package org.zenboot.portal.security

class RoleController extends grails.plugin.springsecurity.ui.RoleController {

    def accessService

    def edit() {
        accessService.refreshAccessCacheByRole(Role.findById(params.id))
        doEdit()
    }

    def delete() {
        accessService.removeRoleFromChacheByRole(Role.findById(params.id))
        super.delete()
    }
}
