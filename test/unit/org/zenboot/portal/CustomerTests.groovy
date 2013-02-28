package org.zenboot.portal

import grails.test.mixin.*
import grails.test.mixin.domain.DomainClassUnitTestMixin

@TestFor(Customer)
@TestMixin(DomainClassUnitTestMixin)
class CustomerTests {

    void testHasHost() {
        Customer customer = new Customer(email: 'test@testmail.com', password: '1234')
        customer.hosts << new Host(state: HostState.COMPLETED)
        assertTrue("Host expected", customer.hasRunningHosts())
    }

    void testHasMultipleHosts() {
        Customer customer = new Customer(email: 'test@testmail.com', password: '1234')
        customer.hosts << new Host(state: HostState.COMPLETED)
        customer.hosts << new Host(state: HostState.ACCESSIBLE)
        customer.hosts << new Host(state: HostState.COMPLETED)
        customer.hosts << new Host(state: HostState.DISABLED)
        assertTrue("Host expected", customer.hasRunningHosts())
    }

    void testHasNoHosts() {
        Customer customer = new Customer(email: 'test@testmail.com', password: '1234')
        customer.hosts << new Host(state: HostState.DISABLED)
        customer.hosts << new Host(state: HostState.ACCESSIBLE)
        customer.hosts << new Host(state: HostState.BROKEN)
        assertFalse("No host expected", customer.hasRunningHosts())
    }
}