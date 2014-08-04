import org.codehaus.groovy.grails.plugins.exceptions.PluginException
import org.zenboot.portal.Customer
import org.zenboot.portal.Environment
import org.zenboot.portal.Host
import org.zenboot.portal.HostService
import org.zenboot.portal.HostState
import org.zenboot.portal.Hostname
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.meta.annotation.Parameter
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.zenboot.portal.processing.meta.annotation.Parameters
import org.zenboot.portal.processing.meta.annotation.Plugin


@Plugin(author="Kim Neunert (kim.neunert@hybris.com)", description="injects the scriptDir")
class InjectScriptDir {

    def grailsApplication

    @Parameters([
        @Parameter(name="SCRIPTDIR", description="The SCRIPTDIR Absolute Path", type=ParameterType.EMIT)
    ])
    def onStart = { ProcessContext ctx ->

        ctx.parameters['SCRIPTDIR'] = grailsApplication.config.zenboot.processing.scriptDir
    }

}
