package org.zenboot.portal.pages

import geb.Page

class ListScriptletBatchesPage extends Page {
    static url = 'scriptletBatch/list'

    static at = {
        title == "ScriptletBatch List"
    }



    static content = {
        scriptletBatchList { $('#scriptlet-batch-list') }
        latestScript { scriptletBatchList.find('tbody tr') }
        latestScriptStatus {
            def stateColumn = scriptletBatchList.find('thead th').findIndexOf { it.text() == 'State' }
            latestScript.find('td')[stateColumn]
        }
        latestScriptLink {
            latestScript.find('td a', href: contains('/scriptletBatch/show/')).first()
        }
    }

}
