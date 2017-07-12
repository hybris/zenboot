package org.zenboot.portal.processing

class ExecutionZoneException extends Exception {

    ExecutionZoneException(String message, Throwable cause) {
        super(message, cause)
    }

    ExecutionZoneException(String message) {
        super(message)
    }
}
