package org.zenboot.portal

import java.util.Date;

class Template {
    static auditable = true
    
    String name
    String template

    static mapping = {
        template type: "text"
    }
    
    static constraints = {
        name(blank: false, unique:true)
    }
}
