package org.zenboot.portal.processing.meta

class ScriptMetadata {

    File script
    String description

    // Below constructor will cause the empty constructor to disappear
    // so i'll add it explicitely
    public ScriptMetadata(){}

    public ScriptMetadata(ScriptMetadata sm) {
      this.script = sm.script
      this.description = sm.description
    }

    @Override
    String toString() {
        return this.description
    }
}
