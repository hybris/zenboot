package org.zenboot.portal.processing

class InstancePoolExhaustedException extends PluginExecutionException {

    InstancePoolExhaustedException(String message, Throwable cause) {
        super(message, cause)
    }

    InstancePoolExhaustedException(String message) {
        super(message)
    }
}
