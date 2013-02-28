package org.zenboot.portal

import grails.test.mixin.*
import grails.test.mixin.services.ServiceUnitTestMixin

import org.junit.*

@TestFor(HostService)
@TestMixin(ServiceUnitTestMixin)
@Mock([Host, Hostname])
class HostServiceTests {

    void testCountAvailableHosts() {
        new Host(state: HostState.COMPLETED).save(validate:false)
        new Host(state: HostState.BROKEN).save(validate:false)
        new Host(state: HostState.ACCESSIBLE).save(validate:false)

        def mockedConfig = new ConfigObject()
        mockedConfig.zenboot.host.instances.poolSize = 5
        assertEquals("Expected different amount of available instances", 2, this.getHostService(mockedConfig).countAvailableHosts())
    }

    void testGetEnvironment() {
        def mockedConfig = new ConfigObject()
        mockedConfig.zenboot.environment = 'uat'
        assertEquals("Environment is wrong", Environment.UAT, this.getHostService(mockedConfig).environment)
    }

    void testExpiryDate() {
        def mockedConfig = new ConfigObject()
        mockedConfig.zenboot.host.instances.lifetime = 86400

        GregorianCalendar calendar = GregorianCalendar.getInstance()
        calendar.add(GregorianCalendar.DAY_OF_MONTH, 1)

        assertEquals("Environment is wrong", calendar.getTime().toString(), this.getHostService(mockedConfig).expiryDate.toString())
    }

    void testGetHostname() {
        new Hostname(name:"host1-t-1", creationDate: new Date()).save(validate:false)

        def mockedConfig = new ConfigObject()
        mockedConfig.zenboot.host.instances.defaultNamePattern = "testname-\${env}-\${vmId}"

        //IDs are note created in unit tests, so vmId will result to "null"
        assertEquals("Hostname wrong", "testname-s-2", this.getHostService(mockedConfig).nextHostName(Environment.STAGING).name)
    }

    private getHostService(ConfigObject mockedConfig) {
        service.grailsApplication = [config:mockedConfig]
        return service
    }
}