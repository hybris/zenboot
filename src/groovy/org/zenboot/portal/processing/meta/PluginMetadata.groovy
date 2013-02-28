package org.zenboot.portal.processing.meta

class PluginMetadata extends ScriptMetadata {

	String author

	@Override
	String toString() {
		return "${this.author} / ${this.description}"
	}
}
