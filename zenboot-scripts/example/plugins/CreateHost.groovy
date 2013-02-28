import org.zenboot.portal.HostState
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.meta.annotation.Plugin


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