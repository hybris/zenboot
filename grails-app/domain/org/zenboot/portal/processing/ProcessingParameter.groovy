package org.zenboot.portal.processing

class ProcessingParameter {

    static auditable = true
    
    String name
    String value
    Boolean published = Boolean.FALSE
    Boolean exposed = Boolean.FALSE

    static constraints = {
        name nullable:false
        value nullable:false
        published nullable:false
        exposed nullable:false
    }

    static mapping = {
        name type: 'text'
        value type: 'text'
        cache false
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
        ProcessingParameter other = (ProcessingParameter) obj
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
