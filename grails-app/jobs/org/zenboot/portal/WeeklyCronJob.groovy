package org.zenboot.portal

import org.zenboot.portal.processing.CronjobExpression

class WeeklyCronJob {

    def cronjobService

    static triggers = {
        cron name: 'weeklyTrigger', cronExpression: CronjobExpression.WEEKLY.toString()
    }

    def execute() {
        this.cronjobService.executeJobs(CronjobExpression.WEEKLY)
    }

}