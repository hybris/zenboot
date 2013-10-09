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
        "/rest/hiera/$puppetEnvironment"(controller:'hieraRest', action:'rest')
        "/rest/hiera/$puppetEnvironment/$qualityStage"(controller:'hieraRest', action:'rest')
        "/rest/properties/$puppetEnvironment/$propertyFile"(controller:'propertiesRest', action:'rest')
        "/rest/$url?"(controller:'exposedExecutionZoneAction', action:'rest')        
        
        "500"(view:'/error')
    }
}
