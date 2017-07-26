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

    def update() {
        accessService.refreshAccessCacheByRole(Role.findById(params.id))
        super.update()
    }

    def delete() {
        accessService.removeRoleFromCacheByRole(Role.findById(params.id))
        super.delete()
    }

    def save() {
        doSave uiRoleStrategy.saveRole(params), {accessService.refreshAccessCacheByRole(Role.findByAuthority(params.authority))}
    }
}
