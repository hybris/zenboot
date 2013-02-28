package org.zenboot.portal.processing.flow

import org.zenboot.portal.processing.meta.ParameterMetadataList
import org.zenboot.portal.processing.meta.PluginAnnotationReader
import org.zenboot.portal.processing.meta.ScriptletAnnotationReader

class ScriptletBatchFlow {

    private ScriptletAnnotationReader scriptletAnnoReader = new ScriptletAnnotationReader()
    private PluginAnnotationReader pluginAnnoReader = new PluginAnnotationReader(this.class.classLoader)

    private PluginFlowElement batchPlugin
    private List flowElements = []
    private ParameterMetadataList paramMetaList

    void setBatchPlugin(File plugin) {
        if (plugin) {
            this.batchPlugin = new PluginFlowElement(metadata:pluginAnnoReader.getPlugin(plugin), file:plugin)
        }
    }

    ParameterMetadataList getParameterMetadataList() {
        if (!this.paramMetaList) {
            throw new IllegalStateException("Can not provide ${ParameterMetadataList.class.simpleName} because flow isn't build.")
        }
        return this.paramMetaList
    }

    void addFlowElement(File script, File plugin) {
        PluginFlowElement pluginElement
        if (plugin) {
            pluginElement = new PluginFlowElement(metadata:this.pluginAnnoReader.getPlugin(plugin), file:plugin)
        }
        this.flowElements << new ScriptletFlowElement(metadata:this.scriptletAnnoReader.getScriptlet(script), plugin:pluginElement, file:script)
    }

    ScriptletBatchFlow build() {
        this.paramMetaList = new ParameterMetadataList()

        this.resolvePluginPreProcessingParameters(this.batchPlugin)
        this.flowElements.each { ScriptletFlowElement element ->
            this.resolvePluginPreProcessingParameters(element.plugin)
            this.resolveScriptletParameters(element)
            this.resolvePluginPostProcessingParameters(element.plugin)
        }
        this.resolvePluginPostProcessingParameters(this.batchPlugin)

        return this
    }

    private resolvePluginPreProcessingParameters(PluginFlowElement pluginEle) {
        if (pluginEle) {
            def preParamMetas = this.pluginAnnoReader.getParameters(pluginEle.file, "onStart")
            if (preParamMetas) {
                this.paramMetaList.addParameters(preParamMetas)
            }
        }
    }

    private resolvePluginPostProcessingParameters(PluginFlowElement pluginEle) {
        if (pluginEle) {
            def postParamMetas = this.pluginAnnoReader.getParameters(pluginEle.file, ["onSuccess", "onStop"])
            if (postParamMetas) {
                this.paramMetaList.addParameters(postParamMetas)
            }
        }
    }

    private resolveScriptletParameters(ScriptletFlowElement scriptletEle) {
        def paramMetas = this.scriptletAnnoReader.getParameters(scriptletEle.file)
        if (paramMetas) {
            this.paramMetaList.addParameters(paramMetas)
        }
    }
}