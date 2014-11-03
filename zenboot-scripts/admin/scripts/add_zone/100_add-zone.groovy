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
    @Parameter(name="DOMAIN", description="the domain", type=ParameterType.CONSUME),
    @Parameter(name="TYPE", description="The ExecutionZoneType to create", type=ParameterType.CONSUME)
])
class CreateExecutionZoneType {

 def grailsApplication
 def executionZoneService

  def execute = { ProcessContext ctx ->

    executionZoneService = grailsApplication.mainContext.getBean(ExecutionZoneService.class)

    if (executionZoneService.findByParameter("DOMAIN",ctx.parameters['DOMAIN']).size() > 0) {
      throw new ProcessingException("ExecutionZone already exists with Domain " + ctx.parameters['DOMAIN'])
    }


    def params = ["description" : ctx.parameters['DOMAIN'], "type" : ExecutionZoneType.findByName("example"), processingParameters: [:]]
    ExecutionZone execZone = executionZoneService.createExecutionZone(params)
    execZone.addProcessingParameter("DOMAIN",ctx.parameters['DOMAIN'])
  }
}
