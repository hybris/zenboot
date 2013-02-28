package org.zenboot.portal.processing

import org.zenboot.portal.security.Role

class ExposedExecutionZoneAction extends AbstractExecutionZoneAction {

    Set roles = []
    String url
    String cronExpression
    String description

    static hasMany = [roles: Role]

    static mapping = {
        url index:'idx_excznact_url'
    }

    static constraints = {
        url(blank:false, nullable:false, size: 1..255, unique:true)
        roles(minSize:1)
    }
}
