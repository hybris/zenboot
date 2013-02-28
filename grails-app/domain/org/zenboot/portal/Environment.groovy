package org.zenboot.portal

enum Environment {
    DEVELOPMENT('d'),
    TESTING('t'),
    STAGING('s'),
    PRODUCTION('p'),
    UAT('u'),
    INTEGRATION('i'),

    String acronym

    Environment(String acronym) {
        this.acronym = acronym
    }
}
