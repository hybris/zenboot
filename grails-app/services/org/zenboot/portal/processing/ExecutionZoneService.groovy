package org.zenboot.portal.processing

import org.zenboot.portal.ControllerUtils
import org.zenboot.portal.PathResolver
import org.zenboot.portal.processing.flow.ScriptletBatchFlow
import org.zenboot.portal.processing.meta.ParameterMetadata
import org.zenboot.portal.processing.meta.ParameterMetadataList
import org.ho.yaml.Yaml

class ExecutionZoneService {




    static final String SCRIPTS_DIR = 'scripts'
    static final String JOBS_DIR = 'jobs'
    static final String PLUGINS_DIR = 'plugins'

    def grailsApplication
    def scriptletBatchService

    void synchronizeExecutionZoneTypes() {
        //type name is the key to be able to resolve a type by name quickly
        Map execZoneTypes = ExecutionZoneType.findAll().inject([:]) { Map map, ExecutionZoneType execZoneType ->
            map << [(execZoneType.name):execZoneType]
        }

        //track the enabled execution zone types in a separate list to be able to find non-existing types later
        Set enabledTypes = this.getEnabledExecutionZoneTypes(execZoneTypes)

        //disable all types which no longer exist by comparing enabled types with previously defined types
        this.disableExcutionZoneTypes(execZoneTypes, enabledTypes)
    }

    private Set getEnabledExecutionZoneTypes(Map execZoneTypes) {
        Set enabledTypes = []

        File scriptDir = this.getZenbootScriptsDir()
        scriptDir.eachDir { File directory ->

            if (execZoneTypes.containsKey(directory.name)) {
                //type already exists, make sure that the type is enabled
                if (!execZoneTypes[directory.name].enabled) {
                    execZoneTypes[directory.name].enabled = true
                    execZoneTypes[directory.name].save()
                }
            } else {
                //new type found
                ExecutionZoneType execZoneType = new ExecutionZoneType(name:directory.name)
                if (!execZoneType.validate()) {
                    throw new ExecutionZoneException("Could not create ${ExecutionZoneType.class.simpleName}: ${execZoneType.errors}")
                }
                execZoneType.save(flush:true)
                execZoneTypes[directory.name] = execZoneType
            }

            enabledTypes << execZoneTypes[directory.name]
        }

        return enabledTypes
    }

	private void disableExcutionZoneTypes(Map execZoneTypes, Set enabledTypes) {
		def disabledTypes = execZoneTypes.values()
		disabledTypes.removeAll(enabledTypes)
		disabledTypes.each { ExecutionZoneType execZoneType ->
			if (execZoneType.enabled) {
				execZoneType.enabled = false
				execZoneType.save()
			}
		}
	}

    List getRuntimeAttributes() {
        return this.normalizeRuntimeAttributes(grailsApplication.config.zenboot.processing.attributes.toString().split(",").asType(List))
    }

    private List normalizeRuntimeAttributes(List attributes) {
        return attributes*.trim()*.toLowerCase()
    }

    ExecutionZoneAction createExecutionZoneAction(ExposedExecutionZoneAction exposedAction, Map processParameters=null, List runtimeAttributes=null) {
        Map mergedParameters = exposedAction.processingParameters.inject([:]) { Map map, ProcessingParameter param ->
            map[param.name] = param.value
            return map
        }
        if (processParameters) {
            mergedParameters.putAll(processParameters)
        }
        return this.createExecutionZoneAction(exposedAction.executionZone, exposedAction.scriptDir, mergedParameters, runtimeAttributes)
    }

    ExecutionZoneAction createExecutionZoneAction(ExecutionZone execZone, File scriptDir, Map processParameters=null, List runtimeAttributes=null) {
        ExecutionZoneAction execAction = new ExecutionZoneAction(executionZone:execZone, scriptDir:scriptDir)

        if (processParameters) {
            execZone.processingParameters.each { it ->
                execZone.processingParameters << new ProcessingParameter(name:it.name, value:it.value, comment:"Parameters added automatically by an execution zone action.")
            }

            ControllerUtils.synchronizeProcessingParameterValues(processParameters, execAction)
        }

        if (runtimeAttributes) {
            execAction.runtimeAttributes.addAll(this.normalizeRuntimeAttributes(runtimeAttributes))
        } else {
            execAction.runtimeAttributes.addAll(this.getRuntimeAttributes())
        }

        if (!execAction.validate()) {
            throw new ProcessingException("Not able to create action for ${execZone}: ${execAction.errors}")
        }

        execAction.save(flush:true)
        return execAction
    }

    File getZenbootScriptsDir() {
        File scriptDir = new File(PathResolver.getAbosolutePath(grailsApplication.config.zenboot.processing.scriptDir))
        if (!scriptDir.exists() || !scriptDir.isDirectory()) {
            throw new ExecutionZoneException("Could not find script directory ${scriptDir}")
        }
        return scriptDir
    }

    File getPluginDir(ExecutionZoneType type) {
        return this.getDir(type, PLUGINS_DIR)
    }

    File getJobDir(ExecutionZoneType type) {
        return this.getDir(type, JOBS_DIR)
    }

    File getScriptDir(ExecutionZoneType type) {
        return this.getDir(type, SCRIPTS_DIR)
    }

    List getScriptDirs(ExecutionZoneType type) {
        List scriptDirs = []
        File scriptDir = this.getScriptDir(type)
        if (scriptDir.exists()) {
            scriptDir.eachDir {
                scriptDirs << it
            }
        }
        return scriptDirs.sort()
    }

    /**
     * returns a List of Directories
     */
    List getScriptDirs(ExecutionZoneType type, String filter) {
      List scriptDirs = []
      File scriptDir = this.getScriptDir(ExecutionZoneType.findAll()[2])
      if (scriptDir.exists()) {
        scriptDir.eachDir {
          File metaFile = new File(it, ".meta.yaml")
          if (metaFile.exists()) {
            def yaml = Yaml.load(metaFile)
            if (yaml['ui-script-types'].contains(filter)) {
              scriptDirs << it
            }
          } else {
            if (filter.equals("misc")) {
              scriptDirs << it
            }
          }
        }

      }
      return scriptDirs.sort()
    }

    private File getDir(ExecutionZoneType type, String subDir) {
        String path = "${this.getZenbootScriptsDir()}${System.properties['file.separator']}${type.name}"
        if (!subDir.isEmpty()) {
            path = "${path}${System.properties['file.separator']}${subDir}"
        }
        return new File(path)
    }

    def getScriptletBatchFlow(File scriptDir) {
        return scriptletBatchService.getScriptletBatchFlow(scriptDir, this.getRuntimeAttributes())
    }

    Set getExposedExecutionZoneActionParameters(ExposedExecutionZoneAction exposedAction) {
        ScriptletBatchFlow flow = scriptletBatchService.getScriptletBatchFlow(exposedAction.scriptDir, this.getRuntimeAttributes())
        ParameterMetadataList paramMetaList = flow.parameterMetadataList
        Set parameters = overlayExecutionZoneParameters(paramMetaList, exposedAction.processingParameters)
        return parameters
    }

    Set getExecutionZoneParameters(ExecutionZone execZone, File scriptDir) {
        ScriptletBatchFlow flow = scriptletBatchService.getScriptletBatchFlow(scriptDir, this.getRuntimeAttributes())
        ParameterMetadataList paramMetaList = flow.parameterMetadataList
        Set parameters = overlayExecutionZoneParameters(paramMetaList, execZone.processingParameters)
        return parameters
    }

    private Set overlayExecutionZoneParameters(ParameterMetadataList paramMetaList, Set overlayParameters) {
		Set parameters = paramMetaList.unsatisfiedParameters
		parameters.each { ParameterMetadata paramMetaData ->
            ProcessingParameter param = overlayParameters.find { it.name == paramMetaData.name }
			if (param) {
				paramMetaData.metaClass.value = param.value
				paramMetaData.metaClass.overlay = true
			} else {
				paramMetaData.metaClass.value = paramMetaData.defaultValue
				paramMetaData.metaClass.overlay = false
			}
		}
		return parameters
	}

    def resolveExposedExecutionZoneActionParameters(ExposedExecutionZoneAction exposedAction, Map parameters) {
        def result = new Expando()
        result.missingParameters = []
        result.resolvedParameters = [:]

        def exposedActionParamMetas = this.getExposedExecutionZoneActionParameters(exposedAction)

        exposedActionParamMetas.each { ParameterMetadata paramMeta ->
            //user is only allowed to set not defined exposed action parameters
            if (paramMeta.value) {
                result.resolvedParameters[paramMeta.name] = paramMeta.value
                return
            }
            if (parameters[paramMeta.name]) {
                result.resolvedParameters[paramMeta.name] = parameters[paramMeta.name]
                return
            }
            result.missingParameters << paramMeta.name
        }

        return result
    }
}
