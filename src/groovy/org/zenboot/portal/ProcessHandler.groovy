package org.zenboot.portal

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
        error.addProcessListener(procListener)
        output.addProcessListener(procListener)
        processListener.add(procListener)
    }

    def removeProcessListener(ProcessListener procListener) {
        processListener.remove(procListener)
        error.removeProcessListener(procListener)
        output.removeProcessListener(procListener)
    }

    def execute(def envParams=null) {
        log.debug(command)
        processListener.each {
            it.onExecute(command)
        }

        ProcessBuilder processBuilder = new ProcessBuilder(this.command.split(' '))
        if (workingDirectory) {
            processBuilder.directory(workingDirectory)
        }

        if (envParams instanceof Map) {
            if (log.debugEnabled) {
                log.debug("Set environment parameters: ${envParams}")
            }
            processBuilder.environment().putAll(envParams)
        }

        if (log.debugEnabled) {
            log.debug("Execute command '${command}'")
        }

        Process proc = processBuilder.start()

        proc.consumeProcessOutput(output,error)
        proc.waitForOrKill(this.timeout)
        Thread.sleep(1000)

        setExitValue(proc.exitValue())
        proc.destroy()
    }

    private void setExitValue(int exitValue) {
        this.exitValue = exitValue
        processListener.each {
            it.onFinish(exitValue)
        }
    }

    def getOutput() {
        output.toString()
    }

    def getError() {
        error.toString()
    }

    def hasError() {
        return (exitValue >= 2)
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
      processListener.add(procListener)
  }

  def removeProcessListener(ProcessListener procListener) {
      processListener.remove(procListener)
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
