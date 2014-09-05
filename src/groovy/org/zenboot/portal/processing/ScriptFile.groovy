package org.zenboot.portal.processing


class ScriptFile extends AbstractRankableFile {

    static final String ATTRIBUTE_SEPARATOR = "_"
    static final String SCRIPT_SEPARATOR = "-"
    static final int MAGIC_WEIRD_NUMBER = -18 // for no having an order

    private String[] tokens
    private String scriptName

    int order

    ScriptFile(File file) {
        super(file)
        this.setTokens(file.name)
        this.scriptName = this.tokens[-1]
        setOrder(this.tokens[0])
    }

    String getScriptName(boolean withExtension=true) {
        if (withExtension) {
            return this.scriptName
        }
        return this.scriptName.substring(0, this.scriptName.lastIndexOf('.'))
    }

    private setTokens(String name) {
        this.tokens = name.split(ATTRIBUTE_SEPARATOR)
    }

    def setOrder(String token) {
        this.order = token.isNumber() ? token.asType(Integer) : MAGIC_WEIRD_NUMBER
    }

    def getOrder() {
        if (this.order == MAGIC_WEIRD_NUMBER) {
            throw new RuntimeException("Your file name does not start from number.")
        } else {
            return this.order
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