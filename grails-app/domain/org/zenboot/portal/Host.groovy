package org.zenboot.portal
import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ScriptletBatch

public class Host {

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
    String iaasUser
    Environment environment
    String metaInformation

    static belongsTo = [execZone:ExecutionZone]

    static hasMany = [dnsEntries:DnsEntry,serviceUrls:ServiceUrl,scriptletBatches: ScriptletBatch]

    static mapping = {
        dnsEntries cascade: 'all-delete-orphan'
        hostname cascade: 'all'
        sort creationDate: "desc"
        scriptletBatches sort: 'creationDate', order: 'asc'
    }

    static constraints = {
        ipAddress(blank:false, length:7..15)
        cname(blank:false)
        macAddress(blank:false)
        instanceId(blank:false)
        state(nullable:false)
        hostname(nullable:false)
        environment(nullable:false)
        metaInformation(nullable: true, blank: true)
    }


    def beforeInsert = { creationDate = new Date() }

    String toString() {
        return "${this.hostname} (${this.ipAddress})"
    }
}
