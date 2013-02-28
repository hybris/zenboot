package org.zenboot.portal

import org.zenboot.portal.processing.ProcessingParameter

class ControllerUtils {

    static Set getProcessingParameters(def params, String paramName="parameters.key", String paramValue="parameters.value", String paramExposed="parameters.exposed", String paramPublished="parameters.published") {
        Set procParameters = []
        def keys = params[paramName]
        def values = params[paramValue]
        def exposed = params[paramExposed]
        def published = params[paramPublished]
        if (keys && values && exposed && published) {
            if (keys.class.isArray() && values.class.isArray() && exposed.class.isArray() && published.class.isArray()) {
                if (keys.length == values.length && keys.length == exposed.length  && keys.length == published.length) {
                    keys.eachWithIndex { key, index ->
                        procParameters << new ProcessingParameter(name:keys[index], value:values[index], exposed:exposed[index], published:published[index])
                    }
                } else {
                    throw new IllegalArgumentException("Could not convert params to ${ProcessingParameter.class.simpleName} because arrays have different length")
                }
            } else {
                procParameters << new ProcessingParameter(name:keys, value:values, exposed:Boolean.valueOf(exposed), published:Boolean.valueOf(published))
            }
        }
        return procParameters
    }

    static Map getParameterMap(def params, String paramKey="parameters.key", String paramValue="parameters.value") {
        def parameters = [:]
        def keys = params[paramKey]
        def values = params[paramValue]
        if (keys && values) {
            if (keys.class.isArray()) {
                if (!values?.class.isArray() || keys.length != values.length) {
                    throw new IllegalArgumentException("Could not map parameters to key/values: keys=${keys} / values=${values}")
                }
                keys.eachWithIndex { key, index ->
                    parameters[key] = values[index]
                }
            } else {
                parameters[keys] = values
            }
        }
        return parameters
    }

    static void synchronizeProcessingParameters(Set procParams, def model) {
        def deletedExecZoneParams = model.processingParameters.findAll { ProcessingParameter param ->
            !procParams*.name.contains(param.name)
        }
        model.processingParameters.removeAll(deletedExecZoneParams)
        procParams.each { ProcessingParameter procParam ->
            model.addProcessingParameter(procParam)
        }
    }

    static void synchronizeProcessingParameterValues(Map keyValues, def model) {
        def deletedExecZoneParams = model.processingParameters.findAll { ProcessingParameter param ->
            !keyValues.containsKey(param.name)
        }
        model.processingParameters.removeAll(deletedExecZoneParams)
        keyValues.each { key, value ->
            ProcessingParameter procParam = model.getProcessingParameter(key)
            if (procParam) {
                procParam.value = value
                procParam.save()
            } else {
                model.addProcessingParameter(new ProcessingParameter(name:key, value:value))
            }
        }
    }
}