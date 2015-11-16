import org.zenboot.portal.HostState
import org.zenboot.portal.processing.meta.annotation.*

import org.apache.commons.logging.LogFactory
import org.zenboot.portal.processing.ScriptExecutionException
import org.zenboot.portal.processing.ProcessContext

import org.zenboot.portal.Host

@Scriptlet(author="Martin Sander", description="fails if active host with name already exists")
@SuppressWarnings("GroovyUnusedDeclaration")
public class CheckIfHostExists {
    private static final log = LogFactory.getLog("org.zenboot.portal.CheckIfHostExists")

    def execute(ProcessContext ctx) {
        if (!ctx.parameters.SHORTNAME) {
            return
        }

        log.info("SEARCHING for ${ctx.parameters.SHORTNAME}")

        def nonDeletedHostsQuery = Host.where {
            execZone == ctx.execZone
            cname ==~ "${ctx.parameters.SHORTNAME}.%"
            state != HostState.DELETED
        }

        if (nonDeletedHostsQuery.count() > 0) {
            def hostlist = nonDeletedHostsQuery.list().collect { "'id: ${it.id} hostname: ${it.hostname.name}'" }
            def message = "We already found non-deleted host(s) with this name: ${hostlist}. " +
                    "Please delete first and then re-create."
            log.error message

            throw new ScriptExecutionException(message, 1)
        }
    }
}
