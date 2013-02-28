import org.codehaus.groovy.grails.plugins.exceptions.PluginException
import org.zenboot.portal.HostState
import org.zenboot.portal.Hostname
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.meta.annotation.Parameter
import org.zenboot.portal.processing.meta.annotation.Parameters


/**
 * The hooks of this plugin will be executed at the beginning (onStart) and at the end (onSuccess, onFailure, onStop)
 * of the exection of script "X_delete-host-instance.sh".
 *
 * This plugin removes a host model from the database.
 *
 */
class DeleteHostInstance {

    def grailsApplication

    @Parameters([
        @Parameter(name="HOSTNAME", description="The name of the host")
    ])
    def onStart = { ProcessContext ctx ->
        ctx.host = Hostname.findByName(ctx.parameters['HOSTNAME'])?.owner
        if (!ctx.host) {
            throw new PluginException("Could not find a host with the name '${ctx.parameters['HOSTNAME']}'")
        }
    }

    def onSuccess = { ProcessContext ctx ->
        ctx.host.state = HostState.DELETED
        ctx.host.save(flush:true)
    }
}