package org.zenboot.portal.processing

import grails.test.mixin.*
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.zenboot.portal.processing.Processable.ProcessState

@TestFor(ScriptletBatch)
@TestMixin(DomainClassUnitTestMixin)
@Mock([ScriptletBatch, Scriptlet])
class ScriptletBatchTests {

    void testExecute() {
        ScriptletBatch processQueue = new ScriptletBatch()
        def proc1Executed = false
        processQueue.processables << this.createProcessUnit { proc1Executed = true }
        def proc2Executed = false
        processQueue.processables << this.createProcessUnit { proc2Executed = true }
        assertEquals("Process state is wrong", processQueue.state, ProcessState.WAITING)
        processQueue.execute(new ProcessContext())
        assertEquals("Process state is wrong", processQueue.state, ProcessState.SUCCESS)

        assertTrue("Process 1 not executed", proc1Executed)
        assertTrue("Process 2 not executed", proc2Executed)
    }

    void testExecuteFailure() {
        ScriptletBatch processQueue = new ScriptletBatch()
        def proc1Executed = false
        processQueue.processables << this.createProcessUnit { proc1Executed = true }
        def proc2Executed = false
        processQueue.processables << this.createProcessUnit {
            proc2Executed = true
            throw new RuntimeException("something went wrong")
        }
        assertEquals("Process state is wrong", processQueue.state, ProcessState.WAITING)


        shouldFail(ProcessingException) { processQueue.execute(new ProcessContext()) }
        assertEquals("Process state is wrong", processQueue.state, ProcessState.FAILURE)

        assertTrue("Process 1 not executed", proc1Executed)
        assertTrue("Process 2 not executed", proc2Executed)
    }

    void testProgress() {
        ScriptletBatch processQueue = new ScriptletBatch()
        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << this.createProcessUnit {}
        processQueue.state= ProcessState.RUNNING
        assertEquals("Progress should be 0", 0, processQueue.getProgress())
        processQueue.processables.eachWithIndex { processUnit, index ->
            def getProgress = { done ->
                int progress
                switch (done) {
                    case 0:
                        progress = 0
                        break
                    default:
                        progress = 100/processQueue.processables.size() * done
                        break
                }
                return progress
            }
            assertEquals("Progress was wrong", getProgress(index), processQueue.getProgress())
            processUnit.state = ProcessState.RUNNING
            assertEquals("Progress was wrong", getProgress(index+1), processQueue.getProgress())
        }
    }

    void testCountProcessUnits() {
        ScriptletBatch processQueue = new ScriptletBatch()
        assertEquals("Wrong count of process units", 0, processQueue.countProcessables())
        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << this.createProcessUnit {}
        assertEquals("Wrong count of process units", 5, processQueue.countProcessables())
    }

    void testCountExecutedProcessUnits() {
        ScriptletBatch processQueue = new ScriptletBatch()
        assertEquals("Wrong count of process units", 0, processQueue.countExecutedProcessables())

        def procUnit1 = this.createProcessUnit {}
        procUnit1.state = ProcessState.RUNNING

        def procUnit2 = this.createProcessUnit {}
        procUnit2.state = ProcessState.FAILURE

        def procUnit3 = this.createProcessUnit {}
        procUnit3.state = ProcessState.SUCCESS

        processQueue.processables << this.createProcessUnit {}
        processQueue.processables << procUnit1
        processQueue.processables << procUnit2
        processQueue.processables << procUnit3
        processQueue.processables << this.createProcessUnit {}

        assertEquals("Wrong count of process units", 3, processQueue.countExecutedProcessables())
    }

    private createProcessUnit(Closure onFailure={ ProcessContext ctx, Throwable exception -> }, Closure onSuccess={}, Closure onStart={}, Closure onStop={}, Closure process) {
        Scriptlet processUnit = new Scriptlet(description:"TestProcessUnit")
        processUnit.onFailure = onFailure
        processUnit.onSuccess = onSuccess
        processUnit.onStart = onStart
        processUnit.onStop = onStop
        processUnit.process = process
        return processUnit
    }
}