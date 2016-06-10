package org.zenboot.portal.processing

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.junit.*

import org.zenboot.portal.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(ExecutionZone)
@TestMixin(DomainClassUnitTestMixin)
class ExecutionZoneTest {

    ExecutionZone ez
    void setUp() {
      ez = new ExecutionZone(type: new ExecutionZoneType())
      ez.hosts << new Host(state: HostState.COMPLETED, serviceUrls: "someUrl")
      ez.hosts << new Host(state: HostState.DISABLED, serviceUrls: "yetAnUrl")
      ez.hosts << new Host(state: HostState.COMPLETED, serviceUrls: "someOtherUrl")
      ez.hosts << new Host(state: HostState.ACCESSIBLE, serviceUrls: "yetAnotherUrl")
      ez.hosts << new Host(state: HostState.UNKNOWN, serviceUrls: "wtfUrl")
      ez.hosts << new Host(state: HostState.UNMANAGED, serviceUrls: "someOtherUrl")
      ez.hosts << new Host(state: HostState.DELETED, serviceUrls: "someOtherUrl")
      println "size: " + ez.hosts[0].state
      ez.save()
    }

    void tearDown() {

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
