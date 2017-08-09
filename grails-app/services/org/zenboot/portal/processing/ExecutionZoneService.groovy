package org.zenboot.portal.processing

import grails.plugin.springsecurity.SpringSecurityUtils
import org.zenboot.portal.security.Role
import org.zenboot.portal.PathResolver
import org.zenboot.portal.ZenbootException
import org.zenboot.portal.processing.flow.ScriptletBatchFlow
import org.zenboot.portal.processing.meta.ParameterMetadataList
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.ho.yaml.Yaml
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.zenboot.portal.ControllerUtils
import org.zenboot.portal.processing.meta.ParameterMetadata

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class ExecutionZoneService implements ApplicationEventPublisherAware {
    def runTimeAttributesService
    def grailsApplication
    def scriptletBatchService
    def applicationEventPublisher
    def springSecurityService
    def hostService
    def accessService
    def scriptDirectoryService

    private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy")

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

        File scriptDir = scriptDirectoryService.getZenbootScriptsDir()
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

    List filterByAccessPermission(executionZones) {
        List execZones = new ArrayList<ExecutionZone>()
        Map execZonesCacheMap = accessService.accessCache[springSecurityService.getCurrentUserId()]

        // if an entry in the cache for the user exists check cache, else update cache
        if(execZonesCacheMap) {
            executionZones.each {
                if (execZonesCacheMap[it.id]?.value) {
                    execZones.add(it)
                }
            }
        }
        else {
            executionZones.each { zone ->
                if (accessService.userHasAccess(zone)) {
                    execZones.add(zone)
                }
            }
        }
        return execZones
    }

    def getRange(filteredExecutionZoneInstanceList, params) {
        if (filteredExecutionZoneInstanceList.size() == 0) {
            return filteredExecutionZoneInstanceList
        }

        int offset = 0
        int max = 10
        if (params) {
            offset = params.int("offset") ?: 0
            max = params.int("max") ?: 10
        }
        log.debug("params sent max " + max + " and offset " + offset)
        log.debug("filteredExecutionZoneInstanceList.size() " + filteredExecutionZoneInstanceList.size())
        int upperBoundary = Math.min(max + offset, filteredExecutionZoneInstanceList.size()) - 1
        log.debug("returning filteredExecutionZoneInstanceList[" + offset + "," + upperBoundary + "]")
        if (offset > upperBoundary) return []
        log.debug("which is size:" + filteredExecutionZoneInstanceList[offset..upperBoundary].size())
        return filteredExecutionZoneInstanceList[offset..upperBoundary]
    }

    /** convenience-method for creating ExecutionZones
      * meant to be called in scripts
      */
    ExecutionZone createExecutionZone(HashMap params) {
      ExecutionZone executionZoneInstance = new ExecutionZone(params)
      if (!executionZoneInstance.save(flush: true)) {
          throw new ZenbootException("could not save ExecutionZone")
      }
      return executionZoneInstance
    }

    /** convenience-method if you only have a stackname instead of a File (directory)
     */
    void createAndPublishExecutionZoneAction(ExecutionZone execZone, String stackName, Map processParameters=null, List runtimeAttributes=null) {
        File stackDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
            + "/" + execZone.type.name + "/scripts/" + stackName)
      createAndPublishExecutionZoneAction(execZone, stackDir, processParameters, runtimeAttributes)
    }

    /** the main-one with a File-parameter
     */
    void createAndPublishExecutionZoneAction(ExecutionZone execZone, File scriptDir, Map processParameters=null, List runtimeAttributes=null) {

      if (processParameters == null) {
        processParameters = ParameterMetadataList.convertToMap(getExecutionZoneParameters(execZone, scriptDir))
      } else {
        def origProcessParameters = ParameterMetadataList.convertToMap(getExecutionZoneParameters(execZone, scriptDir))
        processParameters =  origProcessParameters << processParameters
      }
      ExecutionZoneAction action = createExecutionZoneAction(execZone, scriptDir, processParameters, runtimeAttributes)
      this.applicationEventPublisher.publishEvent(new ProcessingEvent(action, springSecurityService.currentUser))
    }

    /* The exposed-Version
     */
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


        ArrayList<ProcessingParameter> typedProcessParametersArrayList = new ArrayList<ProcessingParameter>()
        processParameters.each { key, value ->
          typedProcessParametersArrayList << new ProcessingParameter(name:key, value:value, comment:"Parameters added automatically by an execution zone action.")
        }
        return this.createExecutionZoneAction(execZone, scriptDir, typedProcessParametersArrayList, runtimeAttributes)
    }

    ExecutionZoneAction createExecutionZoneAction(ExecutionZone execZone,
          File scriptDir,
          ArrayList<ProcessingParameter> processParameters,
          List runtimeAttributes=null) {

        ExecutionZoneAction execAction = new ExecutionZoneAction(executionZone:execZone, scriptDir:scriptDir)


        processParameters.each {
            if(it?.value?.trim()) {
                execAction.addProcessingParameter(it)
            }
        }

        if (runtimeAttributes) {
            execAction.runtimeAttributes.addAll(runTimeAttributesService.normalizeRuntimeAttributes(runtimeAttributes))
        } else {
            execAction.runtimeAttributes.addAll(runTimeAttributesService.getRuntimeAttributes())
        }

        if (!execAction.validate()) {
            throw new ProcessingException("Not able to create action for ${execZone}: ${execAction.errors}")
        }

        execAction.save(flush:true)
        return execAction
    }
    Set getExposedExecutionZoneActionParameters(ExposedExecutionZoneAction exposedAction) {
        ScriptletBatchFlow flow = scriptletBatchService.getScriptletBatchFlow(exposedAction.scriptDir, exposedAction.executionZone.type)
        ParameterMetadataList paramMetaList = flow.parameterMetadataList
        Set parameters = overlayExecutionZoneParameters(paramMetaList, exposedAction.processingParameters)
        return parameters
    }

    /* central entrypoint to get parameters and overlay them by the ones from
     * the execZone. Called by AjaxCalls
     *
     * TODO the name is misleading, this gets all parameters of a scriptletbatch, not only the ones from the execution zone
     */
    Set getExecutionZoneParameters(ExecutionZone execZone, File scriptDir) {

        ScriptletBatchFlow flow = scriptletBatchService.getScriptletBatchFlow(scriptDir, execZone.type)
        ParameterMetadataList paramMetaList = flow.parameterMetadataList
        Set parameters = overlayExecutionZoneParameters(paramMetaList, execZone.processingParameters)
        return parameters
    }

    boolean actionParameterEditAllowed(parameter, originalParameter) {
        originalParameter?.value == null ||
                originalParameter?.value == "" ||
                // multiline-support therefore replace newlines before comparison
                originalParameter?.value?.replaceAll("[\\\t|\\\n|\\\r]","") == parameter?.value ||
                canEdit(springSecurityService.currentUser.getAuthorities(), parameter)
    }

    boolean setParameters(AbstractExecutionZoneCommand command, Map parameters) {
        ExecutionZone executionZone = ExecutionZone.get(command.execId)

        command.execZoneParameters = ControllerUtils.getParameterMap(parameters ?: [:], "key", "value")

        def originalParameters = getExecutionZoneParameters(executionZone, command.scriptDir)

        if (command.containsInvisibleParameters) {

            originalParameters.each { ParameterMetadata originalParameter ->
                if (!originalParameter.visible && !command.execZoneParameters[originalParameter.name]) {
                    command.execZoneParameters[originalParameter.name] = originalParameter.value
                }
            }
        }

        command.execZoneParameters.each { key, value ->
            if (!value) {
                command.errors.reject(
                        'executionZone.parameters.emptyValue',
                        [key] as Object[],
                        'Mandatory parameter is empty'
                )
            }
        }

        rejectIllegalParameterEdits(originalParameters, command, executionZone)

        return command.errors.hasErrors()
    }

    def rejectIllegalParameterEdits(Set originalParameters, command, executionZone) {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            return
        }

        originalParameters.each { ParameterMetadata originalParameterMetaData ->
            def param = command.execZoneParameters[originalParameterMetaData.name];
            def originalParameter = executionZone.getProcessingParameter(originalParameterMetaData.name) ?: originalParameterMetaData
            def processParam = new ProcessingParameter(name: originalParameter.name, value: param.value.toString())
            if (!actionParameterEditAllowed(processParam, originalParameter)) {
                command.errors.reject('executionZone.failure.unallowedEdit',
                        [originalParameter.name] as Object[],
                        'You are not allowed to edit parameter {0}'
                )
            }
        }
    }

    private Set overlayExecutionZoneParameters(ParameterMetadataList paramMetaList, Set overlayParameters) {
        log.debug("Entering overlayExecutionZoneParameters")
		Set parameters = paramMetaList.unsatisfiedParameters
        log.debug("yet unsatisfied parameters: " + parameters)

        parameters.each { ParameterMetadata paramMetaData ->
          ProcessingParameter param = overlayParameters.find { it.name == paramMetaData.name }
            if (param) {
                  log.debug("filling param "+ paramMetaData.name + " with "+ param.value)
                  paramMetaData.metaClass.value = param.value
                  paramMetaData.metaClass.overlay = true
          } else {
                  log.debug("defaulting param "+ paramMetaData.name + " with " + paramMetaData.defaultValue)
                  paramMetaData.metaClass.value = paramMetaData.defaultValue
                  paramMetaData.metaClass.overlay = false
  			  }
		    }
        // Now the other way around. We also want all the execZoneParams which
        // are not defined to be used in here. They are usefull, although
        // the usage might be more of a black magic than the ones who
        // are statically defined
        overlayParameters.each { ProcessingParameter param ->
          if (!parameters*.name.contains(param.name)) {
            ParameterMetadata newParamMataData = new ParameterMetadata(
              script:null,
              description: param.description,
              name:param.name,
              type: ParameterType.CONSUME,
              visible: true)
            newParamMataData.metaClass.value = param.value
            // We're not really overlaying because no script stated that it uses
            // the value but this is better than false later in the UI
            newParamMataData.metaClass.overlay = true
            parameters << newParamMataData
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

    boolean canEdit(Role role, ProcessingParameter parameter) {
      def expression = role.parameterEditExpression

      try {
          def sharedData = new Binding()
          sharedData.setProperty('parameterKey', parameter.name)
          sharedData.setProperty('parameter', parameter)
          return engine.eval(expression == null ? "" : expression, sharedData)
      } catch (Exception e) {
        this.log.error("parameterEditExpression for role '"+ role + " with " + expression +"' is throwing an exception", e)
        return false
      }
    }

    boolean canEdit(Set roles, ProcessingParameter parameter) {
      for ( role in roles) {
        if (canEdit(role,parameter)) {
          return true
        }
      }
      return false
    }

    List findByParameter(String key, String value) {
      return ExecutionZone.findAll().findAll() { it.param(key) == value }
    }

    /** see also HostService.getExpiryDate()
      */
    Date getExpiryDate(ExecutionZone execZone) {
      if (execZone.defaultLifetime) {
        int lifetime = execZone.defaultLifetime
        if (lifetime > 0) {
          GregorianCalendar calendar = GregorianCalendar.getInstance()
          calendar.add(GregorianCalendar.MINUTE, lifetime)
          return calendar.getTime()
        }
      }
      return hostService.getExpiryDate()
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.applicationEventPublisher = eventPublisher
    }
}
