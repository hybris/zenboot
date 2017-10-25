package org.zenboot.portal.processing

import org.springframework.context.ApplicationListener
import org.zenboot.portal.processing.converter.ParameterConverter
import org.zenboot.portal.processing.converter.ParameterConverterMap
import org.zenboot.portal.processing.flow.ScriptletBatchFlow
import org.zenboot.portal.processing.meta.ParameterMetadata
import org.zenboot.portal.processing.meta.ParameterMetadataList
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.zenboot.portal.security.Person
import org.zenboot.portal.processing.ProcessingException

class ScriptletBatchService implements ApplicationListener<ProcessingEvent> {

    static transactional = false //necessary to avoid duplicate event listener registration

    def runTimeAttributesService
    def grailsApplication
    def executionService
    def springSecurityService
    def accessService
    def scriptDirectoryService

    def scriptletFlowCache

    def clearCache() {
      scriptletFlowCache = null
    }

    def getRange(scriptletBatches, params) {
        if (scriptletBatches.empty) {
            return scriptletBatches
        }

        int offset = 0
        int max = 10
        if (params) {
            offset = params.int("offset") ?: 0
            max = params.int("max") ?: 10
        }
        log.debug("params sent max "+max+" and offset "+offset)
        log.debug("filteredScriptletBatchList.size() "+scriptletBatches.size())
        int upperBoundary = Math.min(max+offset, scriptletBatches.size()) -1
        log.debug("returning filteredScriptletBatchList["+offset+","+upperBoundary+"]")
        log.debug("which is size:"+scriptletBatches[offset..upperBoundary].size())
        return scriptletBatches[offset..upperBoundary]
    }

    @Override
    public void onApplicationEvent(ProcessingEvent event) {
        this.log.info("Receive application event ${event}")

        // ToDo Refactor, so that not the processingEvent is a param to the closure, but
        // user, executionZoneAction and comment
        // This is basically
        // * creating and populating the ProcessContext
        // * creating and populating the Scriptletbatch
        // * run the execute-method with the processContext
        // * synchronizeExposedProcessingParameters ?????
        Closure execute = { ProcessingEvent processingEvent ->
            ProcessContext processContext = new ProcessContext(
            parameters:new ParameterConverterMap(parameterConverters:grailsApplication.mainContext.getBeansOfType(ParameterConverter).values()),
            user:processingEvent.user
            )
            ExecutionZoneAction action = processingEvent.executionZoneAction.merge()
            processContext.parameters.putAll(action.processingParameters.inject([:]) { map, param ->
                map[param.name] = param.value
                return map
            })
            processContext.execZone=action.executionZone
            processContext.parameters.put("EXECUTIONZONE_TYPE",action.executionZone.type.name)
            ScriptletBatch batch = this.buildScriptletBatch(action, processingEvent.user, processingEvent.comment)
            processContext.parameters.put("SCRIPTDIR",batch.executionZoneAction.scriptDir)
            processContext.scriptletBatch=batch
            try {
              batch.execute(processContext)
            } catch (ProcessingException e) {
              batch.exceptionMessage = e.getMessage()
              batch.exceptionClass = e.getClass().toString()
              batch.cancel()
            } catch (Exception e) {
              //log.error("Caught Exception: ",e)
              batch.exceptionMessage = e.getMessage()
              batch.exceptionClass = e.getClass().toString()
              batch.cancel()
            }

            this.synchronizeExposedProcessingParameters(batch, processContext)
        }

        if (event.processAsync && grailsApplication.config.zenboot.processing.asynchron.toBoolean()) {
            // This leverages the grails executor-plugin
            // https://github.com/basejump/grails-executor#examples
            runAsync {
              try {
                execute(event)
              } catch (Exception e) {
                log.error("Caught Exception: ",e)
              }
            }
        } else {
            execute(event)
        }
    }

	private void synchronizeExposedProcessingParameters(ScriptletBatch batch, ProcessContext processContext) {
        ExecutionZoneAction action = batch.executionZoneAction
        ScriptletBatchFlow flow = getScriptletBatchFlow(action.scriptDir, batch.executionZoneAction.executionZone.type)
		ParameterMetadataList paramList = flow.getParameterMetadataList()

		def exposedPublishedMetaParams = paramList.parameters.findAll { ParameterMetadata paramMeta ->
			[ParameterType.EXPOSE, ParameterType.PUBLISH].contains(paramMeta.type)
		}

		exposedPublishedMetaParams.each { ParameterMetadata paramMeta ->
			if (!processContext.parameters.containsKey(paramMeta.name)) {
				log.warn("Can not update parameter '${paramMeta.name}' in scriptlet batch (${batch.id}) because key not found in process context")
				return
			}

			ProcessingParameter procParam = action.executionZone.getProcessingParameter(paramMeta.name)
			if (procParam) {
				if (procParam.value != processContext.parameters[paramMeta.name]) {
					if (!action.executionZone.enableExposedProcessingParameters) {
						this.logSyncProcessingParameterWarning(batch, action.executionZone, procParam, processContext.parameters[paramMeta.name])
						return
					}
					if (!procParam.exposed) {
                        this.logSyncProcessingParameterWarning(batch, procParam, procParam, processContext.parameters[paramMeta.name])
						return
					}
				}
                procParam.value = processContext.parameters[paramMeta.name]
                procParam.save()
			} else {
    			action.executionZone.addProcessingParameter(new ProcessingParameter(
					name: paramMeta.name,
					value: processContext.parameters[paramMeta.name],
					exposed: [ParameterType.EXPOSE, ParameterType.PUBLISH ].contains(paramMeta.type), //PUBLISH is basically an extend version of EXPOSE
					published: (ParameterType.PUBLISH == paramMeta.type)
					)
				)
    			action.executionZone.save()
			}
		}
	}

    private void logSyncProcessingParameterWarning(ScriptletBatch batch, def model, ProcessingParameter procParam, String newValue) {
        log.warn("Can not update parameter '${procParam.name}' in scriptlet batch ${batch.id} because ${model.class.simpleName}" +
            " (${model.id}) denies parameter updates [Stored:${procParam.value} != New:${newValue}]")
    }

    /**
      * Method is synchronized because we got exceptions like this:
      * org.springframework.orm.hibernate3.HibernateSystemException: Don't change the reference to a collection with
      * cascade="all-delete-orphan": org.zenboot.portal.processing.AbstractExecutionZoneAction.processingParameters;
      * nested exception is org.hibernate.HibernateException: Don't change the reference to a collection with cascade="all-delete-orphan":
      * org.zenboot.portal.processing.AbstractExecutionZoneAction.processingParameters
	    * at org.zenboot.portal.processing.ScriptletBatchService.buildScriptletBatch(ScriptletBatchService.groovy:125) (135)
      *
      */
    synchronized private ScriptletBatch buildScriptletBatch(ExecutionZoneAction action, Person user, String comment) {
        if (this.log.debugEnabled) {
            this.log.debug("Build scriptlet batch for action ${action}")
        }
        def username = user?.displayName ?: user?.username ?: 'cron'

        ScriptletBatch batch = new ScriptletBatch(description: "${username} : ${action.executionZone.type} : ${action.scriptDir.name} ${action.executionZone.description? action.executionZone.description : "" }", executionZoneAction:action, user:user, comment:comment)

        PluginResolver pluginResolver = new PluginResolver(scriptDirectoryService.getPluginDir(action.executionZone.type))
        File pluginFile = pluginResolver.resolveScriptletBatchPlugin(batch, action.runtimeAttributes)
        if (pluginFile) {
            executionService.injectPlugins(pluginFile, batch)
        }
        if (batch.hasErrors()) {
            throw new ProcessingException("Failure while building ${batch}: ${batch.errors}")
        }
        action.scriptletBatches << batch
        this.addScriptlets(batch, action.runtimeAttributes)
        action.save(flush:true, failOnError: true)
        return batch
    }

    private List<Scriptlet> addScriptlets(ScriptletBatch batch, List runtimeAttributes) {
        ScriptResolver scriptsResolver

        try {
            scriptsResolver = new ScriptResolver(batch.executionZoneAction.scriptDir)
        }
        catch (ProcessingException e) {
            batch.state = Processable.ProcessState.FAILURE
            batch.exceptionMessage = e.getMessage()
            batch.exceptionStacktrace = e.getStackTrace()
            batch.save(flush: true)
            throw e
        }

        PluginResolver pluginResolver = new PluginResolver(scriptDirectoryService.getPluginDir(batch.executionZoneAction.executionZone.type))

        scriptsResolver.resolve(runtimeAttributes).each { File file ->
            Scriptlet scriptlet = new Scriptlet(description:file.name, file:file)

            file.setExecutable(true)
            scriptlet.process = executionService.createProcessClosure(file, scriptlet)

            File pluginFile = pluginResolver.resolveScriptletPlugin(scriptlet, batch.executionZoneAction.runtimeAttributes)
            if (pluginFile) {
                executionService.injectPlugins(pluginFile, scriptlet)
            }
            batch.processables << scriptlet
            scriptlet.scriptletBatch = batch
        }
    }


    ScriptletBatchFlow getScriptletBatchFlow(File scriptDir, ExecutionZoneType type) {
        return this.getScriptletBatchFlow(scriptDir, runTimeAttributesService.getRuntimeAttributes(), type)
    }

    ScriptletBatchFlow getScriptletBatchFlow(File scriptDir, List runtimeAttributes, ExecutionZoneType type) {
        log.debug("cache is:" + scriptletFlowCache)
        log.debug("runtimeAttributes are: " + runtimeAttributes)

        if (scriptletFlowCache == null) {
          scriptletFlowCache = [:]
        }

        if (scriptletFlowCache[scriptDir.toString()+runtimeAttributes.toString()] == null || type.devMode ) {
          ScriptResolver scriptResolver = new ScriptResolver(scriptDir)
          String pathPluginDir = "${scriptDir.parent}${System.properties['file.separator']}..${System.properties['file.separator']}${ScriptDirectoryService.PLUGINS_DIR}"
          PluginResolver pluginResolver = new PluginResolver(new File(pathPluginDir))

          ScriptletBatchFlow flow = new ScriptletBatchFlow()
          flow.batchPlugin = pluginResolver.resolveScriptletBatchPlugin(scriptDir, runtimeAttributes)

          def scriptFiles = scriptResolver.resolve(runtimeAttributes)
          scriptFiles.each { File script ->
              File plugin = pluginResolver.resolveScriptletPlugin(script, runtimeAttributes)
              flow.addFlowElement(script, plugin)
          }
          scriptletFlowCache[scriptDir.toString()+runtimeAttributes.toString()] = flow.build()
          return scriptletFlowCache[scriptDir.toString()+runtimeAttributes.toString()]
        } else {
          return scriptletFlowCache[scriptDir.toString()+runtimeAttributes.toString()]
        }

    }
}
