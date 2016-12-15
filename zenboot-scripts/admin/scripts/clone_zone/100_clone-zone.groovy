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
])

class CreateExecutionZoneType {

 def grailsApplication
 def executionZoneService

  def execute = { ProcessContext ctx ->

    executionZoneService = grailsApplication.mainContext.getBean(ExecutionZoneService.class)

    def params = ["description" : ctx.execZone.description, "type" : ExecutionZoneType.findByName(ctx.execZone.type.name), processingParameters: [:] ]
    ExecutionZone execZone = executionZoneService.createExecutionZone(params)
    ctx.execZone.processingParameters.each() {
      execZone.addProcessingParameter(it.name,it.value)
    }
    // mark it as somehow broken, even in the description
    ctx.execZone.description = "(br) " + ctx.execZone.description
    ctx.execZone.enabled = false
    ctx.execZone.save()


  }
}
