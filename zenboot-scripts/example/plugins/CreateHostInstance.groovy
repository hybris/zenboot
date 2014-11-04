import org.codehaus.groovy.grails.plugins.exceptions.PluginException
import org.zenboot.portal.Customer
import org.zenboot.portal.Environment
import org.zenboot.portal.Host
import org.zenboot.portal.HostService
import org.zenboot.portal.HostState
import org.zenboot.portal.Hostname
import org.zenboot.portal.processing.InstancePoolExhaustedException
import org.zenboot.portal.processing.ExecutionZoneService
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.meta.annotation.Parameter
import org.zenboot.portal.processing.meta.annotation.ParameterType
import org.zenboot.portal.processing.meta.annotation.Parameters
import org.zenboot.portal.processing.meta.annotation.Plugin


/**
 * The hooks of this plugin will be executed at the beginning (onStart) and at the end (onSuccess, onFailure, onStop)
 * of the exection of script "X_create-host-instance.rb".
 *
 * This plugin generates first a unique hostname which is passed to the X_create-host-instance.rb script.
 * If the script was successfully finished, a customer and host model will be created in the database.
 *
 */
@Plugin(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Create a host model in the database (host-state is initally set to CREATED)")
class CreateHostInstance {

    def grailsApplication

    @Parameters([
        @Parameter(name="CUSTOMER_EMAIL", description="The email address of the customer who will own this host", type=ParameterType.CONSUME),
        @Parameter(name="HOSTNAME", description="The hostname which has to be used for the new host", type=ParameterType.EMIT)
    ])
    def onStart = { ProcessContext ctx ->


        HostService hostService = grailsApplication.mainContext.getBean(HostService.class)
        int existingHosts = Host.countByStateNotEqual(HostState.DELETED)
        int poolsize = 150 // could be something like: grailsApplication.config.zenboot.host.instances.poolSize.toInteger()

        if (poolsize <= existingHosts) {
            throw new InstancePoolExhaustedException("Can not create host because host pool is exhausted")
        }

        // Not set means "no limit"
        if (ctx.execZone.hostLimit && ctx.execZone.hostLimit!="") {
          if (ctx.execZone.hostLimit <= ctx.execZone.getNonDeletedHosts().size()) {
              throw new InstancePoolExhaustedException("Can not create host because execZones's hostLimit is exhausted")
          }
        }


        // We could do it here but for convenience, we have something in the code:
        Hostname hostname = hostService.nextHostName()
        log.info("Hostname created: ${hostname}")

        //add the hostname to the process context
        //all parameters in ctx.parameters are passed as environment parameters to the following scripts
        ctx.parameters['HOSTNAME'] = hostname
    }


    @Parameters([
        @Parameter(name="CUSTOMER_EMAIL", description="The email address if the customer", type=ParameterType.CONSUME),
        @Parameter(name="HOSTNAME", description="The name of the host", type=ParameterType.CONSUME),
        @Parameter(name="DOMAIN", description="The name of the host", type=ParameterType.CONSUME),
        @Parameter(name="MAC", description="The MAC address of the host", type=ParameterType.CONSUME),
        @Parameter(name="IP", description="The IP address of the host", type=ParameterType.CONSUME)
    ])
    def onSuccess = { ProcessContext ctx ->
        HostService hostService = grailsApplication.mainContext.getBean(HostService.class)
        ExecutionZoneService executionZoneService = grailsApplication.mainContext.getBean(ExecutionZoneService.class)

        if (ctx.execZone.defaultLifetime && ctx.execZone.defaultLifetime!="") {
          // ToDo calculate ExpiryDate
        }
        Host host = new Host(
                execZone:    ctx.execZone,
                ipAddress :  ctx.parameters['IP'],
                cname:       ctx.parameters['NODENAME']+"."+ctx.parameters['DOMAIN'],
                macAddress:  ctx.parameters['MAC'],
                instanceId:  ctx.parameters.getObject('HOSTNAME').id, //can be an internal id to manage this host in a cloud environment
                hostname:    ctx.parameters.getObject('HOSTNAME'),
                owner:       Customer.findByEmail(ctx.parameters['CUSTOMER_EMAIL']) ?: new Customer(email:ctx.parameters['CUSTOMER_EMAIL']).save(),
                expiryDate:  executionZoneService.getExpiryDate(ctx.execZone),
                state:       HostState.CREATED,
                environment: Environment.DEVELOPMENT
                )

        if (!host.validate()) {
            throw new PluginException("Can not created host model (${host.hostname}): ${host.errors}")
        }

        host.save(flush:true)
        host.addToScriptletBatches(ctx.scriptletBatch)
        println("context: " + ctx)
        ctx.host = host

        log.info("Host created: ${host}")
    }
}
