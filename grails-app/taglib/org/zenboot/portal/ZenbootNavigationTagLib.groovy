package org.zenboot.portal

import org.codehaus.groovy.grails.io.support.AntPathMatcher
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.zenboot.portal.security.Role

// FIXME keeping this file around as we probably need to re-implement some of its functionality
class ZenbootNavigationTagLib {

    static namespace = "apNav"

    def navigationService
    def springSecurityService

    def renderMenu = { attrs, body ->
        return

        def group = attrs.group ?: '*'
        def var = attrs.var
        def isMainMenu = attrs.mainMenu
        def path = navigationService.reverseMapActivePathFor(controllerName, actionName, params)
        def items = navigationService.byGroup[group]
        items.each { item ->
            // set items active
            if (isMainMenu) {
                item.active = (item.path[0] == path[0])
            } else {
                item.active = (item.path == path)
            }

            // filter items which are outside of the path (ignore main-menu items here)
            if (!isMainMenu && item.path[0] != path[0]) {
                return
            }

            boolean renderItem = false
            if (this.isVisible(item)) {
                renderItem = true
            } else if (isMainMenu) {
                //main menu items with visible sub-items are rendered
                //but the link of this mainmenu-item has to point to the visible sub-item
                def visibleSubItem = this.getVisibleSubitem(item)
                if (visibleSubItem) {
                    renderItem = true
                    def itemClone = visibleSubItem.clone()
                    itemClone.title = item.title
                    item = itemClone
                }
            }

            if (renderItem) {
                out << body(var ? [(var):item] : item)
            }
        }
    }

    private def getVisibleSubitem(def item) {
        def items = navigationService.byGroup['*']
        items.find {
            it.path[0] == item.path[0] && this.isVisible(it) 
        }
    }

    private boolean isVisible(def item) {
        if (item.isVisible == null || item.isVisible) {
            if (springSecurityService.authentication.authorities*.authority.contains(Role.ROLE_ADMIN)) {
                return true
            } else if (item.params?.containsKey('restricted')) {
                def requiredRoles = item.params['restricted']
                if (!(requiredRoles instanceof Collection)) {
                    requiredRoles = [requiredRoles]
                }
                return !springSecurityService.authentication.authorities.intersect(requiredRoles).empty
            } else {
                def urlPatterns = grailsApplication.config.grails.plugins.springsecurity.controllerAnnotations.staticRules
                // FIXME test
                AntPathMatcher urlMatcher = new AntPathMatcher()
                return urlPatterns.any { String urlPattern, List roles ->
                    def target = "/${item.controller}/${item.action}"
                    if ( urlMatcher.match(urlPattern, target) ) {

                        return !springSecurityService.authentication.authorities.intersect(roles).empty
                    }
                }
            }
        }
        return false
    }

}
