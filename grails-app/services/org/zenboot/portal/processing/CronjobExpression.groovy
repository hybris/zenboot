package org.zenboot.portal.processing

enum CronjobExpression {

    NONE(''), WEEKLY("0 15 2 ? * SUN"), DAILY("0 15 4 * * ?"), EACH_HOUR("0 45 * * * ?"), EACH_MIN("25 * * * * ?")

    String cronExpression

    CronjobExpression(String cronExpression) {
        this.cronExpression = cronExpression
    }

    @Override
    String toString() {
        return this.cronExpression
    }
}
