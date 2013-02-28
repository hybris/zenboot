import org.codehaus.groovy.grails.plugins.exceptions.PluginException
import org.zenboot.portal.DnsEntry
import org.zenboot.portal.HostState
import org.zenboot.portal.processing.ProcessContext
import org.zenboot.portal.processing.meta.annotation.Parameter
import org.zenboot.portal.processing.meta.annotation.Plugin


/**
 * The hooks of this plugin will be executed at the beginning (onStart) and at the end (onSuccess, onFailure, onStop)
 * of the exection of script "X_create-dns.pl".
 *
 * This plugin create a DNS model in the database.
 */
@Plugin(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Create a DNS model in the database")
class CreateDns {

    def onStart = { ProcessContext ctx ->
        //verify that host was set (happens in the plugin 'CreateHostInstance.groovy')
        if (!ctx.host) {
            throw new PluginException("Host not found in process context. Can not create a DNS model without a host.")
        }
        //make sure that the host has the right state
        if (ctx.host.state < HostState.CREATED) {
            throw new PluginException("Host state has to be ${HostState.CREATED} to trigger DNS model but was ${ctx.host.state}")
        }

        log.info("Host passed sanity checks and can receive a DNS entry now")
    }

    @Parameter(name="FQDN", description="The full qualified DNS name from the host")
    def onSuccess = { ProcessContext ctx ->

        //create a DNS model, using the FQDN provided by the create-dns.pl script:
        DnsEntry dnsEntry = new DnsEntry(owner:ctx.host, fqdn:ctx.parameters['FQDN'], hostType:'A')
        if (dnsEntry.hasErrors()) {
            throw new PluginException("Can not created DNS model '${dnsEntry}': ${dnsEntry.errors}")
        }
        dnsEntry.owner = ctx.host
        dnsEntry.save(flush:true)

        ctx.host.addToDnsEntries(dnsEntry)
        ctx.host.state = HostState.ACCESSIBLE  //mark the host as accessible (can be resolved via DNS)
        ctx.host.save(flush:true)

        log.info("Dns entry '${dnsEntry}' created for host ${ctx.host}")
    }
}