package org.zenboot.portal.processing

import grails.test.GrailsMock
import grails.test.mixin.*
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.plugin.springsecurity.SpringSecurityUtils
import org.junit.*
import org.zenboot.portal.processing.flow.ScriptletBatchFlow
import org.zenboot.portal.processing.meta.ParameterMetadataList

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ExecutionZoneService)
@TestMixin(ServiceUnitTestMixin)
@Mock([ExecutionZoneType, ExecutionZone, ScriptletBatchService])
class ExecutionZoneServiceTests {
    def scriptletBatchService

    @Before
    void setup() {
        ConfigObject mockedConfig = new ConfigObject()
        mockedConfig.zenboot.execution.scriptDir = "scriptDir"
        service.grailsApplication = [config: mockedConfig]
        scriptletBatchService = mockFor(ScriptletBatchService)
        def scriptletBatchFlow = mockFor(ScriptletBatchFlow)
        scriptletBatchFlow.demand.getParameterMetadataList() { ->
            new ParameterMetadataList()
        }

        scriptletBatchService.demand.getScriptletBatchFlow() { a,b,c ->
            scriptletBatchFlow.createMock()
        }
        service.scriptletBatchService = scriptletBatchService.createMock()

        // this is discouraged, but mockFor did not work for SpringSecurityUtils
        SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
            return true
        }

        def execZone = new ExecutionZone(type: new ExecutionZoneType(name: 'test'), id: 1)

        GrailsMock mockZone = new GrailsMock(ExecutionZone)
        mockZone.demand.static.get() { Long id -> execZone }
    }

    @After
    void tearDown() {
        SpringSecurityUtils.metaClass = null;
    }

    void testOneVarEmpty() {
        ExecuteExecutionZoneCommand cmd = new ExecuteExecutionZoneCommand(execId: 1)

        def params = ['key':['VAR1'] as String[], 'value':[''] as String[]]
        service.setParameters(cmd, params)
        assertTrue("should have errors", cmd.hasErrors())
    }


    void testTwoVarsEmpty() {
        ExecuteExecutionZoneCommand cmd = new ExecuteExecutionZoneCommand(execId: 1)
        def parameters= ['key':['VAR1', 'Var2'] as String[], 'value':['', ''] as String[]]
        service.setParameters(cmd, parameters)
        assertTrue("should have errors", cmd.hasErrors())
    }

    void testOneVarFilled() {
        ExecuteExecutionZoneCommand cmd = new ExecuteExecutionZoneCommand(execId: 1)
        def parameters= ['key':['VAR1'] as String[], 'value':['blub'] as String[]]
        service.setParameters(cmd, parameters)
        assertFalse("should have no errors", cmd.hasErrors())
    }

    void testTwoVarsFilled() {
        ExecuteExecutionZoneCommand cmd = new ExecuteExecutionZoneCommand(execId: 1)
        def parameters= ['key':['VAR1', 'Var2'] as String[], 'value':['a', 'b'] as String[]]
        service.setParameters(cmd, parameters)
        assertFalse("should have no errors", cmd.hasErrors())
    }
}
