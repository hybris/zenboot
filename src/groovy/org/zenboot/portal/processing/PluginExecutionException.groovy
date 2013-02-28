package org.zenboot.portal.processing

class PluginExecutionException extends ProcessingException {

    public PluginExecutionException(String message) {
        super(message)
    }

    public PluginExecutionException(String message, Throwable cause) {
        super(message, cause)
    }
}
