package org.zenboot.portal.processing

import grails.test.mixin.*

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@TestMixin(ControllerUnitTestMixin)
class ExecuteExecutionZoneCommandTests {

    @Before
    void setup() {
        mockCommandObject ExecuteExecutionZoneCommand
    }

    void testOneVarEmpty() {
        ExecuteExecutionZoneCommand cmd = new ExecuteExecutionZoneCommand()

        def params = ['key':['VAR1'], 'value':['']]
        params["key"]=['VAR1']
        params["value"] = ['']
        // FIXME
        cmd.setParameters(params)
        // see ControllerUtilsTest
        // assertTrue("should have errors", cmd.hasErrors())
    }


    void testTwoVarsEmpty() {
        ExecuteExecutionZoneCommand cmd = new ExecuteExecutionZoneCommand()
        def parameters= ['key':['VAR1', 'Var2'], 'value':['', '']]
        // FIXME
        cmd.setParameters(parameters)
        // see ControllerUtilsTest
        // assertTrue("should have errors", cmd.hasErrors())
    }

    void testOneVarFilled() {
        ExecuteExecutionZoneCommand cmd = new ExecuteExecutionZoneCommand()
        def parameters= ['key':['VAR1'], 'value':['blub']]
        // FIXME
        cmd.setParameters(parameters)
        assertFalse("should have no errors", cmd.hasErrors())
    }

    void testTwoVarsFilled() {
        ExecuteExecutionZoneCommand cmd = new ExecuteExecutionZoneCommand()
        def parameters= ['key':['VAR1', 'Var2'], 'value':['a', 'b']]
        // FIXME
        cmd.setParameters(parameters)
        assertFalse("should have no errors", cmd.hasErrors())
    }

}
