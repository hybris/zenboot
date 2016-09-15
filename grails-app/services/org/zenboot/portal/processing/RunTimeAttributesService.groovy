package org.zenboot.portal.processing

@SuppressWarnings("GroovyUnusedDeclaration")
public class RunTimeAttributesService {
    def grailsApplication

    public List getRuntimeAttributes() {
        return normalizeRuntimeAttributes(grailsApplication.config.zenboot.processing.attributes.toString().split(",").asType(List))
    }

    static public List normalizeRuntimeAttributes(List attributes) {
        return attributes*.trim()*.toLowerCase()
    }
}
