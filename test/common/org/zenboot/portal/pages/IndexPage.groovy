package org.zenboot.portal.pages

import geb.Page

class IndexPage extends Page {
    static url = ""

    static at = {
        $('h1').text() == "Welcome to Zenboot"
    }
}
