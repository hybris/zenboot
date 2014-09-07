package org.zenboot.portal

class DnsEntry {

    String fqdn
    String hostType
    Date creationDate
    Integer ttl

    static belongsTo = [owner:Host]

    static mapping = {
        sort creationDate: "desc"
    }

    static constraints = {
        fqdn(unique:true, blank:false)
        hostType(blank:false)
    }

    def beforeInsert = {
        if (!this.creationDate) {
            this.creationDate = new Date()
        }
    }

    String toString() {
        return this.fqdn
    }
}
