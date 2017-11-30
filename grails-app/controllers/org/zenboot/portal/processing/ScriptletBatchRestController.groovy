package org.zenboot.portal.processing

import grails.converters.JSON
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.processing.flow.ScriptletFlowElement

class ScriptletBatchRestController extends AbstractRestController {

    static allowedMethods = [listscriptletsdetails: 'GET']

    def scriptletBatchService
    def scriptDirectoryService

    /**
     * The method returns a list of detailed information about the scriptletbatch and releated scriptlet metadata details.
     */
    def listscriptletsdetails = {
        ExecutionZoneType type
        if (params.execType && params.execType.isInteger()) {
            type = ExecutionZoneType.findById(params.execType)
        } else {
            type = ExecutionZoneType.findByName(params.execType)
        }

        if (!type) {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No executionzonetype for ' + params.execType + ' found.')
            return
        }

        String scriptletBatchName = params.scriptletBatchName

        File stackDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                + "/" + type.name + "/scripts/" + scriptletBatchName)

        if (!isValidScriptDir(stackDir)) {
            return
        }

        def batchflow = scriptletBatchService.getScriptletBatchFlow(stackDir, type)

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String zones = builder.bind {
                    if (batchflow.batchPlugin) {
                        scriptletbatch {
                            name batchflow.batchPlugin.file.name
                            author batchflow.batchPlugin.metadata.author
                            description batchflow.batchPlugin.metadata.description
                            batchflow.flowElements.each { ScriptletFlowElement flowelement ->
                                scriptlet {
                                    name flowelement.file.name
                                    author flowelement.metadata.author
                                    description flowelement.metadata.description
                                    if(flowelement.plugin) {
                                        plugin 'Plugin'
                                    }
                                }
                            }
                        }
                    }
                }
                def xml = XmlUtil.serialize(zones).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                def scriptletbatch = [:]


                if(batchflow.batchPlugin) {
                    scriptletbatch.put('name', batchflow.batchPlugin.file.name)
                    scriptletbatch.put('author', batchflow.batchPlugin.metadata.author)
                    scriptletbatch.put('description', batchflow.batchPlugin.metadata.description)
                    def scriptlets = []
                    batchflow.flowElements.each { ScriptletFlowElement flowelement ->
                        def scriptlet = [:]
                        scriptlet.put('name', flowelement.file.name)
                        scriptlet.put('author', flowelement.metadata.author)
                        scriptlet.put('description', flowelement.metadata.description)
                        if(flowelement.plugin) {
                            scriptlet.put('plugin', 'Plugin')
                        }
                        scriptlets.add(scriptlet)
                    }
                    scriptletbatch.put('scriptlets', scriptlets)
                }
                render(contentType: "text/json") { scriptletbatch } as JSON
            }
        }
    }

    /**
     * Check if the script file exists. If not it renders NOT_FOUND with the error message that the script file does not exists.
     * @param scriptDir the script File object.
     * @return true if exists otherwise false.
     */
    private Boolean isValidScriptDir(File scriptDir) {
        if (scriptDir.exists()) {
            return Boolean.TRUE
        }
        else {
            renderRestResult(HttpStatus.NOT_FOUND, null, null, 'The script with path ' + scriptDir.getPath() + ' does not exists.')
        }
        return Boolean.FALSE
    }
}
