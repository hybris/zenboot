package org.zenboot.portal.processing.meta

class ScriptMetadata {

    File script
    String description

    @Override
    String toString() {
        return this.description
    }
}
