class UrlMappings {

    static mappings = {
        //default
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }
        
        
        //homepage
        "/"(controller:'home', action:'index')

        //REST
        "/rest/customer/$id/$property?"(controller:'customer', action:'rest')
        "/rest/action/$id/status"(controller:'executionZoneAction', action:'rest')
        "/rest/hiera/$puppetEnvironment/$qualityStage?"(controller:'hieraRest', action:'rest')
        
        "/rest/properties/$puppetEnvironment/$qualityStage?"(controller:'propertiesRest', action:'rest')
        "/rest/$url?"(controller:'exposedExecutionZoneAction', action:'rest')
        
        // templates
        "/executionZone/$execId/template/$action?/$id?"(controller:'template')
        "/properties/$id"(controller:'propertiesRest', action:'showFile')
        
        "500"(view:'/error')
    }
}
