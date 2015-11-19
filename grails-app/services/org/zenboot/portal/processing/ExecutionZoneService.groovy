package org.zenboot.portal.processing

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.zenboot.portal.security.Role
import org.zenboot.portal.ControllerUtils
import org.zenboot.portal.PathResolver
import org.zenboot.portal.ZenbootException
import org.zenboot.portal.processing.ProcessingParameter
import org.zenboot.portal.processing.flow.ScriptletBatchFlow
import org.zenboot.portal.processing.meta.ParameterMetadata
import org.zenboot.portal.processing.meta.ParameterMetadataList
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.ho.yaml.Yaml
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware

class ExecutionZoneService implements ApplicationEventPublisherAware {


    static final String SCRIPTS_DIR = 'scripts'
    static final String JOBS_DIR = 'jobs'
    static final String PLUGINS_DIR = 'plugins'

    def grailsApplication
    def scriptletBatchService
    def applicationEventPublisher
    def springSecurityService
    def hostService

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

    def findAllByEnabledFiltered(enabled, params) {
      def executionZoneInstanceList = ExecutionZone.findAllByEnabled(enabled)
      def filteredExecutionZoneInstanceList = []
      filteredExecutionZoneInstanceList.addAll(executionZoneInstanceList.findAll() { executionZone ->
        this.hasAccess(springSecurityService.currentUser.getAuthorities(), executionZone)
      })
      int offset = params ? (params.int("offset") ? params.int("offset") : 0) : 0
      int max = params ? (params.int("max") ? params.int("max") : -1) : -1
      log.debug("params sent max "+max+" and offset "+offset)
      log.debug("filteredExecutionZoneInstanceList.size() "+filteredExecutionZoneInstanceList.size())
      int upperBoundary = Math.min(max+offset, filteredExecutionZoneInstanceList.size()) -1
      log.debug("returning filteredExecutionZoneInstanceList["+offset+","+upperBoundary+"]")
      log.debug("which is size:"+filteredExecutionZoneInstanceList[offset..upperBoundary].size())
      return filteredExecutionZoneInstanceList[offset..upperBoundary]

    }

    int countByEnabledFiltered(enabled) {
      def executionZoneInstanceList = ExecutionZone.findAllByEnabled(enabled)
      def filteredExecutionZoneInstanceList = []
      filteredExecutionZoneInstanceList.addAll(executionZoneInstanceList.findAll() { executionZone ->
        this.hasAccess(springSecurityService.currentUser.getAuthorities(), executionZone)
      })
      return filteredExecutionZoneInstanceList.size()
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
      File stackDir = new File(getZenbootScriptsDir().getAbsolutePath() + "/"+ execZone.type.name + "/scripts/"+stackName)
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
          execAction.addProcessingParameter(it)
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
      File scriptDir = this.getScriptDir(type)
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

    def getScriptletBatchFlow(File scriptDir, ExecutionZoneType type) {
        return scriptletBatchService.getScriptletBatchFlow(scriptDir, this.getRuntimeAttributes(), type)
    }

    Set getExposedExecutionZoneActionParameters(ExposedExecutionZoneAction exposedAction) {
        ScriptletBatchFlow flow = scriptletBatchService.getScriptletBatchFlow(exposedAction.scriptDir, this.getRuntimeAttributes(), exposedAction.executionZone.type)
        ParameterMetadataList paramMetaList = flow.parameterMetadataList
        Set parameters = overlayExecutionZoneParameters(paramMetaList, exposedAction.processingParameters)
        return parameters
    }

    /* central entrypoint to get parameters and overlay them by the ones from
     * the execZone. Called by AjaxCalls
     */
    Set getExecutionZoneParameters(ExecutionZone execZone, File scriptDir) {
        ScriptletBatchFlow flow = scriptletBatchService.getScriptletBatchFlow(scriptDir, this.getRuntimeAttributes(), execZone.type)
        ParameterMetadataList paramMetaList = flow.parameterMetadataList
        Set parameters = overlayExecutionZoneParameters(paramMetaList, execZone.processingParameters)
        return parameters
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

    boolean hasAccess(Role role, ExecutionZone executionZone) {
      def expression = role.executionZoneAccessExpression
      try {
        return Eval.me("executionZone",executionZone,expression == null ? "" : expression)
      } catch (Exception e) {
        this.log.error("executionZoneAccessExpression for role '"+ role + " with " + expression +"' is throwing an exception", e)
        return false
      }
    }

    boolean hasAccess(Set roles, ExecutionZone zone) {
      for ( role in roles) {
        if (hasAccess(role,zone)) {
          return true
        }
      }
      return false
    }

    boolean userHasAccess(ExecutionZone zone) {
        SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) ||
                hasAccess(springSecurityService.currentUser.getAuthorities(), zone)
    }

    boolean canEdit(Role role, ProcessingParameter parameter) {
      def expression = role.parameterEditExpression

      try {

        def sharedData = new Binding()
        def shell = new GroovyShell(sharedData)

        sharedData.setProperty('parameterKey', parameter.name)
        sharedData.setProperty('parameter', parameter)

        return shell.evaluate(expression == null ? "" : expression)

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
