package org.zenboot.portal.pages

import geb.Page

class ShowScriptletBatchPage extends Page {
    static url = { 'scriptletBatch/show' }

    static at = {
        title == "Show ScriptletBatch"
    }

    static content = {
        autorefresh { $('input', name: 'autorefresh') }
        stateLabel { $('dt', text: contains('State')).next('dd') }
    }

}
