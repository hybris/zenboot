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

    def update() {
        //for some reason it creates different personrole objects. Because the update method creates his own object it is required to discard
        //the previous one
        def pr = PersonRole.findAllByPerson(Person.findById(params.id))
        pr.each {
            it.discard()
        }
        super.update()
        accessService.refreshAccessCacheByUser(Person.findById(params.id))
    }

    def delete() {
        accessService.removeUserFromCacheByUser(Person.findById(params.id))
        super.delete()
    }

    def save() {
        doSave uiUserStrategy.saveUser(params, roleNamesFromParams(), params.password) ,{accessService.refreshAccessCacheByUser(Person.findByUsername(params.username))}
    }

}
