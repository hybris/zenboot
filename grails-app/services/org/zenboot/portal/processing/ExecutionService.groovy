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

/** a serviceClass dealing with all Execution-near topics which don't need
    state. Shouldn't call higher-level-services.
*/

class ExecutionService {

  def grailsApplication

  public Closure createProcessClosure(File file, Scriptlet owner) {
    if (file.getName().split(/\./)[-1] == "groovy") {
      return createGroovyfileBasedClosure(file,owner)
    } else {
      return createProcessHandlerBasedClosure(file,owner)
    }
  }

  private Closure createGroovyfileBasedClosure(File file, Scriptlet owner) {
    return { ProcessContext ctx ->
      def groovyScript = createObjectFromGroovy(file, owner)
      def oldStdOut
      def oldStdErr
      def outBufStr = new ByteArrayOutputStream()
      def errBufStr = new ByteArrayOutputStream()
      try {
        oldStdOut = System.out
        oldStdErr = System.err
        def newStdOut = new PrintStream(outBufStr)
        def newStdErr = new PrintStream(errBufStr)
        System.out = newStdOut
        System.err = newStdErr
        groovyScript.execute(ctx)
        System.out = oldStdOut
        System.err = oldStdErr
        owner.onOutput(outBufStr.toString())
        owner.onError(errBufStr.toString())
      } catch (Exception exc) {
        System.out = oldStdOut
        System.err = oldStdErr
        owner.onOutput(outBufStr.toString())
        owner.onError(errBufStr.toString())
        throw new PluginExecutionException("Execution of groovyScript '${file.getName()}' failed ': ${exc.getMessage()}", exc)
      }
    }
  }

  /** The process-Closure needs to execute the payload securely (timeouts!!)
      and call the Listener methods of the "owner"
  */
  private Closure createProcessHandlerBasedClosure(File file, Scriptlet owner) {
      return { ProcessContext ctx ->
          ProcessHandler procHandler = new ProcessHandler(
              file.toString(),
              this.grailsApplication.config.zenboot.process.timeout.toInteger() * 1000,
              new File(file.getParent())
          )
          procHandler.addProcessListener(owner)
          procHandler.execute(ctx.parameters)
          if (procHandler.hasError()) {
              if (procHandler.exitValue == 143) {
                // seems to be some kind of magicValue for a process which get killed
                throw new ScriptExecutionException("Execution of script '${procHandler.command}' took too long. Timeout is currently "+ this.grailsApplication.config.zenboot.process.timeout.toInteger()+" seconds.", procHandler.exitValue)
              } else {
                throw new ScriptExecutionException("Execution of script '${procHandler.command}' failed with return code '${procHandler.exitValue}'", procHandler.exitValue)
              }
          } else {
              def result = owner.getProcessOutputAsMap()
              if (!result.empty) {
                  ctx.parameters.putAll(result)
              }
          }
      }
  }

  private void injectPlugins(File pluginFile, Processable processable) {
    def plugin = createObjectFromGroovy(pluginFile, processable)
    def properties = plugin.metaClass.properties*.name
    properties.each { String propName ->
      if (propName.startsWith("on")) {
        processable.metaClass."$propName" = { ctx ->
          try {
            plugin."${propName}".delegate = this
            plugin."${propName}"(ctx)
          } catch (Exception exc) {
            throw new PluginExecutionException("Execution of plugin '${pluginFile}' failed in hook '${propName}': ${exc.getMessage()}", exc)
          }
        }
      }
    }
  }

  private Object createObjectFromGroovy(File pluginFile, Processable processable) {
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
    return plugin
  }
}
