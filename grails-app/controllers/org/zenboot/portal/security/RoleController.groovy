package org.zenboot.portal.security

class RoleController extends grails.plugin.springsecurity.ui.RoleController {

    def accessService

    def search() {
        if (!isSearch()) {
            // show the form
            return
        }

        params.sort = 'authority'
        if (!param('authority')) params.authority = 'ROLE_'

        def results = doSearch {
            like 'authority', delegate
        }

        renderSearch([results: results, totalCount: results.totalCount], 'authority')
    }

    def edit() {
        accessService.refreshAccessCacheByRole(Role.findById(params.id))
        doEdit()
    }

    def delete() {
        accessService.removeRoleFromChacheByRole(Role.findById(params.id))
        super.delete()
    }
}
