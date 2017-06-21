import grails.plugin.springsecurity.SpringSecurityUtils

def isAdmin = { ->
    SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')
}

navigation = {
    base {
        home()

        executionZone(controller: 'executionZone', titleText: 'Processing', action: 'list') {
            list titleText: 'Execution Zone'
            ['create', 'show', 'edit'].each {
                "${it}"(visible: false)
            }

            listExecutionZoneTypes(
                controller: 'executionZoneType', titleText: 'Execution Zone Types', action: 'list', visible: isAdmin
            )
            ['create', 'show', 'edit'].each {
                def controller = 'executionZoneType'
                "${it + controller}"(controller: controller, action: it, visible: false)
            }

            listExposedExecutionZoneActions(
                controller: 'exposedExecutionZoneAction', titleText: 'Exposed Actions', action: 'list'
            )
            ['create', 'show', 'edit'].each {
                def controller = 'exposedExecutionZoneAction'
                "${it + controller}"(controller: controller, action: it, visible: false)
            }

            listScriptletBatches controller: 'scriptletBatch', titleText: 'Executed Actions', action: 'list'
            showscriptletBatch controller: 'scriptletBatch', action: 'list', visible: false

            // only invisible
            listExecutionZoneActions controller: 'executionZoneAction', action: 'list', visible: false
            showExecutionZoneActions controller: 'executionZoneAction', action: 'show', visible: false
            deleteExecutionZoneActions controller: 'executionZoneAction', action: 'delete', visible: false
        }
        host(controller: 'host', titleText: 'Data Management', action: 'list') {
            listHosts titleText: 'Hosts', action: 'list'
            ['edit', 'show'].each {
                "${it}"(visible: false)
            }

            listServiceUrls controller: 'serviceUrl', titleText: 'ServiceUrl', action: 'list'

            listDnsEntries controller: 'dnsEntry', titleText: 'DNS', action: 'list', visible: isAdmin
            ['edit', 'show'].each {
                def controller = 'dnsEntry'
                "${it + controller}"(controller: controller, action: it, visible: false)
            }

            listCustomers controller: 'customer', titleText: 'Customer', action: 'list', visible: isAdmin
            ['edit', 'show'].each {
                def controller = 'customer'
                "${it + controller}"(controller: controller, action: it, visible: false)
            }
        }
        admin(controller: 'administration', titleText: 'Administration', action: 'index', visible: isAdmin) {
            users titleText: 'User Management', action: 'user'
            notifications controller: 'userNotification', titleText: 'User Notifications', action: 'list'
            dbConsole titleText: 'DB Console', action: 'dbconsole'
            accessCache titleText:'Access Cache', action: 'accessCache'
        }
    }
}
