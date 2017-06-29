package org.zenboot.portal.security

class UserController extends grails.plugin.springsecurity.ui.UserController {

    def accessService

    @Override
    def search() {
        if (!isSearch()) {
            // show the form
            return
        }

        def results = doSearch { ->
            def usernamePattern = '%' + params['username'] + '%'
            delegate.or {
                ilike 'username', usernamePattern
                ilike 'email', usernamePattern
                ilike 'displayName', usernamePattern
            }

            eqBoolean 'accountExpired', delegate
            eqBoolean 'accountLocked', delegate
            eqBoolean 'enabled', delegate
            eqBoolean 'passwordExpired', delegate
        }

        renderSearch results: results, totalCount: results.totalCount,
                'accountExpired', 'accountLocked', 'enabled', 'passwordExpired', 'username', 'email', 'displayName'
    }

    def edit() {
        accessService.refreshAccessCacheByUser(Person.findById(params.id))
        doEdit()
    }

    def delete() {
        accessService.removeUserFromCacheByUser(Person.findById(params.id))
        super.delete()
    }

}
