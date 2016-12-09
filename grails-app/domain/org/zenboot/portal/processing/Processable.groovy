package org.zenboot.portal.processing

/*  Something executable. Currently the execution is done on Domain-Objects
 *  So currently only Scriptlet and ScriptletBatch
 */
abstract class Processable {

    Date creationDate
    Date startDate
    Date endDate
    String description
    String exceptionStacktrace
    String exceptionClass
    String exceptionMessage
    ProcessState state = ProcessState.WAITING

    transient def onFailure = { ProcessContext ctx, Throwable exception -> }
    transient def onSuccess = { ProcessContext ctx -> }
    transient def onStart = { ProcessContext ctx -> }
    transient def onStop = { ProcessContext ctx -> }
    transient def process = { ProcessContext ctx -> }

    static constraints = { description blank:false }

    static mapping = {
        table 'processing'
        exceptionStacktrace type: 'text'
        exceptionMessage type: 'text'
    }

    def beforeInsert = { this.creationDate = new Date() }

    void execute(ProcessContext ctx) {
        if (!this.isExecutable()) {
            throw new ProcessingException("${this} not executable")
        }
        if (!this.isWaiting()) {
            throw new ProcessingException("${this} is not waiting for execution")
        }
        Exception handledException
        try {
            this.start(ctx)
            try {
                if (this.log.infoEnabled) {
                    this.log.debug("Process context passed to ${this}: ${ctx}")
                }
                this.process(ctx)
                if (this.log.infoEnabled) {
                    this.log.debug("Process context received from ${this}: ${ctx}")
                }
                this.success(ctx)
            } catch (Exception exc) {
                handledException = exc
                this.failure(ctx, exc)
                this.log.warn("Process failed with handled exception " + exc.getMessage())
            } finally {
                this.stop(ctx)
            }
        } catch (Exception exc) {
            this.state = ProcessState.FAILURE
            this.trackException(exc)
            this.save(flush:true)
            if (handledException) {
                this.log.error("Process failed with unhandled exception! " +
                        "This error can be caused by a previous exception (${handledException.getMessage()}). " +
                        "Please check the logic of the failure and stop hook!", exc)
            } else {
                this.log.error("Process failed with unhandled exception! Please check the logic in all hooks for potential failures!", exc)
            }
            this.throwException(exc)
        }
        if (handledException) {
            this.throwException(handledException)
        }
    }

    private void throwException(Exception exc) {
        if (exc instanceof ProcessingException) {
            throw exc
        } else {
            throw new ProcessingException("Scriptlet stack execution failed with unexpected exception.", exc)
        }
    }

    protected void start(ProcessContext ctx) {
        if (this.log.debugEnabled) {
            this.log.debug("Start ${this}")
        }
        this.startDate = new Date()
        this.state = ProcessState.RUNNING
        this.onStart(ctx)
    }

    protected void success(ProcessContext ctx) {
        if (this.log.debugEnabled) {
            this.log.debug("${this} sucessfully finished")
        }
        this.state = ProcessState.SUCCESS
        this.onSuccess(ctx)
    }

    protected void failure(ProcessContext ctx, Throwable exception) {
        this.log.error("${this} failed", exception)
        this.state = ProcessState.FAILURE
        this.trackException(exception)
        this.onFailure(ctx, exception)
    }

    protected void stop(ProcessContext ctx) {
        this.onStop(ctx)
        this.endDate = new Date()
        this.save(flush:true)
        if (this.log.debugEnabled) {
            this.log.debug("Stop ${this}")
        }
    }

    boolean cancel() {
        if (this.isWaiting()) {
            this.state = ProcessState.CANCELED
            this.save()
            return true
        }
        return false
    }

    boolean isWaiting() {
        return (this.state == ProcessState.WAITING)
    }

    boolean isRunning() {
        return (this.state == ProcessState.RUNNING)
    }

    boolean isExecuted() {
        return (this.state == ProcessState.SUCCESS || this.state == ProcessState.FAILURE)
    }

    abstract boolean isExecutable()

    abstract int countProcessables()

    abstract int countExecutedProcessables()

    enum ProcessState {
        WAITING,
        RUNNING,
        SUCCESS,
        FAILURE,
        CANCELED
    }

    protected void trackException(Throwable exc) {
        StringWriter writer = new StringWriter()
        exc.printStackTrace(new PrintWriter(writer))
        this.exceptionStacktrace = writer.toString()
        this.exceptionClass = exc.class.getName()
        this.exceptionMessage = exc.getMessage()
    }

    int getProcessTime() {
        if (this.endDate) {
            return this.endDate.getTime() - this.startDate.getTime()
        } else if (this.startDate) {
            return (new Date()).getTime() - this.startDate.getTime()
        } else {
            return -1
        }
    }

    String toString() {
        if (this.id) {
            return "${this.class.getSimpleName()} (${this.description}/${this.id})"
        } else {
            return "${this.class.getSimpleName()} (${this.description})"
        }
    }
}
