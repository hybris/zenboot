package org.zenboot.portal

class Hostname {

    String name
    Date creationDate

    static belongsTo = [owner:Host]

    static constraints = {
        name(blank:true)
        owner(nullable:true)
    }

    def beforeInsert = { creationDate = new Date() }

    String toString() {
        return this.name
    }

    @Override
    int hashCode() {
        final int prime = 31
        int result = 1
        result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode())
        result = prime * result + ((name == null) ? 0 : name.hashCode())
        return result
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null) {
            return false
        }
        if (this.is(obj)) {
            return true
        }
        if (getClass() != obj.getClass()) {
            return false
        }
        Hostname other = (Hostname) obj
        if (creationDate == null) {
            if (other.creationDate != null) {
                return false
            }
        } else if (!creationDate.equals(other.creationDate)) {
            return false
        }
        if (name == null) {
            if (other.name != null) {
                return false
            }
        } else if (!name.equals(other.name)) {
            return false
        }
        return true
    }
}
