package org.zenboot.portal.processing

import org.zenboot.portal.Host
import org.zenboot.portal.processing.converter.ParameterConverterMap
import org.zenboot.portal.security.Person

class ProcessContext {

    ParameterConverterMap parameters
    Host host
    Person user

    @Override
    String toString() {
        return "${this.class.getSimpleName()} (host=${this.host}/params=${this.parameters}/users=${this.user.username})"
    }
}
