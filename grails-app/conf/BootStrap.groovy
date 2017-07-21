import grails.util.Environment

import org.zenboot.portal.Host
import org.zenboot.portal.processing.ExecutionZone
import org.zenboot.portal.processing.ExecutionZoneType
import org.zenboot.portal.processing.ExposedExecutionZoneAction
import org.zenboot.portal.processing.Processable
import org.zenboot.portal.processing.ScriptletBatch
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.PersonRole
import org.zenboot.portal.security.Role
import java.util.concurrent.Callable

class BootStrap {

    def executionZoneService
    def accessService
    def grailsApplication
    def scriptDirectoryService
    def executorService

    def init = { servletContext ->
        //create fundamental user groups
        this.setupSecurity()

        //sync execution zone types in each startup
        this.executionZoneService.synchronizeExecutionZoneTypes()

        //setup the sanity check (used for CI)
        this.setupSanityCheckExposedExecutionZoneAction()

        setupBootstrapExecutionZoneAction()

        // setting up JSON-Marshallers
        // otherwise you're rendering out half of the heap-space
        grails.converters.JSON.registerObjectMarshaller(ExecutionZone) {
            // you can filter here the key-value pairs to output:
            def returnArray = [:]
            returnArray['id'] = it.id
            returnArray['type'] = it.type.name
            returnArray['description'] = it.description
            returnArray['hosts'] = it.hosts
            returnArray['serviceUrls'] = it.getActiveServiceUrls()
            returnArray['customerEmail'] = it.param('CUSTOMER_EMAIL')
            return returnArray
        }

        grails.converters.JSON.registerObjectMarshaller(Host) {
            // you can filter here the key-value pairs to output:
            def returnArray = [:]
            returnArray['ipAddress'] = it.ipAddress
            returnArray['cname'] = it.cname
            returnArray['macAddress'] = it.macAddress
            returnArray['creationDate'] = it.creationDate
            returnArray['expiryDate'] = it.expiryDate
            returnArray['state'] = it.state
            returnArray['hostname'] = it.hostname
            returnArray['serviceUrls'] = it.serviceUrls
            return returnArray
        }

        //clean up hung stuff after restart
        ScriptletBatch.findAll { state == Processable.ProcessState.RUNNING || state == Processable.ProcessState.WAITING }.each {
            it.state = Processable.ProcessState.CANCELED
            it.save(flush:true)
        }

        accessService.warmAccessCacheAsync()
    }

    private setupSecurity() {
        def adminRole = Role.findByAuthority(Role.ROLE_ADMIN) ?: new Role(authority: Role.ROLE_ADMIN).save(failOnError: true)
        def adminUser = Person.findByUsername('admin') ?: new Person(
            username: 'admin',
            password: 'zenboot',
            enabled: true
        ).save(failOnError: true)

        if (!adminUser.authorities.contains(adminRole)) {
            PersonRole.create adminUser, adminRole
        }

        def userRole = Role.findByAuthority(Role.ROLE_USER) ?: new Role(authority: Role.ROLE_USER).save(failOnError: true)
        def zenbootUser = Person.findByUsername('zenboot') ?: new Person(
            username: 'zenboot',
            password: 'zenboot',
            enabled: true
        ).save(failOnError: true)

        if (!zenbootUser.authorities.contains(userRole)) {
            PersonRole.create zenbootUser, userRole
        }

    }

    private setupSanityCheckExposedExecutionZoneAction() {

        // Setup a user capable of calling the Exposed Action afterwards
        def userRole = Role.findByAuthority(Role.ROLE_SANITYCHECK) ?: new Role(authority: Role.ROLE_SANITYCHECK).save(failOnError: true)
        def sanitycheckUser = Person.findByUsername('sanitycheck') ?: new Person(
            username: 'sanitycheck',
            password: 'sanitycheck',
            enabled: true
        ).save(failOnError: true)

        if (!sanitycheckUser.authorities.contains(userRole)) {
            PersonRole.create sanitycheckUser, userRole
        }

        ExecutionZoneType sanityType = ExecutionZoneType.findByName("internal")

        ExecutionZone execZoneSanity = ExecutionZone.findByType(sanityType)
        if (!execZoneSanity) {
            execZoneSanity = new ExecutionZone(type:sanityType, description:"Verify that Zenboot works")
            execZoneSanity.save()
        }

        ExposedExecutionZoneAction exposedAction = ExposedExecutionZoneAction.findByUrl('sanitycheck')
        if (!exposedAction) {
            exposedAction = new ExposedExecutionZoneAction(
                executionZone: execZoneSanity,
                scriptDir : new File("${scriptDirectoryService.getScriptDir(sanityType)}${System.properties['file.separator']}sanitycheck"),
                roles: Role.findAll(),
                url: "sanitycheck",
            )
            exposedAction.save()
        }
    }

    private setupBootstrapExecutionZoneAction() {
        ExecutionZoneType bootstrapType = ExecutionZoneType.findByName("initial")
        ExecutionZone execZoneBootstrap = ExecutionZone.findByType(bootstrapType)
        if (!execZoneBootstrap) {
            execZoneBootstrap = new ExecutionZone(type:bootstrapType, description:"Populate server with default data")
            execZoneBootstrap.save()

            // Execute the action on startup - similar bug to RPI-2167
  	    executorService.submit({
	        this.executionZoneService.createAndPublishExecutionZoneAction(execZoneBootstrap, "bootstrap")
	    } as Callable)
        }
    }

    def destroy = {
    }
}
