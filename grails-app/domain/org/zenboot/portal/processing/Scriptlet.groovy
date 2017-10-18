package org.zenboot.portal.processing

import org.apache.log4j.Logger
import org.apache.log4j.SimpleLayout
import org.apache.log4j.WriterAppender
import org.zenboot.portal.ProcessListener
import org.zenboot.portal.processing.Processable.ProcessState

/**  A Scriptlet is a Domain-class and represents one Run of a
  *  specific Script. It stores all all related stuff in the DB
  *  and also has (unfortunately) execution-logic
  */
class Scriptlet extends Processable implements ProcessListener {

    List logslist
    String logged
    String output
    String error
    int exitCode = -1
    File file
    String tempFilePath

    transient def processOutput = new StringBuilder()
    transient def processError = new StringBuilder()
    transient def writer
    transient def appender
    transient File tempfile

    static belongsTo = [scriptletBatch: ScriptletBatch]
    static hasMany = [logslist:String]

    static constraints = { file(nullable:false) }

    static mapping = {
        output type: 'text'
        error type: 'text'
        logged type: 'text'
        tempFilePath type: 'text'
        logslist joinTable: [name: 'scriptlet_logslist', key: 'scriptlet_id', column: 'logslist_string', sqlType: 'text']
        // file sqlType: 'blob' // see #43
    }

    @Override
    protected void start(ProcessContext ctx) {
        tempfile = File.createTempFile(String.valueOf(this.id),'.tmp')
        tempFilePath = tempfile.getAbsolutePath()
        logslist = []
        this.writer = new ScriptletOutputStringWriter(tempfile)
        this.appender = createWriterAppender(this.writer)
        Logger.rootLogger.addAppender(this.appender)
        super.start(ctx)
    }

    @Override
    protected void stop(ProcessContext ctx) {
        Logger.rootLogger.removeAppender(this.appender)
        this.logslist = this.writer.toString().tokenize('\n')
        this.logged = ''
        this.output = this.processOutput.toString()
        this.error = this.processError.toString()
        this.writer = null
        this.appender = null
        this.processOutput = null
        this.processError = null
        this.tempfile.delete()
        super.stop(ctx)
    }

    private WriterAppender createWriterAppender(StringWriter stringWriter) {
        WriterAppender writerAppender = new WriterAppender(
            layout: new SimpleLayout(),
            threshold: org.apache.log4j.Level.TRACE
            )
        writerAppender.setWriter(stringWriter)
        writerAppender.addFilter(new ScriptletLogFilter(threadId:Thread.currentThread().id))
        return writerAppender
    }

    @Override
    public boolean isExecutable() {
        return this.state == ProcessState.WAITING
    }

    @Override
    public void onExecute(String command) {
        //nothing to do
    }

    @Override
    public void onFinish(int exitCode) {
        this.exitCode = exitCode
    }

    @Override
    public void onOutput(String output) {
        this.processOutput << output
    }

    @Override
    public void onError(String error) {
        this.processError << error
    }

    def getProcessOutputAsMap() {
        if (this.processOutput.length() > 0) {
            Properties props = new Properties()
            props.load(new StringReader(this.processOutput.toString()))
            return props
        }
        return [:]
    }

    @Override
    public int countProcessables() {
        return 1
    }

    @Override
    public int countExecutedProcessables() {
        (this.state == ProcessState.WAITING) ? 0 : 1
    }

    String getLogOutput() {
        File logoutput

        if(tempFilePath) {
            logoutput = new File(tempFilePath)
        }

        if(logoutput?.exists()) {
            return logoutput.readLines().join('\n')
        }
        else {
            return (logslist == null || logslist.isEmpty()) ? logged : logslist.join('\n')
        }
    }
}
