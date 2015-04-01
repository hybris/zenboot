package org.zenboot.portal.processing

import org.springframework.context.ApplicationListener
import org.zenboot.portal.ProcessHandler
import org.zenboot.portal.processing.converter.ParameterConverter
import org.zenboot.portal.processing.converter.ParameterConverterMap
import org.zenboot.portal.processing.flow.ScriptletBatchFlow
import org.zenboot.portal.processing.meta.ParameterMetadata
import org.zenboot.portal.processing.meta.ParameterMetadataList
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role

class ScriptletBatchService implements ApplicationListener<ProcessingEvent> {

    static transactional = false //necessary to avoid duplicate event listener registration

    def grailsApplication
    def executionZoneService
    def executionService
    def springSecurityService

    def ScriptletFlowCache


    def findAllByExecZoneFiltered(ExecutionZone execZone) {
      def scriptletBatchList = []
      if (execZone != null) {
         execZone.actions.each { action ->
           if (action.scriptletBatches[0] != null) {
             scriptletBatchList.add(action.scriptletBatches[0])
           }
         }
      } else {
        def executionZoneInstanceList = ExecutionZone.findAll()

        executionZoneInstanceList.each { executionZone ->
          if (executionZoneService.hasAccess(springSecurityService.currentUser.getAuthorities(), executionZone)) {
            executionZone.actions.each { action ->
              if (action.scriptletBatches[0] != null) {
                scriptletBatchList.add(action.scriptletBatches[0])
              }
            }
          }
        }

      }
      return scriptletBatchList
    }

    def findAllByExecZoneFiltered(ExecutionZone execZone, params) {
      def filteredScriptletBatchList = findAllByExecZoneFiltered(execZone)
      int offset = params ? (params.int("offset") ? params.int("offset") : 0) : 0
      int max = params ? (params.int("max") ? params.int("max") : -1) : -1
      log.debug("params sent max "+max+" and offset "+offset)
      log.debug("filteredScriptletBatchList.size() "+filteredScriptletBatchList.size())
      int upperBoundary = Math.min(max+offset, filteredScriptletBatchList.size()) -1
      log.debug("returning filteredScriptletBatchList["+offset+","+upperBoundary+"]")
      log.debug("which is size:"+filteredScriptletBatchList[offset..upperBoundary].size())
      return filteredScriptletBatchList[offset..upperBoundary]

    }

    int countByExecZoneFiltered(ExecutionZone execZone) {
      return findAllByExecZoneFiltered(execZone).size()
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
            ScriptletBatch batch = this.buildScriptletBatch(action, processingEvent.user, processingEvent.comment)
            processContext.scriptletBatch=batch
            try {
              batch.execute(processContext)
            } catch (Exception e) {
              log.error("Catched Exception: ",e)
              batch.exceptionMessage = e.getMessage()
              batch.exceptionClass = e.getClass()
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
                log.error("Catched Exception: ",e)
              }
            }
        } else {
            execute(event)
        }
    }

	private void synchronizeExposedProcessingParameters(ScriptletBatch batch, ProcessContext processContext) {
        ExecutionZoneAction action = batch.executionZoneAction
        ScriptletBatchFlow flow = executionZoneService.getScriptletBatchFlow(action.scriptDir, batch.executionZoneAction.executionZone.type)
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
        ScriptletBatch batch = new ScriptletBatch(description: "${user?.username} : ${action.executionZone.type} : ${action.scriptDir.name} ${action.executionZone.description? action.executionZone.description : "" }", executionZoneAction:action, user:user, comment:comment)

        PluginResolver pluginResolver = new PluginResolver(executionZoneService.getPluginDir(action.executionZone.type))
        File pluginFile = pluginResolver.resolveScriptletBatchPlugin(batch, action.runtimeAttributes)
        if (pluginFile) {
            executionService.injectPlugins(pluginFile, batch)
        }
        if (batch.hasErrors()) {
            throw new ProcessingException("Failure while building ${batch}: ${batch.errors}")
        }
        batch.save(flush:true, failOnError: true);
        action.scriptletBatches << batch
        action.save(flush:true, failOnError: true);

        this.addScriptlets(batch, action.runtimeAttributes)

        return batch
    }

    private List<Scriptlet> addScriptlets(ScriptletBatch batch, List runtimeAttributes) {
        ScriptResolver scriptsResolver = new ScriptResolver(batch.executionZoneAction.scriptDir)
        PluginResolver pluginResolver = new PluginResolver(executionZoneService.getPluginDir(batch.executionZoneAction.executionZone.type))

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

            scriptlet.save(flush:true)
        }
    }



    ScriptletBatchFlow getScriptletBatchFlow(File scriptDir, List runtimeAttributes, ExecutionZoneType type) {
        if (scriptletFlowCache == null) {
          scriptletFlowCache = [:]
        }

        if (scriptletFlowCache[scriptDir.toString()+runtimeAttributes.toString()] == null || type.devMode ) {
          ScriptResolver scriptResolver = new ScriptResolver(scriptDir)
          String pathPluginDir = "${scriptDir.parent}${System.properties['file.separator']}..${System.properties['file.separator']}${ExecutionZoneService.PLUGINS_DIR}"
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
