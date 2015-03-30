package org.zenboot.portal.processing.meta

import org.zenboot.portal.processing.meta.annotation.ParameterType


class ParameterMetadataList {

	private Set satisfiedParameters = []
	private Set unsatisfiedParameters = []

	// The ParameterMetadataList is somehow misused to not only store the
	// compile-time resolved list of parameters in the scripts, but also
	// the result of overlaying
	// https://github.com/hybris/zenboot/blob/64b3792ad57c11c06b1323cb2ff6eef0f012a51b/grails-app/services/org/zenboot/portal/processing/ExecutionZoneService.groovy#L227-L240
	// the parameters stored in the execZone with the ones coming from the
	// scripts. Therefore, we're returning cloned versions of the parameters
	// which enables proper caching
	
	Set getSatisfiedParameters() {
		Set parameters = []
		this.satisfiedParameters.each {
			parameters.add(it.clone())
		}
		return parameters
	}

	Set getUnsatisfiedParameters() {
		Set parameters = []
		this.unsatisfiedParameters.each {
			log.debug("looping: " + it)
			parameters.add(it.clone())
		}
		log.debug("returning unsatisfied Parameters: " + parameters)
		return parameters
	}

	Set getParameters() {
		Set parameters = []
		this.satisfiedParameters.each {
			parameters.add(it.clone())
		}
		this.unsatisfiedParameters.each {
			parameters.add(it.clone())
		}
		//parameters.addAll(this.satisfiedParameters)
		//parameters.addAll(this.unsatisfiedParameters)
		return parameters
	}

	void addParameters(Collection parameters) {
		//verify consumed parameters first
		def inputParameters = parameters.findAll { ParameterMetadata parameter ->
			parameter.type == ParameterType.CONSUME
		}
		inputParameters.each { ParameterMetadata parameter ->
			if (!satisfiedParameters*.name.contains(parameter.name)) {
				this.@unsatisfiedParameters << parameter
			}
		}
		//add exposed parameters
		parameters.removeAll(inputParameters)
		parameters.each { ParameterMetadata parameter ->
			this.@satisfiedParameters << parameter
		}
	}

}
