import org.zenboot.portal.Host
import org.zenboot.portal.HostState
import org.zenboot.portal.processing.JobContext
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
class Groovytestclass {

    def grailsApplication
    def exposedAction
    def hosts

  @Parameters([
      @Parameter(name="SOMEVAR", description="Some Variable to consume", type=ParameterType.CONSUME)
  ])
  def execute = { ProcessContext ctx ->
      this.otherMethod()
  }

  void otherMethod() {
    print "hello world! (output)\n"
    System.err << "Want this to go to stderr\n"
    print grailsApplication.inspect()
  }



}
