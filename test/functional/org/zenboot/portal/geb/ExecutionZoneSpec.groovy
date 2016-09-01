package org.zenboot.portal.geb

import org.zenboot.portal.pages.ShowExecutionZonePage

class ExecutionZoneSpec extends ZenbootGebSpec {

    def 'show execution zone'() {
        when:
        to ShowExecutionZonePage

        then:
        executeScript.displayed
    }
}