package org.zenboot.portal.processing.meta

import org.zenboot.portal.processing.meta.annotation.ParameterType


class ParameterMetadataList {

	private Set satisfiedParameters = []
	private Set unsatisfiedParameters = []

	Set getSatisfiedParameters() {
		return this.satisfiedParameters
	}

	Set getUnsatisfiedParameters() {
		return this.unsatisfiedParameters
	}

	Set getParameters() {
		Set parameters = []
		parameters.addAll(this.satisfiedParameters)
		parameters.addAll(this.unsatisfiedParameters)
		return parameters
	}

	void addParameters(Collection parameters) {
		//verify consumed parameters first
		def inputParameters = parameters.findAll { ParameterMetadata parameter ->
			parameter.type == ParameterType.CONSUME
		}
		inputParameters.each { ParameterMetadata parameter ->
			if (!satisfiedParameters*.name.contains(parameter.name)) {
				this.unsatisfiedParameters << parameter
			}
		}
		//add exposed parameters
		parameters.removeAll(inputParameters)
		parameters.each { ParameterMetadata parameter ->
			this.satisfiedParameters << parameter
		}
	}

}
