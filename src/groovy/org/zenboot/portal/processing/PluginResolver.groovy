package org.zenboot.portal.processing


class PluginResolver {

    private File pluginDir
    private List plugins = []

    PluginResolver(File pluginDir) {
        if (!pluginDir.exists()) {
            throw new ProcessingException("Plugin directory '${pluginDir.path}' not found")
        }
        this.pluginDir = pluginDir
        pluginDir.eachFile { File file ->
            if (file.name.endsWith(".groovy")) {
                this.plugins << file
            }
        }
    }

    File resolveScriptletBatchPlugin(ScriptletBatch batch, List attributes=[]) {
        return this.resolveScriptletBatchPlugin(batch.executionZoneAction.scriptDir, attributes)
    }

    File resolveScriptletBatchPlugin(File batchDir, List attributes=[]) {
        log.debug("Resolve plugins for scriptlet-batch directory ${batchDir.name} with attributes ${attributes}")

        String pluginName = this.getPluginName(batchDir.name, PluginFile.EXECUTIONZONETYPE_SEPARATOR)

        return this.getBestWeightedPlugin(pluginName, attributes)
    }

    File resolveScriptletPlugin(Scriptlet scriptlet, List attributes=[]) {
        return this.resolveScriptletPlugin(scriptlet.file, attributes)
    }

    File resolveScriptletPlugin(File script, List attributes=[]) {
        log.debug("Resolve plugins for scriptlet ${script.name} with attributes ${attributes}")

        String pluginName = this.getPluginName(new ScriptFile(script).getScriptName(false), ScriptFile.SCRIPT_SEPARATOR)

        return this.getBestWeightedPlugin(pluginName, attributes)
    }

	private String getPluginName(String scriptName, String separator) {
		String[] scriptNameTokens = scriptName.split(separator)
		String pluginName = scriptNameTokens.collect { String token ->
			token.capitalize()
		}.join("")
		return pluginName
	}

    private List getAvailablePlugins(String pluginName, List attributes) {
        List availablePlugins = this.plugins.findAll { File pluginFile ->
            (pluginFile.name =~ "^(${attributes*.toLowerCase()*.capitalize().join('|')})*${pluginName}.groovy")
        }
        return availablePlugins
    }

    private File getBestWeightedPlugin(String pluginName, List attributes) {
        List plugins = this.getAvailablePlugins(pluginName, attributes)

        if (plugins.empty) {
            return null
        }

        List weightedPlugins = []

        plugins.each { File file ->
            weightedPlugins << new PluginFile(file, pluginName)
        }

        PluginFileComparator comparator = new PluginFileComparator(attributes)
        Collections.sort(weightedPlugins, comparator)

        if (weightedPlugins.isEmpty()) {
            return null
        }
        if (attributes.isEmpty()) {
            return weightedPlugins[-1].isDefault() ? weightedPlugins[-1].file : null
        } else {
            return weightedPlugins[-1].file
        }
    }

}