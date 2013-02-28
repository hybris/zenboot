package org.zenboot.portal.processing

import java.util.List;
import java.util.Map;

import org.ho.yaml.Yaml

class ScriptResolver {
    
   private File scriptDir
    
    ScriptResolver(File scriptDir) {
        if (!scriptDir.exists()) {
            throw new ProcessingException("Script directory '${scriptDir.path}' not found")
        }
        this.scriptDir = scriptDir
    }
    
    List resolve(List attributes) {
        Map groupByOrder = [:]

        def scriptFiles = this.scriptDir.listFiles(new FileFilter()).sort()

        scriptFiles.each { File file ->
            //ignore hidden files, directories or file not following the naming convention
            if (file.isHidden() || file.isDirectory() || !(file.name ==~ /\d+_.*/)) {
                return
            }

            ScriptFile scriptFile = new ScriptFile(file)

            if (groupByOrder.containsKey(scriptFile.order)) {
                groupByOrder[scriptFile.order] << scriptFile
            } else {
                groupByOrder[scriptFile.order] = [scriptFile]
            }
        }

        List filteredScriptFiles = this.filterScriptFiles(attributes, groupByOrder)

        return this.injectYamlScriptFiles(filteredScriptFiles)
    }

	private List filterScriptFiles(List attributes, Map groupByOrder) {
        List filteredScriptFiles = []
        Comparator comparator = new ScriptFileComparator(attributes)
        groupByOrder.each { int order, List files ->
            if (files.size() > 1) {
                Collections.sort(files, comparator)
            }
            filteredScriptFiles << files[-1]
        }
        Collections.sort(filteredScriptFiles, comparator)
        return filteredScriptFiles*.file
    }

    private List injectYamlScriptFiles(List scriptFiles) {
		List result = []
		scriptFiles.each { File scriptFile ->
			if (scriptFile.name =~ /.*\.(yaml|yml)$/) {
				result.addAll(this.getScriptFilesFromYaml(scriptFile))
			} else {
				result.add(scriptFile)
			}
		}
        return result
	}

    private List getScriptFilesFromYaml(File yamlFile) {
        def yaml = Yaml.load(yamlFile)

        List lookupPath = this.getLookupPath(yamlFile.parent, yaml)

        List scriptFiles = yaml.scripts.findResults { String script ->
            lookupPath.findResult { String path ->
                def scriptFile = new File("${path}${System.properties['file.separator']}${script}")
                if (scriptFile.exists()) {
                    return scriptFile
                }
                return null
            }
        }

        return scriptFiles
    }

    private List getLookupPath(String rootDir, yaml) {
        List lookupPath = []
        lookupPath << rootDir
        yaml.lookupPath?.each { String path ->
            if (path.startsWith('/')) {
                lookupPath << path
            } else {
                lookupPath << "${rootDir}${System.properties['file.separator']}${path}"
            }
        }
        return lookupPath
    }
}

class FileFilter implements FilenameFilter {

    def pattern = ~/.*\.(txt|md)$/

    @Override
    boolean accept(File dir, String name) {
        return !pattern.matcher(name).matches()
    }

}