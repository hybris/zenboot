package org.zenboot.portal.processing

import org.zenboot.portal.processing.Processable.ProcessState
import org.zenboot.portal.security.Person
import org.zenboot.portal.Host


/**  a Domain-class to Stores the result of a complete Stack-run in the DB
 *   Stores a List of Proecessables, which are Scriptlets (only) at this point
 */
class ScriptletBatch extends Processable {

    String comment

    List processables = []

    static hasMany = [processables: Processable]

    static belongsTo = [executionZoneAction: ExecutionZoneAction, user:Person, host: Host]

    static constraints = {
        processables(nullable:false)
    }

    static mapping = {
        processables cascade: 'all-delete-orphan'
    }

    @Override
    public void execute(ProcessContext ctx) {
        try {
            super.execute(ctx);
        } catch (Exception exc) {
            //cancel all sub process in case of a failure
            this.cancel()
            throw exc
        }
    }

    def process = { ProcessContext ctx ->
       this.processables.each { Processable processable ->
           processable.execute(ctx)
       }
       def processUnitFailed = this.processables.find { Processable processUnit ->
           (processUnit.state == ProcessState.FAILURE)
       }
       if (processUnitFailed) {
           throw new ProcessingException("Process queue failed caused by previous failing process unit '${processUnitFailed}'")
       }
    }

    @Override
    public int countProcessables() {
        return this.processables.size()
    }

    @Override
    public int countExecutedProcessables() {
        return this.processables.findAll { Processable processable ->
            return (processable.state != ProcessState.WAITING)
        }.size()
    }

    int getProgress() {
        if (this.processables.isEmpty()) {
            return 0
        }
        switch (this.state) {
            case ProcessState.FAILURE:
            case ProcessState.SUCCESS:
                return 100
            case ProcessState.WAITING:
                return 0
            default:
                int total = this.processables.inject(0) { int counter, Processable processable ->
                    counter += processable.countProcessables()
                }
                int done = this.processables.inject(0) { int counter, Processable processable ->
                    counter += processable.countExecutedProcessables()
                }
                return (done/total) * 100
        }
    }

    boolean isExecutable() {
        return !this.processables.empty
    }

    @Override
    public boolean cancel() {
        this.processables.each { Processable processable ->
            processable.cancel()
        }
        return super.cancel();
    }
}
