package org.zenboot.portal
import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ScriptletBatch

class Host {

    static auditable = true


    String ipAddress
    String cname
    String macAddress
    String datacenter
    Date creationDate
    Date expiryDate
    String instanceId // can be used for the ID if the underlying IAAS-Provider
    HostState state = HostState.UNKNOWN
    Hostname hostname
    List dnsEntries = []
    Customer owner
    Environment environment

    static belongsTo = [execZone:ExecutionZone]

    static hasMany = [dnsEntries:DnsEntry,serviceUrls:ServiceUrl,scriptletBatches: ScriptletBatch]

    static mapping = {
        dnsEntries cascade: 'all-delete-orphan'
        hostname cascade: 'all'
        sort creationDate: "desc"
    }

    static constraints = {
        ipAddress(blank:false, length:7..15)
        cname(blank:false)
        macAddress(blank:false)
        hostname(nullable:false)
        instanceId(blank:false)
        state(nullable:false)
        hostname(nullable:false)
        environment(nullable:false)
    }


    def beforeInsert = { creationDate = new Date() }

    String toString() {
        return "${this.hostname} (${this.ipAddress})"
    }
}
