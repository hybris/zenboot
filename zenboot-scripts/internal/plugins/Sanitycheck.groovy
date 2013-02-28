import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ExecutionZoneType
import org.zenboot.portal.processing.PluginExecutionException
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.meta.annotation.Plugin


/**
 * This Plugin has the same name as the script-folder "sanitycheck". So it will be executed as Scriptlet-Batch-Plugin.
 *
 * The hooks of a Scriptlet-Batch-Plugin are executed at the beginning of the script-batch execution (e.g. onStart)
 * and at the end (e.g. onSuccess, onFailure, onStop).
 */
@Plugin(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Do some simple test to verify that Zenboot is running")
class CreateDns {

    def onStart = { ProcessContext ctx ->
        ExecutionZoneType typeSanity = ExecutionZoneType.findByName("internal")
        ExecutionZone execZoneSanity = null
        if (typeSanity) {
            execZoneSanity = ExecutionZone.findByType(typeSanity)
        }
        if (typeSanity && execZoneSanity) {
            log.info("Sanitycheck ready to start...")
        } else {
            throw new PluginExecutionException("Sanitycheck can not be started. Action models not correct configured")
        }
    }

    def onSuccess = { ProcessContext ctx ->
        log.info("Sanitycheck passed!")
    }

    def onFailure = { ProcessContext ctx ->
        log.err("Sanitycheck failed!")
    }
}