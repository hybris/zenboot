package org.zenboot.portal.processing.meta

import org.zenboot.portal.processing.meta.annotation.ParameterType

class ParameterMetadata extends ScriptMetadata {

    String name
    String defaultValue
    ParameterType type
    boolean visible

    // Below constructor will cause the empty constructor to disappear
    // so i'll add it explicitely
    public ParameterMetadata(){}

    public ParameterMetadata(ParameterMetadata pm) {
      super(pm)
      this.name = pm.name
      this.defaultValue = pm.defaultValue
      this.type = pm.type
      this.visible = pm.visible

    }


    @Override
    public int hashCode() {
        final int prime = 31
        int result = 1
        result = prime * result + ((name == null) ? 0 : name.hashCode())
        result = prime * result + ((type == null) ? 0 : type.hashCode())
        return result
    }

    @Override
    public boolean equals(def obj) {
        if (obj == null) {
            return false
        }
        if (this.is(obj)) {
            return true
        }
        if (getClass() != obj.getClass()) {
            return false
        }
        ParameterMetadata other = (ParameterMetadata) obj
        if (name == null) {
            if (other.name != null) {
                return false
            }
        } else if (!name.equals(other.name)) {
            return false
        }
        if (type != other.type) {
            return false
        }
        return true
    }

    @Override
    String toString() {
        return "${this.type}: ${this.name}=${this.defaultValue} / ${this.description} / visible=${this.visible}"
    }

    ParameterMetadata clone() {
      return new ParameterMetadata(this)
    }
}
