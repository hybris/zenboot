package org.zenboot.portal.processing


class ScriptletOutputStringWriter extends StringWriter {

    Scriptlet scriptlet

    private Date lastUpdate = new Date()
    private int uncommitedLines = 0

    int lineThreshold = 2
    int syncTimeout = 5
    boolean saveWithFlush = true

    ScriptletOutputStringWriter(Scriptlet scriptlet) {
        super()
        this.scriptlet = scriptlet
    }


    ScriptletOutputStringWriter(Scriptlet scriptlet, int initialSize) {
        super(initialSize)
        this.scriptlet = scriptlet
    }

    @Override
    void write(char[] cbuf) throws IOException {
        super.write(cbuf)
        this.updateScriptlet(new String(cbuf))
    }

    @Override
    void write(int c) {
        super.write(c)
        this.updateScriptlet(String.valueOf(c))
    }

    @Override
    void write(char[] cbuf, int off, int len) {
        super.write(cbuf, off, len)
        this.updateScriptlet(new String(cbuf).substring(off, len))
    }

    @Override
    void write(String str) {
        super.write(str)
        this.updateScriptlet(str)
    }

    @Override
    void write(String str, int off, int len) {
        super.write(str, off, len)
        this.updateScriptlet(new String(str).substring(off, len))
    }

    private updateScriptlet(String data) {
        this.uncommitedLines += data.count("\n")
        Date now = new Date()
        if (this.uncommitedLines >= this.lineThreshold || ((this.lastUpdate.time - now.time) >= (this.syncTimeout * 1000) && this.uncommitedLines > 0)) {
            this.scriptlet.output = this.buffer.toString()
            this.scriptlet.save(this.saveWithFlush)
            this.uncommitedLines = 0
            this.lastUpdate = now
        }
    }
}
