package org.zenboot.portal.processing

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.zenboot.portal.processing.Processable.ProcessState

import java.util.concurrent.ConcurrentHashMap

@TestFor(ScriptletBatchController)
@Mock([ExecutionZone])
class ScriptletBatchControllerTests {

    def params = [:]
    def accessService

    ScriptletBatchControllerTests() {
        params = [:]

        accessService = new AccessService()
        accessService.accessCache = new ConcurrentHashMap<Long, HashMap>()
        accessService.accessCache[15] = new ConcurrentHashMap<Long, Boolean>()
        accessService.accessCache[15][1] = true
        accessService.accessCache[15][2] = false
        accessService.accessCache[15][7] = false
        accessService.accessCache[15][14] = true
    }


    void testGetAllScripletBatchesForCurrentUser() {

        registerNewGetOnExecutionZone()

        def springSecurityService = [getCurrentUserId: { -> 15 }] as SpringSecurityService


        def scripletBatchController = new ScriptletBatchController()
        scripletBatchController.accessService = accessService
        scripletBatchController.springSecurityService = springSecurityService


        def batches = scripletBatchController.getAllScripletBatchesForCurrentUser(params)
        assertEquals("ScripletBatch count unexpected", 2, batches.size())

        params.filter = new Object() {
            def executionZoneAction = new Object() {
                def executionZone = new Object() {
                    def id = 14
                }
            }
        }
        batches = scripletBatchController.getAllScripletBatchesForCurrentUser(params)
        assertEquals("ScripletBatch count unexpected", 1, batches.size())
    }

    def registerNewGetOnExecutionZone() {
        ExecutionZone.metaClass.static.get = { Serializable id ->
            HashSet<ExecutionZoneAction> eza = new HashSet<>()
            def se = new ExecutionZoneAction()
            se.scriptletBatches = new ArrayList()
            se.scriptletBatches.add(new ScriptletBatch())
            eza.add(se)

            def execZone = new ExecutionZone()
            execZone.actions = eza
            return execZone
        }
    }
}