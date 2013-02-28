package org.zenboot.portal.processing

import org.springframework.context.ApplicationEvent
import org.zenboot.portal.security.Person

class ProcessingEvent extends ApplicationEvent {

    boolean processAsync = true
    Person user

    ProcessingEvent(ExecutionZoneAction action, Person user) {
        super(action)
        this.user = user
    }

    ExecutionZoneAction getExecutionZoneAction() {
        return this.source
    }

    String toString() {
        return "${this.class.getSimpleName()} (source=${this.source}/user=${this.user})"
    }
}
