package org.zenboot.portal.processing




abstract class AbstractExecutionZoneCommand {

    def executionZoneService

    Long execId
    File scriptDir
    boolean containsInvisibleParameters
    Map execZoneParameters
    Map parameters


    static constraints = {
        execZoneParameters nullable: true
        parameters nullable: true
        scriptDir validator: { value, commandObj ->
            if (!value.exists()) {
                return "executionZone.failure.scriptDirNotExist"
            }
        }
    }



    abstract AbstractExecutionZoneAction getExecutionZoneAction();
}
