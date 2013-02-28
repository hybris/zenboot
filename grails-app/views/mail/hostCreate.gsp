Dear customer ${ctx.host.owner.email},

your test instance has been created and is available here:

http://${ctx.host.dnsEntries.iterator().next().fqdn}

Please be aware that the usage of the host test instance is limited. Your host will be automatically deactivated at <g:formatDate date="${ctx.host.expiryDate}" type="datetime" style="SHORT"/>h.

Your sincerely,
Administration Portal
