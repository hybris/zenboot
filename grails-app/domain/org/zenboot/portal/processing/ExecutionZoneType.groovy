package org.zenboot.portal.processing


class ExecutionZoneType {

    Date creationDate
    String name
    String description
    boolean enabled = true
    boolean devMode = false

    static constraints = {
        name blank:false, unique:true
    }

    def beforeInsert = {
        this.creationDate = new Date()
    }

    @Override
    public int hashCode() {
        final int prime = 31
        int result = 1
        result = prime * result + ((name == null) ? 0 : name.hashCode())
        return result
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false
        }
        if (this.is(obj)) {
            return true
        }
        if (getClass() != obj.getClass()) {
            return false
        }
        ExecutionZoneType other = (ExecutionZoneType) obj
        if (name == null) {
            if (other.name != null) {
                return false
            }
        } else if (!name.equals(other.name)) {
            return false
        }
        return true
    }

    String toString() {
        return name
    }
}
