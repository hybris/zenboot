package org.zenboot.portal.processing

class ScriptExecutionException extends ProcessingException {

    def int returnCode

    public ScriptExecutionException(String message, returnCode) {
        super(message)
        this.returnCode = returnCode
    }

}
