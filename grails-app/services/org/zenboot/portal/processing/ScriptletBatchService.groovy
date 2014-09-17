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

class ScriptletBatchService implements ApplicationListener<ProcessingEvent> {

    static transactional = false //necessary to avoid duplicate event listener registration

    def grailsApplication
    def executionZoneService

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
            batch.execute(processContext)
            this.synchronizeExposedProcessingParameters(batch, processContext)
        }

        if (event.processAsync && grailsApplication.config.zenboot.processing.asynchron.toBoolean()) {
            // This leverages the grails executor-plugin
            // https://github.com/basejump/grails-executor#examples
            runAsync { execute(event) }
        } else {
            execute(event)
        }
    }

	private void synchronizeExposedProcessingParameters(ScriptletBatch batch, ProcessContext processContext) {
        ExecutionZoneAction action = batch.executionZoneAction
        ScriptletBatchFlow flow = executionZoneService.getScriptletBatchFlow(action.scriptDir)
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

    private ScriptletBatch buildScriptletBatch(ExecutionZoneAction action, Person user, String comment) {
        if (this.log.debugEnabled) {
            this.log.debug("Build scriptlet batch for action ${action}")
        }
        ScriptletBatch batch = new ScriptletBatch(description: "${user.username} : ${action.executionZone.type} : ${action.scriptDir.name} ${action.executionZone.description? action.executionZone.description : "" }", executionZoneAction:action, user:user, comment:comment)

        PluginResolver pluginResolver = new PluginResolver(executionZoneService.getPluginDir(action.executionZone.type))
        File pluginFile = pluginResolver.resolveScriptletBatchPlugin(batch, action.runtimeAttributes)
        if (pluginFile) {
            this.injectPlugins(pluginFile, batch)
        }

        if (batch.hasErrors()) {
            throw new ProcessingException("Failure while building ${batch}: ${batch.errors}")
        }

        batch.save(flush:true)
        action.scriptletBatches << batch
        action.save()

        this.addScriptlets(batch, action.runtimeAttributes)

        return batch
    }

    private List<Scriptlet> addScriptlets(ScriptletBatch batch, List runtimeAttributes) {
        ScriptResolver scriptsResolver = new ScriptResolver(batch.executionZoneAction.scriptDir)
        PluginResolver pluginResolver = new PluginResolver(executionZoneService.getPluginDir(batch.executionZoneAction.executionZone.type))

        scriptsResolver.resolve(runtimeAttributes).each { File file ->
            Scriptlet scriptlet = new Scriptlet(description:file.name, file:file)

            file.setExecutable(true)
            scriptlet.process = this.getScriptProcess(file, scriptlet)

            File pluginFile = pluginResolver.resolveScriptletPlugin(scriptlet, batch.executionZoneAction.runtimeAttributes)
            if (pluginFile) {
                this.injectPlugins(pluginFile, scriptlet)
            }

            batch.processables << scriptlet
            scriptlet.scriptletBatch = batch

            scriptlet.save(flush:true)
        }
    }

    private void injectPlugins(File pluginFile, Processable processable) {
        GroovyClassLoader gcl = new GroovyClassLoader(this.class.classLoader)
        Class clazz = gcl.parseClass(pluginFile)

        def plugin = clazz.newInstance()
        def properties = plugin.metaClass.properties*.name
        if (properties.contains('grailsApplication')) {
            plugin.grailsApplication = grailsApplication
        }
        if (properties.contains('scriptlet')) {
            switch (processable.class) {
                case Scriptlet:
                plugin.scriptlet = processable
                break
                case ScriptletBatch:
                plugin.scriptlet = processable.processables
                break
            }
            plugin.scriptlet = processable
        }
        if (properties.contains('scriptletBatch')) {
            switch (processable.class) {
                case Scriptlet:
                plugin.scriptletBatch = processable.scriptletBatch
                break
                case ScriptletBatch:
                plugin.scriptletBatch = processable
                break
            }
        }
        properties.each { String propName ->
            if (propName.startsWith("on")) {
                processable.metaClass."$propName" = { ctx ->
                    try {
                        plugin."${propName}".delegate = this
                        plugin."${propName}"(ctx)
                    } catch (Exception exc) {
                        throw new PluginExecutionException("Execution of plugin '${clazz.name}' failed in hook '${propName}': ${exc.getMessage()}", exc)
                    }
                }
            }
        }
    }

    private Closure getScriptProcess(File file, Scriptlet owner) {
        return { ProcessContext ctx ->
            ProcessHandler procHandler = new ProcessHandler(
                file.toString(),
                this.grailsApplication.config.zenboot.process.timeout.toInteger() * 1000,
                new File(file.getParent())
            )
            procHandler.addProcessListener(owner)
            procHandler.execute(ctx.parameters)
            if (procHandler.hasError()) {
                throw new ScriptExecutionException("Execution of script '${procHandler.command}' failed with return code '${procHandler.exitValue}'", procHandler.exitValue)
            } else {
                def result = owner.getProcessOutputAsMap()
                if (!result.empty) {
                    ctx.parameters.putAll(result)
                }
            }
        }
    }

    ScriptletBatchFlow getScriptletBatchFlow(File scriptDir, List runtimeAttributes) {
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
        return flow.build()
    }
}
