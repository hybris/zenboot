package org.zenboot.portal.processing.converter

class DefaultParameterConverter implements ParameterConverter {

    @Override
    boolean supports(Class clazz) {
        return true
    }

    @Override
    public boolean supports(def key) {
        return true
    }

    @Override
    public void addParameters(Map parameters, Object key, Object value) {
        if (value.respondsTo("toString")) {
            parameters[key] = value.toString()
        } else if (value.respondsTo("text")) {
            parameters[key] = value.text()
        } else {
            parameters[key] = "${value}"
        }
    }

    @Override
    public void removeParameters(Map parameters, Object key) {
        parameters.remove(key)
    }
}
