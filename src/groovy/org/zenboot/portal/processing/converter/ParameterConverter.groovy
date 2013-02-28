package org.zenboot.portal.processing.converter

interface ParameterConverter {

    boolean supports(Class clazz)

    boolean supports(def key)    

    void addParameters(Map parameters, def key, def value)

    void removeParameters(Map parameters, def key)
}
