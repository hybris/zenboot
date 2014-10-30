import org.zenboot.portal.Host
import org.zenboot.portal.HostState
import org.zenboot.portal.processing.JobContext
import org.zenboot.portal.processing.ExecutionZoneAction
import org.zenboot.portal.processing.ProcessingEvent
import org.springframework.context.ApplicationEventPublisher
import org.zenboot.portal.processing.meta.annotation.Parameter
import org.zenboot.portal.processing.meta.annotation.Plugin
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.zenboot.portal.processing.meta.annotation.Parameters
import org.zenboot.portal.processing.ProcessContext

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
 @Parameters([
     @Parameter(name="HOWMANY", description="the number of slaves to create", type=ParameterType.CONSUME),
     @Parameter(name="WAIT", description="seconds to wait", type=ParameterType.CONSUME)
 ])
class CreateNumberHosts {

  def grailsApplication
  def executionZoneService

  def execute = { ProcessContext ctx ->
    executionZoneService = this.grailsApplication.mainContext.getBean('executionZoneService')
    for ( i in 1..ctx.parameters['HOWMANY'].toInteger() ) {
      this.grailsApplication.mainContext.getBean('executionZoneService').createAndPublishExecutionZoneAction(ctx.execZone, "create_jenkinsslave")
      Thread.sleep(ctx.parameters['HOWMANY'].toInteger()*1000);
    }


  }
}
