package org.zenboot.portal

import org.zenboot.portal.processing.CronjobExpression

class EachMinuteCronJob {

    def cronjobService

    static triggers = {
        cron name: 'eachMinuteTrigger', cronExpression: CronjobExpression.EACH_MIN.toString()
    }

    def execute() {
        this.cronjobService.executeJobs(CronjobExpression.EACH_MIN)
    }

}