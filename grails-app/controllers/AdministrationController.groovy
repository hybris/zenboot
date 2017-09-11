import org.zenboot.portal.processing.ExecutionZoneAction

class AdministrationController {

    def accessService
    def sessionFactory

    def index = {
        redirect(action:"user")
    }

    def user = {}

    def dbconsole = {}

    def accessCache = {}

    def clear = {
      accessService.clearAccessCache()
      redirect(action:"accessCacheCleared")
    }

    def accessCacheCleared = {}

    // because of an error in execution zone controller many actions where created without any sense. This will clean all ExecutionzoneActions without scriptletBatches
    def database_cleanup = {
        if (params.totalSize) {
           [totalItems: params.totalSize]
        }
    }

    def database_cleaned = {
        log.info('Starting cleanup....')
        // a query which contains all actions which has no scriptletBatches nor processing parameters
        def dmgEmptyActions = ExecutionZoneAction.where { isEmpty('scriptletBatches') && isEmpty('processingParameters')}
        // a query which contains all actions which has no scriptletBatches
        def dmgNonEmptyActions = ExecutionZoneAction.where {isEmpty('scriptletBatches') && isNotEmpty('processingParameters')}

        def emptyActionList = dmgEmptyActions.list()
        def nonemptyActionList = dmgNonEmptyActions.list()
        //iterate over all actions with processing parameter to unbound them and make them deleteable.
        def processingParameterCounter = 0

        nonemptyActionList.each { action ->
            action.lock()
            def parameters = action.processingParameters.toArray()
            processingParameterCounter += parameters.size()
            parameters.each {
                action.removeFromProcessingParameters(it)
            }
        }

        // delete all in one query
        log.info('Removing ' + emptyActionList.size() + ' ExecutionZoneAction entries from the database.')
        dmgEmptyActions.deleteAll()
        // delete all in one query
        log.info('Removing ' + nonemptyActionList.size() + ' ExecutionZoneAction entries and ' + processingParameterCounter + ' ProcessingParameter entries from the database.')
        dmgNonEmptyActions.deleteAll()

        // flush session to make it real
        sessionFactory.currentSession.flush()
        def totalSize = emptyActionList.size() + nonemptyActionList.size() + processingParameterCounter
        log.info('Cleanup finished... total removed items from the database: ' + totalSize)
        redirect(action: 'database_cleanup', params: [totalSize: totalSize])
    }

}
