package org.zenboot.portal
import java.lang.AbstractStringBuilder

class ProcessHandler {

    // For onError
    private error = new ObservableStringBuilder("error")

    // For onOutput
    private output = new ObservableStringBuilder("output")

    // For onExecute and onFinish
    private processListener = []

    boolean newLine = true
    String command
    long timeout
    File workingDirectory
    int exitValue

    int getExitValue() {
      return exitValue
    }

    ProcessHandler(String command, long timeout, File workingDirectory=null) {
        this.command = command
        this.timeout = timeout
        this.workingDirectory = workingDirectory
    }

    def addProcessListener(ProcessListener procListener) {
        this.error.addProcessListener(procListener)
        this.output.addProcessListener(procListener)
        this.processListener.add(procListener)
    }

    def removeProcessListener(ProcessListener procListener) {
        this.processListener.remove(procListener)
        this.error.removeProcessListener(procListener)
        this.output.removeProcessListener(procListener)
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

        proc.consumeProcessOutput(output,error)
        proc.waitForOrKill(this.timeout)
        Thread.sleep(1000)

        this.setExitValue(proc.exitValue())
        proc.destroy()
    }

    private void setExitValue(int exitValue) {
        this.exitValue = exitValue
        this.processListener.each {
            it.onFinish(exitValue)
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

class ObservableStringBuilder implements Appendable {

  String notificationType
  StringBuilder wrappedStringBuilder = new StringBuilder()
  StringBuilder oneLineBuilder = new StringBuilder()
  private processListener = []

  ObservableStringBuilder(String notificationType) {
    this.notificationType = notificationType
  }

  def addProcessListener(ProcessListener procListener) {
      this.processListener.add(procListener)
  }

  def removeProcessListener(ProcessListener procListener) {
      this.processListener.remove(procListener)
  }

  Appendable append(char c) {
    wrappedStringBuilder.append(c)
    oneLineBuilder.append(c)
    if (c == "\n") {
      notifyThem()
      oneLineBuilder = new StringBuilder()
    }
    return this
  }

  Appendable append(CharSequence csq) {
    wrappedStringBuilder.append(csq)
    oneLineBuilder.append(csq)
    notifyThem()
    oneLineBuilder = new StringBuilder()
    return this
  }

  Appendable append(CharSequence csq, int start, int end) {
    wrappedStringBuilder.append(csq,start,end)
    return this
  }

  String toString() {
    return wrappedStringBuilder.toString()
  }

  void notifyThem() {
    this.processListener.each {
        if (notificationType.equals("error")) {
          it.onError(oneLineBuilder.toString())
        }

        if (notificationType.equals("output")) {
          it.onOutput(oneLineBuilder.toString())
        }
    }
  }


}
