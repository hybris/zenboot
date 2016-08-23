navigation = {
    base {
        home()

        executionZone(controller: 'executionZone', titleText: 'Processing', action: 'list') {
            list titleText: 'Execution Zone'
            ['create', 'show', 'edit'].each {
                "${it}"(visible: false)
            }

            listExecutionZoneTypes controller: 'executionZoneType', titleText: 'Execution Zone Types', action: 'list'
            ['create', 'show', 'edit'].each {
                def controller = 'executionZoneType'
                "${it + controller}"(controller: controller, action: it, visible: false)
            }

            listExposedExecutionZoneActions controller: 'exposedExecutionZoneAction', titleText: 'Exposed Actions', action: 'list'
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

            listDnsEntries controller: 'dnsEntry', titleText: 'DNS', action: 'list'
            ['edit', 'show'].each {
                def controller = 'dnsEntry'
                "${it + controller}"(controller: controller, action: it, visible: false)
            }

            listCustomers controller: 'customer', titleText: 'Customer', action: 'list'
            ['edit', 'show'].each {
                def controller = 'customer'
                "${it + controller}"(controller: controller, action: it, visible: false)
            }
        }
        admin(controller: 'administration', titleText: 'Administration', action: 'index') {
            users titleText: 'User Management', action: 'user'
            notifications controller: 'userNotification', titleText: 'User Notifications', action: 'list'
            dbConsole titleText: 'DB Console', action: 'dbconsole'

        }
    }
}