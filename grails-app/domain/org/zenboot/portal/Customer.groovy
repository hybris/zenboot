package org.zenboot.portal

class Customer {

    String email
    Date creationDate
    Set hosts = []

    static hasMany = [hosts:Host]

    static constraints = {
        email(blank: false, email:true, unique:true)
    }

    boolean hasRunningHosts() {
        this.refresh()
        if (this.hosts?.empty) {
            return false
        } else {
            return this.hosts.any {
                it.state == HostState.COMPLETED
            }
        }
    }

    def beforeInsert = { creationDate = new Date() }

    String toString() {
        return "${this.class.getSimpleName()} (${this.email}/${this.id})"
    }
}
