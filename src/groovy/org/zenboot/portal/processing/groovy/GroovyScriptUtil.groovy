package org.zenboot.portal.processing.groovy

import java.nio.file.Paths

/**
 * in a perfect world, this would be a service, but ScriptletAnnotationReader
 * is instantiated via new, and so cannot use services
 */
class GroovyScriptUtil {
    /**
     * parse a groovy script from the script stack into a class
     *
     * adds the "lib" directory from the execution zone to the class path
     * of the script to facilitate code reuse in scripts
     *
     * this assumes the current execution zone structure without substructure
     */
    static Class parseGroovyScript(File script) {
        GroovyClassLoader gcl = new GroovyClassLoader(GroovyScriptUtil.classLoader)
        def libPath = Paths.get(script.getParent(), "../../lib").toAbsolutePath().normalize()
        gcl.addClasspath(libPath.toString() + "/")
        gcl.parseClass(script)
    }
}
