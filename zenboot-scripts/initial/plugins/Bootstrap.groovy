import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ExecutionZoneType
import org.zenboot.portal.processing.PluginExecutionException
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.meta.annotation.Plugin

/**
 * This Plugin has the same name as the script-folder "bootstrap". So it will be executed as Scriptlet-Batch-Plugin.
 *
 * The hooks of a Scriptlet-Batch-Plugin are executed at the beginning of the script-batch execution (e.g. onStart)
 * and at the end (e.g. onSuccess, onFailure, onStop).
 */
@Plugin(author="Gordian Edenhofer", description="Populate the server with some default data")
class InitializeZonesAndUsers {

    def onStart = { ProcessContext ctx ->
        ExecutionZoneType typeBootstrap = ExecutionZoneType.findByName("initial")
        ExecutionZone execZoneBootstrap = null
        if (typeBootstrap) {
            execZoneBootstrap = ExecutionZone.findByType(typeBootstrap)
        }
        if (typeBootstrap && execZoneBootstrap) {
            log.info("Bootstrap ready to start...")
        } else {
            throw new PluginExecutionException("Bootstrap can not be started. Action models not correct configured!")
        }
    }

    def onSuccess = { ProcessContext ctx ->
        log.info("Bootstrap passed!")
    }

    def onFailure = { ProcessContext ctx ->
        log.err("Bootstrap failed!")
    }
}
