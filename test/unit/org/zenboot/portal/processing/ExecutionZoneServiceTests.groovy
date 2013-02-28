package org.zenboot.portal.processing

import grails.test.mixin.*
import grails.test.mixin.services.ServiceUnitTestMixin

import org.junit.*


/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ExecutionZoneService)
@TestMixin(ServiceUnitTestMixin)
@Mock([ExecutionZoneType])
class ExecutionZoneServiceTests {

    void testGetExecutionZoneTypes() {
        ConfigObject mockedConfig = new ConfigObject()
        mockedConfig.zenboot.execution.scriptDir = "scriptDir"
        def service = getExecutionZoneService(mockedConfig)
        //FIXME
        //def execZoneTypes = service.getExecutionZoneTypes()
        //assertFalse(execZoneTypes.empty)
    }



    private getExecutionZoneService(ConfigObject mockedConfig) {
        service.grailsApplication = [config:mockedConfig]
        return service
    }

}
