package org.zenboot.portal.processing




abstract class AbstractExecutionZoneCommand {

    def executionZoneService

    Long execId
    File scriptDir
    boolean containsInvisibleParameters
    Map execZoneParameters
    Map parameters


    static constraints = {
        execId nullable:false
        scriptDir nullable:false, validator: { value, commandObj ->
            if (!value.exists()) {
                return "executionZone.failure.scriptDirNotExist"
            }
        }
    }



    abstract AbstractExecutionZoneAction getExecutionZoneAction();
}
