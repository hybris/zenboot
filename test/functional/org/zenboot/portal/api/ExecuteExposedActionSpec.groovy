package org.zenboot.portal.api

import spock.util.concurrent.PollingConditions

class ExecuteExposedActionSpec extends ZenbootApiSpec {
    def 'execute sanitycheck'() {
        when: 'the sanitcheck exposed action is triggered'
        def res = http.post(
            path: 'rest/sanitycheck',
        )

        then: 'a callback is returned to check the status'
        res.status == 201
        res.headers['Content-Type'] ==~ 'Content-Type: application/json.*'
        def referralUrl = res.responseData['referral']
        referralUrl

        when: 'the status is checked'
        def conditions = new PollingConditions(timeout: 10, delay: 1, factor: 1)

        then: 'eventually, a RUNNING status is reported'
        conditions.eventually {
            try {
                res = http.get uri: referralUrl
            } catch (e) {
                assert false : "get returned ${e.response.status}"
            }
            assert res.status == 200
            assert res.responseData.status.scriptletBatch.state == 'RUNNING'
        }

        when: 'we get the list of running executions'
        res = http.get(path: 'scriptletBatch/ajaxList')

        then: 'the first one is running'
        res.status == 200
        res.responseData.queue.first().state == 'RUNNING'


        when: 'the status is checked even more'
        conditions = new PollingConditions(timeout: 10, delay: 1, factor: 1)

        then: 'eventually, a SUCCESS status is reported'
        conditions.eventually {
            try {
                res = http.get uri: referralUrl
            } catch (e) {
                assert false : "get returned ${e.response.status}"
            }
            assert res.status == 200
            assert res.responseData.status.scriptletBatch.state == 'SUCCESS'
        }

        when: 'we get the list of running executions'
        res = http.get(path: 'scriptletBatch/ajaxList')

        then: 'the first one is finished'
        res.status == 200
        res.responseData.queue.first().state == 'SUCCESS'
    }

}
