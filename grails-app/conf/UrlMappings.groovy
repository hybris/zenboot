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
        "/rest/v1/executionzones/$execId/listactions"(controller: 'executionZoneRest', action: 'listactions')
        "/rest/v1/executionzones/$execId/actions/$execAction/listparams?"(controller: 'executionZoneRest', action: 'listparams')
        "/rest/v1/exectypes"(controller: 'executionZoneRest', action: 'exectypes')
        "/rest/v1/executionzones/execzonetemplate"(controller: 'executionZoneRest', action: 'execzonetemplate')
        "/rest/v1/executionzones/create"(controller: 'executionZoneRest', action: 'createzone')
        "/rest/v1/executionzones/$execId/clone"(controller: 'executionZoneRest', action: 'cloneexecutionzone')
        "/rest/v1/executionzones/$execId/actions/$execAction/$quantity/execute?"(controller: 'executionZoneRest', action: 'execute')
        "/rest/v1/executionzones/hosts?"(controller: 'executionZoneRest', action: 'listhosts')
        "/rest/v1/hoststates"(controller: 'executionZoneRest', action: 'listhoststates')

        // templates
        name template : "/template/$action?/$id?" { controller='template' }
        "/properties/$id"(controller:'propertiesRest', action:'showFile')

        "500"(view:'/error')
    }
}
