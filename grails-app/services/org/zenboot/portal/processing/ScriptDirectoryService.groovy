package org.zenboot.portal.processing

import org.ho.yaml.Yaml
import org.zenboot.portal.PathResolver

public class ScriptDirectoryService {
    def grailsApplication
    static final String SCRIPTS_DIR = 'scripts'
    static final String JOBS_DIR = 'jobs'
    static final String PLUGINS_DIR = 'plugins'

    File getZenbootScriptsDir() {
        String scriptDirName = grailsApplication.config.zenboot.processing.scriptDir
        File scriptDir = new File(PathResolver.getAbsolutePath(scriptDirName))
        if (!scriptDir.exists() || !scriptDir.isDirectory()) {
            throw new ExecutionZoneException("Could not find script directory ${scriptDir}")
        }
        return scriptDir
    }

    File getPluginDir(ExecutionZoneType type) {
        return getDir(type, PLUGINS_DIR)
    }

    File getJobDir(ExecutionZoneType type) {
        return getDir(type, JOBS_DIR)
    }

    File getScriptDir(ExecutionZoneType type) {
        return getDir(type, SCRIPTS_DIR)
    }

    private File getDir(ExecutionZoneType type, String subDir) {
        String path = "${getZenbootScriptsDir()}${System.properties['file.separator']}${type.name}"
        if (!subDir.isEmpty()) {
            path = "${path}${System.properties['file.separator']}${subDir}"
        }
        return new File(path)
    }

    List getScriptDirs(ExecutionZoneType type) {
        List scriptDirs = []
        File scriptDir = this.getScriptDir(type)
        if (scriptDir.exists()) {
            scriptDir.eachDir {
                scriptDirs << it
            }
        }
        return scriptDirs.sort()
    }

    List getScriptDirs(ExecutionZoneType type, String filter) {
        List scriptDirs = []
        File scriptDir = this.getScriptDir(type)
        if (scriptDir.exists()) {
            scriptDir.eachDir {
                File metaFile = new File(it, ".meta.yaml")
                if (metaFile.exists()) {
                    def yaml = Yaml.load(metaFile)
                    if (yaml['ui-script-types'].contains(filter)) {
                        scriptDirs << it
                    }
                } else {
                    if (filter == "misc") {
                        scriptDirs << it
                    }
                }
            }

        }
        return scriptDirs.sort()
    }
}
