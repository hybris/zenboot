package org.zenboot.portal.processing

@SuppressWarnings("GroovyUnusedDeclaration")
class RunTimeAttributesService {
    def grailsApplication

    List getRuntimeAttributes() {
        return normalizeRuntimeAttributes(grailsApplication.config.zenboot.processing.attributes.toString().split(",").asType(List))
    }

    static List normalizeRuntimeAttributes(List attributes) {
        return attributes*.trim()*.toLowerCase()
    }
}
