package org.zenboot.portal

class ZenbootException extends Exception {

    ZenbootException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace)
    }

    ZenbootException(String message, Throwable cause) {
        super(message, cause)
    }

    ZenbootException(String message) {
        super(message)
    }
}
