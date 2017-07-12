package org.zenboot.portal.processing

import org.apache.log4j.spi.Filter
import org.apache.log4j.spi.LoggingEvent

class ScriptletLogFilter extends Filter {

    def pattern = ~/(.*)org\.zenboot\.portal(.*)/
    def threadId

    @Override
    int decide(LoggingEvent event) {
        if (Thread.currentThread().id == this.threadId && pattern.matcher(event.getLoggerName()).matches()) {
            return Filter.ACCEPT
        } else {
            return Filter.DENY
        }
    }
}