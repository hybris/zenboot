package org.zenboot.portal.processing


class ScriptletOutputStringWriter extends StringWriter {

    Scriptlet scriptlet
    File tempFile

    private Date lastUpdate = new Date()
    private int uncommitedLines = 0

    //all 30 lines the process output will be writen to the database
    int lineThreshold = 2
    int syncTimeout = 5

    ScriptletOutputStringWriter(File tempFile) {
        super()
        this.tempFile = tempFile
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        super.write(cbuf)
        this.updateScriptlet(new String(cbuf))
    }

    @Override
    public void write(int c) {
        super.write(c)
        this.updateScriptlet(String.valueOf(c))
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        super.write(cbuf, off, len)
        this.updateScriptlet(new String(cbuf).substring(off, len))
    }

    @Override
    public void write(String str) {
        super.write(str)
        this.updateScriptlet(str)
    }

    @Override
    public void write(String str, int off, int len) {
        super.write(str, off, len)
        this.updateScriptlet(new String(str).substring(off, len))
    }

    private updateScriptlet(String data) {
        this.uncommitedLines += data.count("\n")
        Date now = new Date()
        if (this.uncommitedLines >= this.lineThreshold || ((this.lastUpdate.time - now.time) >= (this.syncTimeout * 1000) && this.uncommitedLines > 0)) {
            String content = this.buffer.toString()
            List logOutput =  content.tokenize('\n')
            if (logOutput.size() > tempFile.readLines().size()) {
                logOutput[tempFile.readLines().size()..-1].each {
                    tempFile << it + '\n'
                }
            }
            this.uncommitedLines = 0
            this.lastUpdate = now
        }
    }
}
