package org.zenboot.portal.processing

class ExecutionZoneAction extends AbstractExecutionZoneAction {

    List runtimeAttributes = []
    List scriptletBatches = []

    static hasMany = [runtimeAttributes: String, scriptletBatches: ScriptletBatch]

    static mapping = {
        runtimeAttributes type: 'text', cascade: 'all'
        scriptletBatches cascade: 'all-delete-orphan'
    }


    String toString() {
        if (scriptletBatches != null && scriptletBatches[0]!=null) {
          return scriptletBatches[0].description +"(" + this.creationDate + ")"
        }
    }
}
