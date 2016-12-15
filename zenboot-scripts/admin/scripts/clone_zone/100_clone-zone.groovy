import org.zenboot.portal.processing.ExecutionZoneType
import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ExecutionZoneService
import org.zenboot.portal.processing.meta.annotation.Parameter
import org.zenboot.portal.processing.meta.annotation.Plugin
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.zenboot.portal.processing.meta.annotation.Parameters
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.ProcessingException
import org.ho.yaml.Yaml

@Parameters([
  @Parameter(name="EXECID", description="ID of the ExecutionZone", type=ParameterType.CONSUME),
  @Parameter(name="EMERGENCY", description="ID of the ExecutionZone", type=ParameterType.CONSUME, defaultValue="no"),
])

class CreateExecutionZoneType {

 def grailsApplication
 def executionZoneService

  def execute = { ProcessContext ctx ->

    executionZoneService = grailsApplication.mainContext.getBean(ExecutionZoneService.class)

    def oldZone = ExecutionZone.findById(ctx.parameters['EXECID'])
    if (!oldZone) {
      throw new ProcessingException("ExecutionZone already exists with Domain " + ctx.parameters['DOMAIN'])
    }

    def params = ["description" : oldZone.description, "type" : ExecutionZoneType.findByName(oldZone.type.name), processingParameters: [:] ]
    ExecutionZone newZone = executionZoneService.createExecutionZone(params)
    oldZone.processingParameters.each() {
      newZone.addProcessingParameter(it.name,it.value)
    }
    // mark it as somehow broken, even in the description
    if (ctx.parameters['EMERGENCY'].toBoolean()) {
      // mark the old zone broken
      oldZone.description = "(br) " + oldZone.description
      oldZone.enabled = false
      oldZone.save()
    } else {
      // "unify" the new zone
      newZone.description = "copy of " + oldZone.description
      def domain = newZone.processingParameters.find { it.name == 'DOMAIN' }
      if (domain) {
        domain.value = "copyof"+domain.value
        domain.save()
      }
      newZone.save()
    }
  }
}
