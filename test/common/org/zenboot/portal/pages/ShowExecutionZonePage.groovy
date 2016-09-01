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
    }
}
