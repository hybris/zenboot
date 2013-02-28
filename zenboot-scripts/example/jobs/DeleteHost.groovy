import org.zenboot.portal.Host
import org.zenboot.portal.HostState
import org.zenboot.portal.processing.JobContext

/**
 * Jobs can be triggered by exposed actions. A job needs to fit to the script-folder name which is set in the exposed action.
 *
 * Jobs can define how often the exposed action should be executed. This is done by defining the "before" closure and
 * to fill the jobContext with action object.
 * 
 * The method ExecutionZoneService.createExecutionZoneAction will create an action for you using the exposed action object as template.
 * Parameters which will be used for this particular action are passed in the second method parameter.
 * 
 * If needed, an "after" closure can also be defined in a Job class. This hook will be called after all actions are executed.
 */
class DeleteHost {

    def grailsApplication
    def exposedAction
    def hosts

    def before = { JobContext jobCtx ->
        this.hosts = Host.withCriteria {
            le ("expiryDate", new Date())
            not {
                'in'("state", [HostState.DELETED, HostState.DISABLED, HostState.BROKEN])
            }
        }

        if (this.hosts.empty) {
            log.info("Found no hosts which exceeded their time-to-life")
            return
        }

        this.hosts.each { host ->
            jobCtx.actions << this.grailsApplication.mainContext.getBean('executionZoneService').createExecutionZoneAction(this.exposedAction, ['HOSTNAME': host.hostname.name])
        }
    }

    def after = { JobContext jobCtx ->
        if (this.hosts.empty) {
            return
        }
        log.info("Following hosts exceeded their time-to-life: ${this.hosts}")
    }

}
