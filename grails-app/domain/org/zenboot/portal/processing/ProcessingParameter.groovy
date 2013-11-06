package org.zenboot.portal.processing

class ProcessingParameter implements Comparable {

    String name
    String value
    String description
    String comment
    Boolean published = Boolean.FALSE
    Boolean exposed = Boolean.FALSE
    Date dateCreated
    Date lastUpdated

    static hasMany = [processingParameterLogs: ProcessingParameterLog]

    static transients = ['comment']

    static constraints = {
        name nullable:false
        value nullable:false
        published nullable:false
        exposed nullable:false
    }

    static mapping = {
        name type: 'text'
        value type: 'text'
        description type: 'text'
        comment type: 'text'
        cache false
        processingParameterLogs cascade: "all-delete-orphan"
        sort "name"
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

    void setComment(String comment){
        this.comment = comment;
    }

    def addParameterLogs() {
        if (this.comment != null) {
            addToProcessingParameterLogs(new ProcessingParameterLog(name: this.name, description: this.description, value: this.value, comment: this.comment))
            this.comment = null
        }
    }

    def beforeUpdate() {
        // if nothing changes, do not log update
        if(!this.isDirty()) {
            this.comment = null
        }
    }

    def afterInsert(){
        addParameterLogs()
    }

    def afterUpdate(){
        addParameterLogs()
    }

    @Override
    int compareTo(Object obj) {
        name.toUpperCase() <=> obj.name.toUpperCase()
    }
}
