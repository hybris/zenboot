package org.zenboot.portal.processing.meta

class ScriptletMetadata extends ScriptMetadata {

    String author

    @Override
    String toString() {
        return "${this.author} / ${this.description}"
    }
}
