package org.zenboot.portal.processing

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.apache.commons.logging.Log
import org.zenboot.portal.security.Role
import spock.lang.Specification

@TestFor(AccessService)
@TestMixin(GrailsUnitTestMixin)
class AccessServiceSpec extends Specification {
    def 'true is always true'() {
        given:
        service.log = Mock(Log)

        expect:
        hasAccess == service.roleHasAccess(
            new Role(executionZoneAccessExpression: expression),
            new ExecutionZone()
        )

        where:
        hasAccess | expression
        true   | 'true'
        true   | '"truthy"'
        false  | 'null'
        false  | '[]'
        false  | '1 == 0'
        false  | 'throw new RuntimeException("Horst was here")'
    }

    def 'check description of executionzone'() {
        when:
        def hasAccess = service.roleHasAccess(
            new Role(executionZoneAccessExpression: 'executionZone.description == "horst"'),
            new ExecutionZone(description: 'horst')
        )
        then:
        hasAccess
    }

    def 'return true if one role has access'() {
        when:
        service.log = Mock(Log)

        then:
        service.rolesHaveAccess(
            [
                new Role(executionZoneAccessExpression: 'false'),
                new Role(executionZoneAccessExpression: 'null'),
                new Role(executionZoneAccessExpression: 'throw new RuntimeException("guenther")'),
                new Role(executionZoneAccessExpression: 'executionZone.description == "horst"'),
            ] as Set,
            new ExecutionZone(description: 'horst')
        )
    }

    def 'if no role has access, return false'() {
        when:
        service.log = Mock(Log)

        then:
        !service.rolesHaveAccess(
            [
                new Role(executionZoneAccessExpression: 'false'),
                new Role(executionZoneAccessExpression: 'null'),
                new Role(executionZoneAccessExpression: 'throw new RuntimeException("guenther")'),
                new Role(executionZoneAccessExpression: 'executionZone.description == "horst"'),
            ] as Set,
            new ExecutionZone(description: 'kevin')
        )
    }

    def 'do not check the other roles if you find a matching one'() {
        given:
        def role = Mock(Role)

        when: 'the user has two roles and the first one matches'
        def hasAccess = service.rolesHaveAccess(
            [
                new Role(executionZoneAccessExpression: 'true'),
                role,
            ] as Set,
            new ExecutionZone(description: 'kevin')
        )

        then: 'the second one is not checked'
        hasAccess
        0 * role.executionZoneAccessExpression >> 'true'
    }
}
