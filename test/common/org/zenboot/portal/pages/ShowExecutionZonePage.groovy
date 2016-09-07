package org.zenboot.portal.pages

import geb.Page

class ShowExecutionZonePage extends Page {
    static url = 'executionZone/show/1'

    static at = {
        title == "Show ExecutionZone"
    }

    static content = {
        showZone { $('#showZone') }
        executeScript { $('#executeScript') }

        sanityCheckRadioButton { $('input', value: endsWith('sanitycheck')) }
        sleepScriptRadioButton { $('input', value: endsWith('sleep-a-bit')) }

        executeButton { $('input', type: 'submit', name: "_action_execute") }

        errorList(required: false) { $('.alert.alert-error').find('ul > li') }

        showExecutionZoneAccordion { $('#accordion-toggle-show') }
        listScriptLetBatches(wait: true, to: ListScriptletBatchesPage) { $('.listScriptLetBatchesForZoneButton') }

        parameterValue { name ->
            $("input[value='$name']").parents('tr').find('input[name="parameters.value"]')
        }
    }

}
