package org.zenboot.portal

import org.zenboot.portal.processing.CronjobExpression

class DailyCronJob {

    def cronjobService

    static triggers = {
        cron name: 'dailyTrigger', cronExpression: CronjobExpression.DAILY.toString()
    }

    def execute() {
        this.cronjobService.executeJobs(CronjobExpression.DAILY)
    }

}