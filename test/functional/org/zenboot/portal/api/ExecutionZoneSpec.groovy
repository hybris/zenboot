package org.zenboot.portal.api

class ExecutionZoneSpec extends ZenbootApiSpec {
    def 'list executionZones'() {
        when:
        def res = http.get(
            path: 'rest/executionzones',
            query: [max: 10000]
        )

        then:
        res.status == 200
        res.headers['Content-Type'] ==~ 'Content-Type: application/json.*'
        res.responseData.find { it.description == 'Verify that Zenboot works' }
    }
}
