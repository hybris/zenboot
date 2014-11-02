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

 @Parameters([
     @Parameter(name="HOWMANY", description="the number of slaves to create", type=ParameterType.CONSUME),
     @Parameter(name="SLEEP", description="seconds to wait", type=ParameterType.CONSUME)
 ])
class CreateNumberHosts {

  def grailsApplication
  def executionZoneService

  def execute = { ProcessContext ctx ->
    executionZoneService = this.grailsApplication.mainContext.getBean('executionZoneService')
    for ( i in 1..ctx.parameters['HOWMANY'].toInteger() ) {
      this.grailsApplication.mainContext.getBean('executionZoneService').createAndPublishExecutionZoneAction(ctx.execZone, "create_jenkinsslave")
      Thread.sleep(ctx.parameters['SLEEP'].toInteger()*1000);
    }


  }
}
