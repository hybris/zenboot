class UrlMappings {

    static mappings = {
        //default
        "/$controller/$action?/$id?(.$format)?" {
        //"/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        //homepage
        "/"(controller:'home', action:'index')

        //REST
        "/rest/customers/$id/$property?"(controller:'customer', action:'rest')
        "/rest/hosts/$id"(controller:'host', action:'rest')
        "/rest/executionzones"(controller: "executionZone", action: "list")
        "/rest/executionzones/$id"(controller:'executionZone', action:'rest')
        "/rest/actions/$id/status"(controller:'executionZoneAction', action:'rest')
        "/rest/templates/$action?/$id?" (controller:'template')
        "/rest/properties/$puppetEnvironment/$qualityStage?"(controller:'propertiesRest', action:'rest')
        "/rest/$url?"(controller:'exposedExecutionZoneAction', action:'rest')
        "/rest/executionzones/$id/$stackName"(controller:'executionZone', action:'exec')

        //ExecutionZoneRest
        "/rest/v1"(controller: 'executionZoneRest', action: 'help')
        "/rest/v1/help"(controller: 'executionZoneRest', action: 'help')
        "/rest/v1/executionzones/list?"(controller: 'executionZoneRest', action: 'list')
        "/rest/v1/executionzones/$execId/actions/list"(controller: 'executionZoneRest', action: 'listactions')
        "/rest/v1/executionzones/$execId/actions/$execAction/params/list?"(controller: 'executionZoneRest', action: 'listparams')
        "/rest/v1/executionzones/execzonetemplate"(controller: 'executionZoneRest', action: 'execzonetemplate')
        "/rest/v1/executionzones/create"(controller: 'executionZoneRest', action: 'createzone')
        "/rest/v1/executionzones/$execId/clone"(controller: 'executionZoneRest', action: 'cloneexecutionzone')
        "/rest/v1/executionzones/$execId/actions/$execAction/$quantity/execute?"(controller: 'executionZoneRest', action: 'execute')
        "/rest/v1/executionzones/$execId/params/edit"(controller: 'executionZoneRest', action: 'changeexecutionzoneparams')
        "/rest/v1/executionzones/$execId/attributes/edit"(controller: 'executionZoneRest', action: 'changeexecutionzoneattributes')

        //ExecutionZoneActionRest
        "/rest/v1/actions/list?"(controller: 'executionZoneActionRest', action: 'listdetailedactions')

        //ServiceUrlRest
        "/rest/v1/serviceurls/list?"(controller: 'serviceUrlRest', action: 'listserviceurls')

        //HostRest
        "/rest/v1/hosts/list?"(controller: 'hostRest', action: 'listhosts')
        "/rest/v1/hosts/$hostId/edit?"(controller: 'hostRest', action: 'edithost')
        "/rest/v1/hoststates/list"(controller: 'hostRest', action: 'listhoststates')

        //CustomerRest
        "/rest/v1/customers/list?"(controller: 'customerRest', action: 'listcustomers')
        "/rest/v1/customers/$identifier/edit"(controller: 'customerRest', action: 'editcustomers')

        //UserNotificationRest
        "/rest/v1/usernotifications/list?"(controller: 'userNotificationRest', action: 'listusernotifications')
        "/rest/v1/usernotifications/$notificationId/edit"(controller: 'userNotificationRest', action: 'editusernotification')
        "/rest/v1/usernotifications/create"(controller: 'userNotificationRest', action: 'createusernotification')
        "/rest/v1/usernotificaitons/$notificationId/delete"(controller: 'userNotificationRest', action: 'deleteusernotification')

        //ExecutionZoneTypeRest
        "/rest/v1/exectypes/list"(controller: 'executionZoneTypeRest', action: 'listexectypes')
        "/rest/v1/exectypes/$execTypeId/edit"(controller: 'executionZoneTypeRest', action: 'editexectype')

        // templates
        name template : "/template/$action?/$id?" { controller='template' }
        "/properties/$id"(controller:'propertiesRest', action:'showFile')

        "500"(view:'/error')
    }
}
