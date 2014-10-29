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
@Parameters([
  @Parameter(name="IP",       type=ParameterType.EMIT,    description="A random IP address"),
  @Parameter(name="ID",       type=ParameterType.EMIT,    description="A random IP address"),
  @Parameter(name="MAC",      type=ParameterType.EMIT,    description="A random MAC address"),
  @Parameter(name="HOSTNAME", type=ParameterType.CONSUME, description="The name of the host which will be set"),
  @Parameter(name="DO_API_KEY", type=ParameterType.CONSUME, description="Digital Occean API key"),
])
class Groovytestclass {


  def grailsApplication
  def exposedAction
  def hosts

  def execute = { ProcessContext ctx ->
    this.otherMethod(ctx)
  }

}

REQUEST_JSON="{\"name\":\"${HOSTNAME}\",\"region\":\"ams1\",\"size\":\"512mb\",\"image\":757789}"

echo "# REQUEST_JSON ..."
echo $REQUEST_JSON

RESPONSE_JSON=`curl -X POST "https://api.digitalocean.com/v2/droplets" \
    -d $REQUEST_JSON \
    -H "Authorization: Bearer $DO_API_KEY" \
    -H "Content-Type: application/json"`
echo "done"

ID=`echo $RESPONSE_JSON | jq .droplet.id`


while [ "$STATUS" != "\"active\"" ]; do
RESPONSE_JSON=`curl -X GET "https://api.digitalocean.com/v2/droplets/${ID}" \
    -H "Authorization: Bearer $DO_API_KEY"`
STATUS=`echo $RESPONSE_JSON | jq .droplet.status`
echo $STATUS
sleep 5
done

echo "ready!"

echo $RESPONSE_JSON
