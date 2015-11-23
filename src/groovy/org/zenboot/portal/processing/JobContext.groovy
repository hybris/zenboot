package org.zenboot.portal.processing

class JobContext {

    List<ExecutionZoneAction> actions = []
    // ms to wait between executions
    int jobExecutionDelay = 5000

}
