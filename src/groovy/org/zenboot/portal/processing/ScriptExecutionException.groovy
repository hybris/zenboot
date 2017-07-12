package org.zenboot.portal.processing

class ScriptExecutionException extends ProcessingException {

    int returnCode

    ScriptExecutionException(String message, returnCode) {
        super(message)
        this.returnCode = returnCode
    }

}
