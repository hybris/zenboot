package org.zenboot.portal.processing

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import org.zenboot.portal.Host
import org.zenboot.portal.HostState
import org.zenboot.portal.ServiceUrl

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(ExecutionZone)
class ExecutionZoneTest {

    ExecutionZone ez

    void setUp() {
      ez = new ExecutionZone(type: new ExecutionZoneType())
      ez.hosts << new Host(state: HostState.COMPLETED, serviceUrls: new ServiceUrl(urls: ["someUrl"]))
      ez.hosts << new Host(state: HostState.DISABLED, serviceUrls: new ServiceUrl(urls: ["yetAnUrl"]))
      ez.hosts << new Host(state: HostState.COMPLETED, serviceUrls: new ServiceUrl(urls: ["someOtherUrl"]))
      ez.hosts << new Host(state: HostState.ACCESSIBLE, serviceUrls: new ServiceUrl(urls: ["yetAnotherUrl"]))
      ez.hosts << new Host(state: HostState.UNKNOWN, serviceUrls: new ServiceUrl(urls: ["wtfUrl"]))
      ez.hosts << new Host(state: HostState.UNMANAGED, serviceUrls: new ServiceUrl(urls: ["someOtherUrl"]))
      ez.hosts << new Host(state: HostState.DELETED, serviceUrls: new ServiceUrl(urls: ["someOtherUrl"]))
      println "size: " + ez.hosts[0].state
      ez.save()
    }

    void testGetCompletedAndUnmanagedHosts() {
      assert ez.getCompletedAndUnmanagedHosts().size() == 3
    }

    void testGetActiveServiceUrls() {
      assert ez.getActiveServiceUrls().size() == 3
    }

    void testGetNonDeletedHosts() {
      assert ez.getNonDeletedHosts().size() == 6
    }

    void testGetCompletedHosts() {
      assert ez.getCompletedHosts().size() == 2
    }
}
