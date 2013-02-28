package org.zenboot.portal.processing


class ScriptFile extends AbstractRankableFile {

    static final String ATTRIBUTE_SEPARATOR = "_"
    static final String SCRIPT_SEPARATOR = "-"

    private String[] tokens
    private String scriptName

    int order

    ScriptFile(File file) {
        super(file)
        this.setTokens(file.name)
        this.scriptName = this.tokens[-1]
        this.order = this.tokens[0].asType(Integer)
    }

    String getScriptName(boolean withExtension=true) {
        if (withExtension) {
            return this.scriptName
        }
        return this.scriptName.substring(0, this.scriptName.lastIndexOf('.'))
    }

    private setTokens(String name) {
        this.tokens = name.split(ATTRIBUTE_SEPARATOR)
        if (this.tokens.length < 2) {
            throw new ProcessingException("Scriptlet has invalid name pattern: ${name}")
        }
    }

    protected Set qualify(String name) {
        Set result = []
        if (tokens.length > 2) {
            for (int i = 1; i <= tokens.length-2; i++) {
                String qualifier = tokens[i].trim()
                if (qualifier) {
                    result << qualifier.toLowerCase()
                }
            }
        }
        return result
    }
}