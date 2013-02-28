package org.zenboot.portal.processing

import org.zenboot.portal.ZenbootException

class ProcessingException extends ZenbootException {

    public ProcessingException(String message, Throwable cause) {
        super(message, cause)
    }

    public ProcessingException(String message) {
        super(message)
    }
}
