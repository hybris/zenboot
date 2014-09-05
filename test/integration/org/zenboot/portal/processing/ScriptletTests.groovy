package org.zenboot.portal.processing

import static org.junit.Assert.*

import org.junit.*
import org.zenboot.portal.processing.Processable.ProcessState
import org.zenboot.portal.processing.converter.ParameterConverterMap

class ScriptletTests {

    @Test
    void testExecute() {
        def onFailureExecuted = false
        def onFailure = { ProcessContext ctx, Throwable exception ->
            ctx.parameters['failure'] = true
            onFailureExecuted = true
        }

        def onSuccessExecuted = false
        def onSuccess = { ProcessContext ctx ->
            assertNotNull("Context was null", ctx)
            verifyProcessContextState(['start':true, 'process':true], ctx)
            ctx.parameters['success'] = true
            onSuccessExecuted = true
        }

        def onStartExecuted = false
        def onStart = { ProcessContext ctx ->
            assertNotNull("Context was null", ctx)
            verifyProcessContextState([:], ctx)
            ctx.parameters['start'] = true
            onStartExecuted = true
        }

        def onStopExecuted = false
        def onStop = { ProcessContext ctx ->
            assertNotNull("Context was null", ctx)
            verifyProcessContextState(['start':true, 'process':true, 'success':true], ctx)
            ctx.parameters['stop'] = true
            onStopExecuted = true
        }

        def processExecuted = false
        def process = { ProcessContext ctx ->
            assertNotNull("Context was null", ctx)
            verifyProcessContextState(['start':true], ctx)
            ctx.parameters['process'] = true
            processExecuted = true
        }

        Scriptlet processUnit = this.createProcessUnit(onFailure, onSuccess, onStart, onStop, process)
        assertEquals(processUnit.state, ProcessState.WAITING)
        processUnit.execute(new ProcessContext(parameters:new ParameterConverterMap(parameterConverters:[:])))
        assertEquals(processUnit.state, ProcessState.SUCCESS)

        assertFalse("onFailure should not be called", onFailureExecuted)
        assertTrue("onSuccess was not called", onSuccessExecuted)
        assertTrue("onStart was not called", onStartExecuted)
        assertTrue("onStop was not called", onStopExecuted)
        assertTrue("process was not executed", processExecuted)
    }

    @Test
    void testExecuteFailure() {
        def onFailureExecuted = false
        def onFailure = { ProcessContext ctx, Throwable exception ->
            assertNotNull("Context was null", ctx)
            assertEquals("exeception of onFalilure is not expected", exception.class, RuntimeException)
            verifyProcessContextState(['start':true, 'process':true], ctx)
            ctx.parameters['failure'] = true
            onFailureExecuted = true
        }

        def onSuccessExecuted = false
        def onSuccess = { def currentResult ->
            assertNotNull("Context was null", ctx)
            onSuccessExecuted = true
        }

        def onStartExecuted = false
        def onStart = { ProcessContext ctx ->
            assertNotNull("Context was null", ctx)
            verifyProcessContextState([:], ctx)
            ctx.parameters['start'] = true
            onStartExecuted = true
        }

        def onStopExecuted = false
        def onStop = { ProcessContext ctx ->
            assertNotNull("Context was null", ctx)
            verifyProcessContextState(['start':true, 'process':true, 'failure':true], ctx)
            ctx.parameters['stop'] = true
            onStopExecuted = true
        }

        def processExecuted = false
        def process = { ProcessContext ctx ->
            assertNotNull("Context was null", ctx)
            verifyProcessContextState(['start':true], ctx)
            ctx.parameters['process'] = true
            processExecuted = true
            throw new RuntimeException("something failed")
        }

        Scriptlet processUnit = this.createProcessUnit(onFailure, onSuccess, onStart, onStop, process)
        assertEquals("State should be waiting", ProcessState.WAITING, processUnit.state)

        try {
            processUnit.execute(new ProcessContext(parameters:new ParameterConverterMap(parameterConverters:[:])))
        } catch (ProcessingException exc) {
        }
        assertEquals("State should be failure", ProcessState.FAILURE, processUnit.state)

        assertTrue("onFailure should be called", onFailureExecuted)
        assertFalse("onSuccess should not be called", onSuccessExecuted)
        assertTrue("onStart was not called", onStartExecuted)
        assertTrue("onStop was not called", onStopExecuted)
        assertTrue("process was not executed", processExecuted)
        assertTrue(processExecuted)
    }

    @Test
    void testProcessOutputMap() {
        Scriptlet processUnit = new Scriptlet()
        processUnit.file = new File("/tmp/zenboot-test.sh")
        processUnit.description = "Just for testing..."
        processUnit.processOutput << "#this is a comment\n"
        processUnit.processOutput << "key1=value1\n"
        processUnit.processOutput << "some text\n"
        processUnit.processOutput << "key2 = value2\n"
        Map outputAsMap = processUnit.getProcessOutputAsMap()
        assertEquals("number of values in outputMap are different", 3, outputAsMap.size())
        assertEquals("map entry 1 was wrong", "value1", outputAsMap.get("key1"))
        assertEquals("map entry 2 was wrong", "value2", outputAsMap.get("key2"))
        assertEquals("map entry 3 was wrong", "text", outputAsMap.get("some"))
    }

    @Test
    void testProcessUnitLogTracking() {
        def logMessage = "*#-What a spooky message#-*"
        Scriptlet processUnit = new Scriptlet()
        processUnit.process = { log.debug(logMessage) }
        processUnit.execute()
        assertTrue("Log entry not found", processUnit.logged.contains(logMessage))
    }

    private createProcessUnit(Closure onFailure, Closure onSuccess, Closure onStart, Closure onStop, Closure process) {
        Scriptlet processUnit = new Scriptlet(description:"TestProcessUnit")
        processUnit.onFailure = onFailure
        processUnit.onSuccess = onSuccess
        processUnit.onStart = onStart
        processUnit.onStop = onStop
        processUnit.process = process
        return processUnit
    }

    private void verifyProcessContextState(Map expected, ProcessContext ctx) {
        if (expected.empty) {
            assertTrue("Result should be empty", ctx.parameters.empty)
        } else {
            assertEquals("Parameters size is different (${ctx.toString()}):", expected.size(), ctx.parameters.size())
            def keys = expected.keySet()
            keys.each {
                //be aware, that the ctx.parameters always returns STRING values [see ParameterConverters]! If you want work type-safe, use ctx.getObject()
                assertEquals("Maps content is different", expected[it].asType(String), ctx.parameters[it])
                //type-safe verification with ctx.getObject()
                assertEquals("Maps content is different", expected[it], ctx.parameters.getObject(it))
            }
        }
    }
}
