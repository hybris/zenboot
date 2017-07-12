package org.zenboot.portal.processing

abstract class AbstractExecutionZoneCommand {

    def executionZoneService

    Long execId
    File scriptDir
    boolean containsInvisibleParameters
    Map execZoneParameters
    Map parameters


    @SuppressWarnings("GroovyAssignabilityCheck")
    static constraints = {
        execZoneParameters nullable: true
        parameters nullable: true
        execId validator: { value ->
            def executionZone = ExecutionZone.get(value)
            if (!executionZone) {
                return "executionZone.does.not.exist"
            }
            if (!executionZone.enabled) {
                return "executionZone.disabled"
            }
        }
        scriptDir validator: { value, commandObj ->
            if (!value.exists()) {
                return "executionZone.failure.scriptDirNotExist"
            }
        }
    }

    abstract AbstractExecutionZoneAction getExecutionZoneAction()
}
