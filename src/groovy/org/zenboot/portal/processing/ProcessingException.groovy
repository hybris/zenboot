package org.zenboot.portal.processing

import org.zenboot.portal.ZenbootException

class ProcessingException extends ZenbootException {

    ProcessingException(String message, Throwable cause) {
        super(message, cause)
    }

    ProcessingException(String message) {
        super(message)
    }
}
