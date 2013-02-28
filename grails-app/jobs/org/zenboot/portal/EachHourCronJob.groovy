package org.zenboot.portal

import org.zenboot.portal.processing.CronjobExpression

class EachHourCronJob {

    def cronjobService

    static triggers = {
        cron name: 'eachHourTrigger', cronExpression: CronjobExpression.EACH_HOUR.toString()
    }

    def execute() {
        this.cronjobService.executeJobs(CronjobExpression.EACH_HOUR)
    }

}