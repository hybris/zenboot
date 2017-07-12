package org.zenboot.portal.processing

class PluginExecutionException extends ProcessingException {

    PluginExecutionException(String message) {
        super(message)
    }

    PluginExecutionException(String message, Throwable cause) {
        super(message, cause)
    }
}
