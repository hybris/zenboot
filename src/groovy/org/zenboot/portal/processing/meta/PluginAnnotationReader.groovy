package org.zenboot.portal.processing.meta

import org.zenboot.portal.processing.meta.annotation.Parameter
import org.zenboot.portal.processing.meta.annotation.Parameters
import org.zenboot.portal.processing.meta.annotation.Plugin

class PluginAnnotationReader {

	private GroovyClassLoader gcl

	PluginAnnotationReader(ClassLoader classLoader=null) {
		if (classLoader) {
			this.gcl = new GroovyClassLoader(classLoader)
		} else {
			this.gcl = new GroovyClassLoader()
		}
	}

	Set getParameters(File script, String field) {
		return this.getParameters(script, [field])
	}

	Set getParameters(File script, def fields) {
		Class pluginClass = this.gcl.parseClass(script)

		def declaredFields = pluginClass.getDeclaredFields()*.name

		def annotations = []

		fields.each { String field ->
			if (declaredFields.contains(field)) {
				def parameterAnnotation = pluginClass.getDeclaredField(field).getAnnotation(Parameter)
				if (parameterAnnotation) {
					annotations << parameterAnnotation
				}
				def parametersAnnotation = pluginClass.getDeclaredField(field).getAnnotation(Parameters)
				if (parametersAnnotation) {
					annotations.addAll(parametersAnnotation.value())
				}
			}
		}

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

	PluginMetadata getPlugin(File script) {
		Class pluginClass = this.gcl.parseClass(script)
		def annotation = pluginClass.getAnnotation(Plugin)
		if (annotation) {
			return new PluginMetadata(author:annotation.author(), description:annotation.description(), script:script)
		} else {
			return null
		}
	}
}
