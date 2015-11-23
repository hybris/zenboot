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

        def cancel = false

        // Searching for hosts exceeded their lifetime
        this.hosts = Host.withCriteria {
            le ("expiryDate", new Date())
            not {
                'in'("state", [HostState.DELETED, HostState.DISABLED, HostState.BROKEN])
            }
            eq("execZone", exposedAction.executionZone)

        }

        if (this.hosts.empty) {
            log.info("Found no hosts which exceeded their time-to-life")
            cancel = true
        } else {
            log.info("First search: Following hosts exceeded their time-to-life: ${this.hosts}")
        }

        // Defining Additional Criterial via the host
        def filterExpression = exposedAction.executionZone.processingParameters.find(){
          it.name == "DELETEHOSTJOB_HOST_FILTER"
        }
        // example: !( host.cname.startsWith('chefserver') )
        if (filterExpression) {
          this.hosts = this.hosts.findAll() { host ->
            Eval.me("host",host,filterExpression.value)
          }
        }

        // Defining Minimum numbers of hosts per role
        def minimumHashExpression = exposedAction.executionZone.processingParameters.find(){
          it.name == "DELETEHOSTJOB_ROLES_MINIMUM"
        }
        // example: ["jks":3,"jkm":1]
        if (minimumHashExpression) {
          def minimumHash = Eval.me(minimumHashExpression.value)
          minimumHash.each() { role, minInstances ->
            def toBeDeletedHostsSliceWithRole = this.hosts.findAll() { host ->
              host.cname.startsWith(role)
            }
            def allHostsWithRole = Host.withCriteria {
                not {
                    'in'("state", [HostState.DELETED, HostState.DISABLED, HostState.BROKEN])
                }
                eq("execZone", exposedAction.executionZone)
                like("cname", "${role}%")
            }
            if (allHostsWithRole.size() - toBeDeletedHostsSliceWithRole.size() < minInstances ) {
              log.info("minimum ${minInstances} reached for ${role} (all: ${allHostsWithRole.size()} / exceeded: ${toBeDeletedHostsSliceWithRole.size()} )!! ")
              def willDelete =  allHostsWithRole.size() - minInstances

              log.info("will Delete ${willDelete} from ${toBeDeletedHostsSliceWithRole.size()}")
              this.hosts = this.hosts - toBeDeletedHostsSliceWithRole.drop(willDelete)
            }
          }
        }


        if (this.hosts.empty) {
            log.info("Found no hosts which exceeded their time-to-life")
            cancel = true
        }

        this.hosts.each { host ->
          if (host.execZone.enableAutodeletion && !cancel) {
            log.info("DELETING " + host)
            jobCtx.actions << this.grailsApplication.mainContext.getBean('executionZoneService').createExecutionZoneAction(this.exposedAction, ['HOSTNAME': host.hostname.name])
          } else {
            log.info("not deleting" + host)
          }
        }
        jobCtx.jobExecutionDelay=10000
    }

    def after = { JobContext jobCtx ->
        if (this.hosts.empty) {
            return
        }
        log.info("Following hosts exceeded their time-to-life: ${this.hosts}")
    }

}
