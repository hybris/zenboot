package org.zenboot.portal.geb

import org.openqa.selenium.StaleElementReferenceException
import org.zenboot.portal.pages.ListScriptletBatchesPage
import org.zenboot.portal.pages.ShowExecutionZonePage
import org.zenboot.portal.pages.ShowScriptletBatchPage
import spock.util.concurrent.PollingConditions

class ExecutionZoneSpec extends ZenbootGebSpec {

    def 'show execution zone'() {
        when:
        to ShowExecutionZonePage

        then:
        executeScript.displayed

        when: 'a script is selected'
        sleepScriptRadioButton.click()
        waitFor {
            executeButton.enabled
            parameterValue('SLEEP_SECONDS')
        }
        and: 'a parameter value is entered'
        parameterValue('SLEEP_SECONDS').value('5')

        and: 'the execution triggered'
        executeButton.click()

        then: 'no errors are displayed'
        !errorList.displayed

        when: 'we navigate to the scriptletbatches'
        showExecutionZoneAccordion.click()
        waitFor { listScriptLetBatches.displayed }
        listScriptLetBatches.click()
        at ListScriptletBatchesPage

        then: 'the latest scriptletbatch is running'
        latestScriptStatus.text() == 'RUNNING'

        when: 'we go to its detail page'
        latestScriptLink.click()

        then: 'it is displayed'
        at ShowScriptletBatchPage
        autorefresh.click()
        stateLabel.text() == "RUNNING"

        when: 'we enable autorefresh again'
        autorefresh.click()
        def conditions = new PollingConditions(timeout: 10, delay: 1, factor: 1)

        then: 'the status will eventually become SUCCESS'
        conditions.eventually {
            try {
                assert stateLabel.text() == "SUCCESS"
            } catch (StaleElementReferenceException) {
                assert false
            }
        }

        when: 'we go back to the scriptlet batches list'
        to ListScriptletBatchesPage

        then: 'the status is also displayed as SUCCESS'
        latestScriptStatus.text() == 'SUCCESS'
    }
}