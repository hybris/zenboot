package org.zenboot.portal.pages

import geb.Page

class LoginPage extends Page {
    static url = 'login/auth'

    static at = {
        $('h2.page-header').text() == "Please log in"
    }

    static content = {
        username { $('#username') }
        password { $('#password') }
        loginButton(to: IndexPage) { $('#loginButton')}
    }
}
