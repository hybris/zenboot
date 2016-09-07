package org.zenboot.portal.geb

import geb.spock.GebReportingSpec
import org.zenboot.portal.pages.LoginPage

abstract class ZenbootGebSpec extends GebReportingSpec  {
    void login() {
        to LoginPage

        username.value 'zenboot'
        password.value 'zenboot'
        loginButton.click()
    }

    def setupSpec() {
        login()
    }
}
