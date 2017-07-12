package org.zenboot.portal.processing.converter

import org.zenboot.portal.Hostname

class HostnameParameterConverter implements ParameterConverter {

    private static final String HOSTNAME = "NODENAME"

    @Override
    boolean supports(Class clazz) {
        Hostname.isAssignableFrom(clazz)
    }

    @Override
    boolean supports(Object key) {
        return HOSTNAME == key
    }

    @Override
    void addParameters(Map parameters, Object key, Object value) {
        parameters[key] = value.name
        parameters[HOSTNAME] = value.name
    }

    @Override
    void removeParameters(Map parameters, Object key) {
        parameters.remove(HOSTNAME)
    }
}
