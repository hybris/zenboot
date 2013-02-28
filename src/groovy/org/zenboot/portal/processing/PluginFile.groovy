package org.zenboot.portal.processing

class PluginFile extends AbstractRankableFile {

    static final String EXECUTIONZONETYPE_SEPARATOR = "_"

    private String pluginName

    PluginFile(File file, String pluginName) {
        super(file)
        this.pluginName = pluginName
    }

    String getPluginName() {
        return this.pluginName
    }

    protected Set qualify(String name) {
        //split camelCase file name in tokens
        Set qualifiers = name.replace("${this.pluginName}.groovy", "").split("(?=[A-Z])").asType(Set)
        //the split can sometimes cause results with empty values
        qualifiers.removeAll("", null)
        return qualifiers*.toLowerCase()
    }
}