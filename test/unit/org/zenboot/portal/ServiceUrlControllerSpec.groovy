package org.zenboot.portal

import grails.test.mixin.*
import org.grails.plugin.filterpane.FilterPaneService
import spock.lang.*

@TestFor(ServiceUrlController)
@Mock(ServiceUrl)
class ServiceUrlControllerSpec extends Specification {
    def setup() {
        controller.filterPaneService = Mock(FilterPaneService)
    }

    def populateValidParams(params) {
        assert params != null

        params.url = "http://example.com"
        def host = new Host(state: HostState.COMPLETED)
        host.save(validate:false)
        params.host = host.id
    }

    void "Test the list action returns the correct model"() {
        when: "The list action is executed"
        def model = controller.list()

        then:
        1 * controller.filterPaneService.filter(_, _) >> []
        1 * controller.filterPaneService.count(_, _) >> 0

        and: "The model is correct"
        !model.serviceUrlInstanceList
        model.serviceUrlInstanceTotal == 0
    }

    void "Test that the show action returns the correct model"() {
        when: "The show action is executed with a null domain"
        def model = controller.show()

        then: "A redirect is returned"
        response.status == 302
        response.redirectedUrl == '/serviceUrl/list'
        response.reset()

        when: "A domain instance is passed to the show action"
        def url = "http://example.com"
        def host = new Host(state: HostState.COMPLETED)
        host.save(validate:false)
        def serviceUrl = new ServiceUrl(url: url, owner: host).save()
        params.id = serviceUrl.id

        model = controller.show()

        then: "A model is populated containing the domain instance"
        model.serviceUrlInstance == serviceUrl
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when: "The delete action is called for a null instance"
        request.method = 'POST'
        controller.delete()

        then: "A redirect is returned"
        response.status == 302
        response.redirectedUrl == '/serviceUrl/list'
        flash.message != null

        when: "A domain instance is created"
        response.reset()
        def serviceUrl = new ServiceUrl(params).save(flush: true)

        then: "It exists"
        ServiceUrl.count() == 1

        when: "The domain instance is passed to the delete action"
        params.id = serviceUrl.id
        controller.delete()

        then: "The instance is deleted"
        ServiceUrl.count() == 0
        response.redirectedUrl == '/serviceUrl/list'
        flash.message != null
    }
}
