import org.zenboot.portal.HostState
import org.zenboot.portal.ServiceUrl
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.meta.annotation.Plugin
import org.codehaus.groovy.grails.plugins.exceptions.PluginException


/**
 * This Plugin has the same name as the script-folder. So it will be executed as Scriptlet-Batch-Plugin.
 *
 * The hooks of a Scriptlet-Batch-Plugin are executed at the beginning of the script-batch execution (e.g. onStart)
 * and at the end (e.g. onSuccess, onFailure, onStop)
 *
 * This plugin verifies the result of the batch processing. If the processing failed, the host is marked as broken.
 * Otherwise the host is marked as complete and ready.
 *
 */
@Plugin(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Set the final state of the Host (COMPLETED or BROKEN) depending on the result of the batch process")
class CreateHostInstance {

    def grailsApplication

    def onSuccess = { ProcessContext ctx ->
        if (ctx.parameters['SERVICEURL']) {
          ServiceUrl serviceUrl = new ServiceUrl(owner:ctx.host, url:ctx.parameters['SERVICEURL'])
          if (serviceUrl.hasErrors()) {
              throw new PluginException("Can not create ServiceUrl model")
          }
          serviceUrl.owner = ctx.host
          serviceUrl.save(flush:true)
        }

        //host is ready to use: mark it as complete
        ctx.host.state = HostState.COMPLETED
        ctx.host.save(flush:true)

        log.info("Host '${ctx.host}' is ready to use")

    }

    def onFailure = { ProcessContext ctx, Throwable exc ->
        //possible that the batch process failed before a host model was created
        if (ctx.host) {
            ctx.host.state = HostState.BROKEN
            ctx.host.save(flush:true)
        }

        log.info("Host could not be created")
    }
}
