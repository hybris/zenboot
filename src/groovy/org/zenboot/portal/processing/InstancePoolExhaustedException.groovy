package org.zenboot.portal.processing

class InstancePoolExhaustedException extends PluginExecutionException {

    public InstancePoolExhaustedException(String message, Throwable cause) {
        super(message, cause)
    }

    public InstancePoolExhaustedException(String message) {
        super(message)
    }
}
