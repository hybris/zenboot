package org.zenboot.portal.processing.converter

import org.zenboot.portal.Host

class HostParameterConverter implements ParameterConverter {

    private static final String HOST_IP = "IP"
    private static final String HOST_MAC = "MAC"
    private static final String HOST_ID = "ID"

    @Override
    boolean supports(Class clazz) {
        Host.isAssignableFrom(clazz)
    }

    @Override
    boolean supports(Object key) {
        return [HOST_IP, HOST_MAC, HOST_ID].contains(key)
    }

    @Override
    void addParameters(Map parameters, Object key, Object value) {
        parameters[HOST_IP] = value.ipAddress
        parameters[HOST_MAC] = value.macAddress
        parameters[HOST_ID] = value.instanceId
    }

    @Override
    void removeParameters(Map parameters, Object key) {
        parameters.remove(HOST_IP)
        parameters.remove(HOST_MAC)
        parameters.remove(HOST_ID)
    }
}
