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
        "/rest/help"(controller: 'executionZoneRest', action: 'help')
        "/rest/execute"(controller: 'executionZoneRest', action: 'execute')
        "/rest/list"(controller: 'executionZoneRest', action: 'list')
        "/rest/listactions"(controller: 'executionZoneRest', action: 'listactions')
        "/rest/listparams"(controller: 'executionZoneRest', action: 'listparams')


        // templates
        name template : "/template/$action?/$id?" { controller='template' }
        "/properties/$id"(controller:'propertiesRest', action:'showFile')

        "500"(view:'/error')
    }
}
