package org.zenboot.portal

import grails.test.mixin.*
import grails.test.mixin.support.*

import org.junit.*
import org.zenboot.portal.processing.ScriptletBatch

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(ScriptletBatch)
@TestMixin(GrailsUnitTestMixin)
class ProcessHandlerTests {

    private outputScript = new File("test/resources/${this.class.getPackage().getName()}/processHandler_testOutput.sh")

    File getScript(String scriptName) {
      File script = new File("test/resources/${this.class.getPackage().getName()}/${scriptName}")
      script.setExecutable(true)
      return script
    }

    @Before
    void setUp() {
        outputScript.setExecutable(true)
    }

    @Test
    void testExecute() {
        ProcessHandler processHandler = new ProcessHandler("echo hello world", 5000)
        processHandler.newLine = true
        processHandler.execute()
        assertEquals("Output is wrong", "hello world\n", processHandler.output)
        assertEquals("Return code is wrong", 0, processHandler.exitValue)
        assertFalse("No error expected", processHandler.hasError())
    }

    @Test
    void testExecuteNoNewline() {
        ProcessHandler processHandler = new ProcessHandler("echo hello world", 5000)
        processHandler.execute()
        assertEquals("Output is wrong", "hello world\n", processHandler.output)
        assertEquals("Return code is wrong", 0, processHandler.exitValue)
        assertFalse("No error expected", processHandler.hasError())
    }

    @Test
    void testOutput() {
        ProcessHandler processHandler = new ProcessHandler("${this.outputScript} hello world exit2", 5000)
        processHandler.execute()
        assertEquals("Std-Output is wrong", "hello\nworld\nexit2\n", processHandler.output)
        assertEquals("Err-Output is wrong", "hello\nworld\nexit2\n", processHandler.error)
        assertFalse("No error expected", processHandler.hasError())
    }

    @Test
    void testExitWithError() {
        ProcessHandler processHandler = new ProcessHandler("${this.getScript("failingScript.sh")}", 1000)
        processHandler.execute()
        // 143 seems to be the exit-Value for a killed process
        assertEquals("exit-value is wrong", 14, processHandler.exitValue)
        assertTrue("Error expected", processHandler.hasError())
        assertEquals("Std-Output is wrong", "now, i'm failing\n", processHandler.output)
    }

    @Test
    void testTimeout() {
        ProcessHandler processHandler = new ProcessHandler("${this.getScript("long-running-script.sh")}", 10)
        processHandler.execute()
        // 143 seems to be the exit-Value for a killed process
        assertEquals("exit-value is wrong", 143, processHandler.exitValue)
        assertTrue("Error expected", processHandler.hasError())
        assertEquals("Std-Output is wrong", "test\n", processHandler.output)
    }


    @Test
    void testListener() {
        ProcessListener processListener = new TestProcessListener()
        ProcessHandler processHandler = new ProcessHandler("${this.outputScript} hello world", 1000)
        //test adding a process listener
        processHandler.addProcessListener(processListener)
        processHandler.execute()
        assertEquals("Std-Output is wrong", "hello\nworld\n", processListener.output.toString())
        assertEquals("Err-Output is wrong", "hello\nworld\n", processListener.error.toString())
        assertEquals("Std-Output is wrong", "hello\nworld\n", processHandler.output)
        assertEquals("Err-Output is wrong", "hello\nworld\n", processHandler.error)
        //test removing a process listener
        processHandler.removeProcessListener(processListener)
        processHandler.execute()
        assertEquals("Std-Output is wrong", "hello\nworld\n", processListener.output.toString())
        assertEquals("Err-Output is wrong", "hello\nworld\n", processListener.error.toString())
        assertEquals("Std-Output is wrong", "hello\nworld\nhello\nworld\n", processHandler.output)
        assertEquals("Err-Output is wrong", "hello\nworld\nhello\nworld\n", processHandler.error)
    }

}

class TestProcessListener implements ProcessListener {

    String command
    StringBuilder output = new StringBuilder()
    StringBuilder error = new StringBuilder()
    boolean onFinishExecuted = false

    @Override
    public void onExecute(String command) {
        this.command = command
    }

    @Override
    public void onFinish(int exitCode) {
        this.onFinishExecuted = true
    }

    @Override
    public void onOutput(String output) {
        this.output << output
    }

    @Override
    public void onError(String error) {
        this.error << error
    }
}
