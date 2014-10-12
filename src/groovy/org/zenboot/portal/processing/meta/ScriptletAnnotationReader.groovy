package org.zenboot.portal.processing.meta

import groovy.text.SimpleTemplateEngine

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.zenboot.portal.processing.meta.annotation.Parameter
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.zenboot.portal.processing.meta.annotation.Parameters
import org.zenboot.portal.processing.meta.annotation.Scriptlet

class ScriptletAnnotationReader {

    private Pattern pattern

    private static final String PARAMETER = "Parameter"
    private static final String SCRIPTLET = "Scriptlet"

    ScriptletAnnotationReader() {
        this.pattern = ~"(#+|/{2,})\\s*@(${PARAMETER}|${SCRIPTLET})\\s*\\("
    }


    public Set getParameters(File script) {
      Class scriptletClass = this.getScriptletClass(script)

      def annotations = []
      if (scriptletClass.getAnnotation(Parameter)) {
        annotations << scriptletClass.getAnnotation(Parameter)
      }
      if (scriptletClass.getAnnotation(Parameters)) {
        annotations.addAll(scriptletClass.getAnnotation(Parameters).value())
      }

      /** Iterating through the annotationslist, transforming it to a HashSet */
      return annotations.inject(new HashSet()) { Set params, Parameter annotation ->
        params << new ParameterMetadata(
            description:annotation.description(),
            name:annotation.name(),
            defaultValue:annotation.defaultValue(),
            type:annotation.type(),
            script:script,
            visible:annotation.visible()
        )
      }
    }

    private Class getScriptletClass(File script) {
      if (script.getName().split(/\./)[-1] == "groovy") {
        GroovyClassLoader gcl = new GroovyClassLoader(this.class.classLoader)
        return gcl.parseClass(script)
      } else {
        Class scriptletClass
        Script groovyScript = this.getGroovyScript(script)
        Set loadedClasses = groovyScript.class.classLoader.parent.classCache.values().asType(Set)
        loadedClasses.remove(groovyScript.class)
        switch (loadedClasses.size()) {
            case 0:
                throw new AnnotationReaderException("Could not find any class in script '${script}' for parameter annotation resolution")
            case 1:
                scriptletClass = loadedClasses.iterator().next()
            break
            default:
                scriptletClass = this.removeSubClasses(script, loadedClasses)
            break
        }
        return scriptletClass
      }
    }

    private Script getGroovyScript(File script) {
        GroovyShell shell = new GroovyShell(this.class.classLoader)
        String groovySrc
        /*if (script.getName().split(/\./)[-1] == "groovy") {
          return shell.parse(script)
        } else { */
          groovySrc = this.convertBashToGroovyCode(script)
        //}
        return shell.parse(groovySrc)
    }

    private Class removeSubClasses(File script, Set loadedClasses) {
        Set scriptletClasses = loadedClasses.findAll {
            it.declaredClasses.length > 0
        }
        if (scriptletClasses.size() != 1) {
            throw new AnnotationReaderException("Found ${scriptletClasses.size()} classes in the script '${script}': " +
                "no unique class match! Not able to get scriptlet metadata.")
        }
        return scriptletClasses.iterator().next()
    }

    private String convertBashToGroovyCode(File script) {
        def parameters = []
        def scriptlet
        script.eachLine { String line ->
            Matcher matcher = this.pattern.matcher(line)
            if (matcher.find()) {
                def filteredLine = line.replaceFirst(matcher.group(1), "").replaceAll(/\)\s*,\s*$/, ")")
                switch (matcher.group(2)) {
                    case PARAMETER:
                    parameters << filteredLine
                    break
                    case SCRIPTLET:
                    scriptlet = filteredLine
                    break
                }
            }
        }
        String groovySrc = """
            import ${Parameters.class.canonicalName}
            import ${Parameter.class.canonicalName}
            import ${ParameterType.class.canonicalName}
            import ${Scriptlet.class.canonicalName}

            \${scriptlet}
            @Parameters([
            \${parameters.join(",\n")}
            ])
            class \${className} {}
            def annotatedClass = \${className}()
        """
        def template = new SimpleTemplateEngine().createTemplate(groovySrc)
        return template.make([
            parameters:parameters,
            scriptlet:(scriptlet ? scriptlet : ""),
            className:"AnnotatedClass_${script.name.replaceAll(/\W/, "")}"
        ])
    }


    ScriptletMetadata getScriptlet(File script) {
        Class scriptletClass = this.getScriptletClass(script)
        def annotation = scriptletClass.getAnnotation(Scriptlet)
        if (annotation) {
            return new ScriptletMetadata(author:annotation.author(), description:annotation.description(), script:script)
        } else {
            return null
        }
    }
}

class AnnotationReaderException extends Exception {

    public AnnotationReaderException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AnnotationReaderException(String message, Throwable cause) {
        super(message, cause)
    }

    public AnnotationReaderException(String message) {
        super(message)
    }

}
