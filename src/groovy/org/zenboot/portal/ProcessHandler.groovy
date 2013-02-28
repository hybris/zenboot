package org.zenboot.portal

class ProcessHandler {

    private error = new StringBuilder()
    private output = new StringBuilder()
    private processListener = []

    boolean newLine = true
    String command
    long timeout
    File workingDirectory
    int exitValue

    ProcessHandler(String command, long timeout, File workingDirectory=null) {
        this.command = command
        this.timeout = timeout
        this.workingDirectory = workingDirectory
    }

    def addProcessListener(ProcessListener procListener) {
        this.processListener.add(procListener)
    }

    def removeProcessListener(ProcessListener procListener) {
        this.processListener.remove(procListener)
    }

    def execute(def envParams=null) {
        this.log.debug(this.command)
        this.processListener.each {
            it.onExecute(this.command)
        }

        ProcessBuilder processBuilder = new ProcessBuilder(this.command.split(' '))
        if (this.workingDirectory) {
            processBuilder.directory(this.workingDirectory)
        }

        if (envParams instanceof Map) {
            if (this.log.debugEnabled) {
                this.log.debug("Set environment parameters: ${envParams}")
            }
            processBuilder.environment().putAll(envParams)
        }

        if (this.log.debugEnabled) {
            this.log.debug("Execute command '${this.command}'")
        }

        Process proc = processBuilder.start()
        proc.inputStream.eachLine {
            this.appendOutput(it)
        }
        proc.errorStream.eachLine {
            this.appendError(it)
        }
        proc.waitForOrKill(this.timeout)
        this.setExitValue(proc.exitValue())
        proc.destroy()
    }

    private void setExitValue(int exitValue) {
        this.exitValue = exitValue
        this.processListener.each {
            it.onFinish(exitValue)
        }
    }

    private appendError(String errorStr) {
        this.log.warn(errorStr)
        if (this.newLine) {
            errorStr += "\n"
        }
        this.error << errorStr
        this.processListener.each {
            it.onError(errorStr)
        }
    }

    private appendOutput(String outputStr) {
        this.log.debug(outputStr)
        if (this.newLine) {
            outputStr += "\n"
        }
        this.output << outputStr
        this.processListener.each {
            it.onOutput(outputStr)
        }
    }

    def getOutput() {
        this.output.toString()
    }

    def getError() {
        this.error.toString()
    }

    def hasError() {
        return (this.exitValue >= 2)
    }
}