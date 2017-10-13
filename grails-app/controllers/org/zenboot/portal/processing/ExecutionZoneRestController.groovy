package org.zenboot.portal.processing

import grails.converters.JSON
import grails.converters.XML
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.slurpersupport.NodeChild
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONException
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.ControllerUtils
import org.zenboot.portal.Host
import org.zenboot.portal.HostState
import org.zenboot.portal.security.Person
import org.zenboot.portal.security.Role
import org.zenboot.portal.Template

class ExecutionZoneRestController extends AbstractRestController implements ApplicationEventPublisherAware{

    def springSecurityService
    def accessService
    def scriptDirectoryService
    def executionZoneService
    def grailsLinkGenerator
    def applicationEventPublisher

    static allowedMethods = [index: "GET" , help: "GET", list: "GET", execute: "POST", listparams: "GET", listactions: "GET", createzone: "POST", exectypes: "GET", execzonetemplate: "GET",
    cloneexecutionzone: "GET", listhosts: "GET", listhoststates: "GET"]

    @Override
    void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.applicationEventPublisher = eventPublisher
    }

    /**
     * The method gives you an overview about the possible rest endpoints and which parameters could be set.
     */
    def help = {
        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String restendpoints = builder.bind {
                    restendpoints {
                        restendpoint {
                            name 'execute'
                            description 'The method execute the specific action of an execution zone based on the parameters one or multiple times. The "quantity" parameter ensure that the user knows the number ' +
                                    'of executions and will be used to compare with the calculated executions. The "runs" parameter could be used to execute scripts multiple times. To do this ' +
                                    'the value of "quantity" has to be the same as "runs". This redundant set of the number of executions prevents the user from unwanted actions. ' +
                                    'For more information look at the documentation in the wiki.'
                            urls {
                                all '/rest/v1/executionzones/{execId}/actions/{execAction}/{quantity}/execute'
                                specific '/rest/v1/executionzones/{execId}/actions/{execAction}/{quantity}/execute?runs={the number of your executions}'
                                exampleurl '/rest/v1/executionzones/1/actions/internal/5/execute?runs=5'
                            }
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                            }
                            execAction {
                                description 'The name of the action.'
                                type 'String'
                                mandatory 'Yes'
                            }
                            quantity {
                                description 'The numbers of wanted executions'
                                type 'Int'
                                mandatory 'Yes'
                            }
                            runs {
                                description 'The numbers of executions'
                                type 'Int'
                                mandatory 'No'
                            }
                            parameters 'Requires json or xml where all the necessary parameters are stored. You can save the result of /listparams to get a working template.'
                        }
                        restendpoint {
                            name 'list'
                            description 'The method returns the execution zones of the user.'
                            urls {
                                all '/rest/v1/executionzones/list'
                                specific '/rest/v1/executionzones/list?execType={execType}'
                                exampleurl '/rest/v1/executionzones/list?execType=internal'
                            }
                            execType {
                                description 'The id or the name of the execution zone type. If not set the method returns all enabled execution zones of the user.'
                                type 'Long (id) or String (name).'
                            }
                        }
                        restendpoint {
                            name 'listparams'
                            description 'The method returns all required parameters on an specific execution zone action. With an additional ?executions= you are able to generate a template for more executions.'
                            urls {
                                url '/rest/v1/executionzones/{execId}/actions/{execAction}/listparams'
                                specific '/rest/v1/executionzones/listparams?executions={integer}'
                                exampleurl '/rest/v1/executionzones/1/actions/sanitycheck/listparams?executions=3'
                            }
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                            }
                            execAction {
                                description 'The name of the action.'
                                type 'String'
                                mandatory 'Yes'
                            }
                        }
                        restendpoint {
                            name 'listactions'
                            description 'The method return all action names of the specific execution zone.'
                            urls {
                                url '/rest/v1/executionzones/$execId/listactions'
                                exampleurl '/rest/v1/executionzones/1/listactions'
                            }
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                                }
                        }
                        restendpoint {
                            name 'exectypes'
                            description 'The method return all available execution zone types.'
                            urls {
                                url '/rest/v1/executionzones/exectypes'
                                exampleurl '/rest/v1/executionzones/exectypes'
                            }
                        }
                        restendpoint {
                            name 'execzonetemplate'
                            description 'The method return a template of an execution zone which could be used to create a new one.'
                            urls {
                                url '/rest/v1/executionzones/execzonetemplate'
                                exampleurl '/rest/v1/executionzones/execzonetemplate'
                            }
                            restriction 'admin only'
                        }
                        restendpoint {
                            name 'createzone'
                            description 'The method return a template of an execution zone which could be used to create a new one.'
                            urls {
                                url '/rest/v1/executionzones/create'
                                exampleurl '/rest/v1/executionzones/create'
                            }
                            restriction 'admin only'
                            parameters 'Requires json or xml where all the necessary parameters are stored. You can save the result of /execzonetemplate to get a working template.'
                        }
                        restendpoint {
                            name 'cloneexecutionzone'
                            description 'The method clones an existing execution zone.'
                            urls {
                                url '/rest/v1/executionzones/{execId}/clone'
                                exampleurl '/rest/v1/executionzones/1/clone'
                            }
                            restriction 'admin only'
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                            }
                        }
                        restendpoint {
                            name 'hosts'
                            description 'The method returns a list of all hosts for a specific execution zone. Could be specified by host state.'
                            urls {
                                all '/rest/v1/executionzones/{execId}/hosts'
                                specific '/rest/v1/executionzones/$execId/hosts?hostState={hostState,hostState...}'
                                exampleurl '/rest/v1/executionzones/1/hosts'
                                exampleurlmulti '/rest/v1/executionzones/1/hosts?hostState=completed,created'
                            }
                            execId {
                                description 'The id of the specific execution zone.'
                                type 'Long'
                                mandatory 'Yes'
                            }
                        }
                        restendpoint {
                            name 'hoststates'
                            description 'The method returns a list of all possible host states'
                            urls {
                                url '/rest/v1/hoststates'
                            }
                        }
                    }
                }

                def xml = XmlUtil.serialize(restendpoints).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {

                def execId = [description: 'The id of the specific execution zone.', type: 'Long', mandatory: 'Yes']
                def execAction = [description: 'The name of the action.', type: 'String', mandatory: 'Yes']
                def execType = [description: 'The id or the name of the execution zone type. If not set the method returns all enabled execution zones of the user.', type: 'Long or String.',
                                mandatory: 'No']

                def executeEndPoint = [description: 'The method execute the specific action of an execution zone based on the parameters one or multiple times. The {quantity} parameter ensure that the user knows the number ' +
                        'of executions and will be used to compare with the calculated executions. The {runs} parameter could be used to execute scripts multiple times. To do this ' +
                        'the value of {quantity} has to be the same as {runs}. This redundant set of the number of executions prevents the user from unwanted actions. ' +
                        'For more information look at the documentation in the wiki.',
                                       parameters: 'Requires json or xml where all the necessary parameters are stored. You can save the result of /listparams to get a working template.',
                                       urls: [all: '/rest/v1/executionzones/{execId}/actions/{execAction}/{quantity}/execute',
                                              specific: '/rest/v1/executionzones/{execId}/actions/{execAction}/{quantity}/execute?runs={the number of your executions}',
                                              exampleurl: '/rest/v1/executionzones/1/actions/internal/5/execute?runs=5'],
                                       quantity: [
                                               description: 'The numbers of wanted executions',
                                               type: 'Int',
                                               mandatory: 'Yes'
                                       ],
                                       runs: [
                                               description: 'The numbers of executions',
                                               type: 'Int',
                                               mandatory: 'No'
                                       ],
                        execId: execId,
                        execAction: execAction
                ]

                def listEndPoint = [description: 'The method returns the execution zones of the user.', execType: execType,
                                    urls: [
                                            all: '/rest/v1/executionzones/list',
                                            specific: '/rest/v1/executionzones/list?execType={execType}',
                                            exampleurl: '/rest/v1/executionzones/list?execType=internal'
                                    ]
                ]

                def listparamsEndPoint = [description: 'The method returns all required parameters on an specific execution zone action. With an additional ?executions= you are able to ' +
                        'generate a template for more executions.', execId: execId, action: execAction,
                                          urls: [
                                              url: '/rest/v1/executionzones/{execId}/actions/{execAction}/listparams',
                                              specific: '/rest/v1/executionzones/listparams?executions={integer}',
                                              exampleurl: '/rest/v1/executionzones/1/actions/sanitycheck/listparams?executions=3'
                                          ]
                ]

                def listactionsEndPoint = [description: 'The method return all action names of the specific execution zone.', execId: execId,
                                           urls: [
                                               url: '/rest/v1/executionzones/$execId/listactions',
                                               exampleurl: '/rest/v1/executionzones/1/listactions'
                                           ]
                ]

                def exectypes = [description: 'The method return all available execution zone types.',
                                 urls: [
                                         url: '/rest/v1/executionzones/exectypes',
                                         exampleurl: '/rest/v1/executionzones/exectypes'
                                 ]
                ]

                def execzonetemplate = [description: 'The method return a template of an execution zone which could be used to create a new one.', restriction: 'admin only',
                                        urls: [
                                                url: '/rest/v1/executionzones/execzonetemplate',
                                                exampleurl: '/rest/v1/executionzones/execzonetemplate'
                                                ]
                ]

                def createzone = [description: 'The method return a template of an execution zone which could be used to create a new one.', restriction: 'admin only',
                                  urls: [
                                      url: '/rest/v1/executionzones/create',
                                      exampleurl: '/rest/v1/executionzones/create'
                                  ]

                ]

                def cloneexecutionzone = [description: 'The method clones an existing execution zone.', restriction: 'admins only', execId: execId,
                                          urls: [
                                              url: '/rest/v1/executionzones/{execId}/clone',
                                              exampleurl: '/rest/v1/executionzones/1/clone'
                                          ]
                ]

                def listhosts = [description: 'The method returns a list of all hosts for a specific execution zone. Could be specified by host state.', execId: execId,
                                 urls: [
                                         all: '/rest/v1/executionzones/{execId}/hosts',
                                         specific: '/rest/v1/executionzones/$execId/hosts?hostState={hostState,hostState...}',
                                         exampleurl: '/rest/v1/executionzones/1/hosts',
                                         exampleurlmulti: '/rest/v1/executionzones/1/hosts?hostState=completed,created'
                                 ]
                ]

                def listhostsstates = [description: 'The method returns a list of all possible host states',
                                       urls: [
                                               url: '/rest/v1/hoststates'
                                       ]
                ]

                render (contentType: "text/json") { restendpoints execute: executeEndPoint, list: listEndPoint, listparams: listparamsEndPoint, listactions: listactionsEndPoint,
                        exectypes: exectypes, execzonetemplate: execzonetemplate, create: createzone, clone: cloneexecutionzone, hosts: listhosts, hoststates: listhostsstates }
            }
        }
    }

    /**
     * Returns a list of enabled execution zones to which the user has access.
     * The list is be more specified if an execType param is set.
     */
    def list = {
        def results
        ExecutionZoneType executionZoneType

        if (params.execType) {
            if (params.long('execType')) {
                executionZoneType = ExecutionZoneType.findById(params.execType as Long)
            } else if (params.execType instanceof String) {
                executionZoneType = ExecutionZoneType.findByName(params.execType as String)
            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'The executionZoneType (execType) has to be a long or a string')
                return
            }
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {

            if (executionZoneType) {
                results = ExecutionZone.findAllByTypeAndEnabled(executionZoneType, true)
            }
            else {
                results = ExecutionZone.findAllByEnabled(true)
            }
        }
        else {

            List<ExecutionZone> executionZones = new ArrayList<ExecutionZone>()

            Map executionZonesIDs
            Long currentUserID = springSecurityService.getCurrentUserId() as Long

            if (accessService.accessCache[currentUserID]) {
                executionZonesIDs = accessService.accessCache[currentUserID].findAll {it.value}
            }
            else {
                accessService.refreshAccessCacheByUser(Person.findById(currentUserID))
                executionZonesIDs = accessService.accessCache[currentUserID].findAll {it.value}
            }

            executionZonesIDs.each {
                executionZones.add(ExecutionZone.get(it.key as Long))
            }

            if (executionZoneType) {
                results = new ArrayList<ExecutionZone>()

                executionZones.each {zone ->
                    if (zone.type == executionZoneType && zone.enabled) {
                        results.add(zone)
                    }
                }
            }
            else if (executionZoneType == null && params.execType) {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'The requested execution zone type does not exist.')
                return
            }
            else {
                results = executionZones.findAll() {it.enabled}
            }
        }

        def executionZones = results.collect {[execId: it.id, execType: it.type.name, execDescription: it.description]}

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String zones = builder.bind {
                    executionzones {
                        executionZones.each { execZone ->
                            executionzone {
                                execId execZone.execId
                                execType execZone.execType
                                execDescription execZone.execDescription
                            }
                        }
                    }
                }

                def xml = XmlUtil.serialize(zones).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                def zones = [:]
                zones.put('executionZones', executionZones)

                render(contentType: "text/json") { zones } as JSON
            }
        }
    }

    /**
     * The method returns a list of all required parameters of an execution zone. It is possible to generate a template
     * for multiple execution while adding ?executions={number of your planed executions} to the url
     */
    def listparams = {
        ExecutionZone executionZone
        String actionName

        if (params.execId && params.execId.isInteger()) {
            if (ExecutionZone.findById(params.execId as Long)) {
                executionZone = ExecutionZone.findById(params.execId as Long)
            } else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone with id ${params.execId} not found.')
                return
            }
        } else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone id (execId) not set.')
            return
        }

        if (params.execAction) {
            actionName = params.execAction
        } else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'Action name (execAction) not set.')
            return
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) || userHasAccess(executionZone)) {

            File stackDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" + actionName)

            if (!isValidScriptDir(stackDir)) {
                return
            }

            def paramsSet = executionZoneService.getExecutionZoneParameters(executionZone, stackDir)
            int numberofExecutions = 1

            if (params.executions && params.executions.isInteger()) {
                numberofExecutions = params.int('executions')
            }

            withFormat {
                xml {
                    def builder = new StreamingMarkupBuilder()
                    builder.encoding = 'UTF-8'

                    String executions = builder.bind {
                        executions {
                            numberofExecutions.times {
                                execution {
                                    parameters {
                                        paramsSet.each { param ->
                                            parameter {
                                                parameterName param.name
                                                parameterValue param.value
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    def xml = XmlUtil.serialize(executions).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                    xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                    render contentType: "text/xml", xml
                }
                json {
                    def responseParams = [:]
                    responseParams.put('parameters', paramsSet.collect {
                        ['parameterName': it.name, 'parameterValue': it.value]
                    })
                    def executions = [:]
                    def executionList = []
                    numberofExecutions.times {
                        executionList.add(responseParams)
                    }
                    executions.put('executions', executionList)
                    render(contentType: "text/json") { executions } as JSON
                }
            }
        } else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'This user has no permission to request the parameter for this zone.')
        }
    }


        /**
     * This method returns a list of all possible actions for the executionzone.
     */
    def listactions = {

        ExecutionZone executionZone
        File scriptDir

        if (params.execId && params.execId.isInteger()) {
            if(ExecutionZone.findById(params.execId as Long)){
                executionZone = ExecutionZone.findById(params.execId as Long)
            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone with id ${params.execId} not found.')
                return
            }
        }
        else {
            this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone id (execId) not set.')
            return
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            scriptDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" )
        }
        else if (userHasAccess(executionZone)) {

            scriptDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                    + "/" + executionZone.type.name + "/scripts/" )
        }
        else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'This user has no permission to request the actions for this zone.')
            return
        }

        if(!isValidScriptDir(scriptDir)) {
            return
        }

        File[] scriptDirFiles = scriptDir.listFiles()

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String execActions = builder.bind {
                    execActions {
                        scriptDirFiles.each {
                            execAction it.name
                        }
                    }
                }
                def xml = XmlUtil.serialize(execActions).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                def dirContent = [:]
                dirContent.put('execActions', scriptDirFiles.collect {it.name})

                render (contentType: "text/json") { dirContent } as JSON
            }
        }
    }

    /**
     * execTypes returns a list of all existing executionZoneTypes.
     */
    def exectypes = {
        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String executionZoneTypes = builder.bind {
                    executionZoneTypes {
                        ExecutionZoneType.list().sort().each {
                            executionZoneType it
                        }
                    }
                }

                def xml = XmlUtil.serialize(executionZoneTypes).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                render (contentType: "text/json") { ExecutionZoneType.list().sort() } as JSON
            }
        }
    }

    /**
     * This method returns a xml or json template to create an execution zone.
     */
    def execzonetemplate = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            String[] nonrelevant_Properties = ['actions', 'creationDate', 'hosts', 'templates', 'processingParameters']
            DefaultGrailsDomainClass d = new DefaultGrailsDomainClass(ExecutionZone.class)
            GrailsDomainClassProperty[] properties = d.getPersistentProperties()

            withFormat {
                xml {
                    def builder = new StreamingMarkupBuilder()
                    builder.encoding = 'UTF-8'

                    String executionZone = builder.bind {
                        executionZone {
                            executionZoneProperties {
                                properties.each { property ->
                                    if (!nonrelevant_Properties.contains(property.name)) {
                                        executionZoneProperty {
                                            propertyName property.name
                                            propertyValue ''
                                        }
                                    }
                                }
                            }
                            processingParameters {
                                parameter {
                                    parameterName ''
                                    parameterValue ''
                                    parameterDescription ''
                                    parameterExposed ''
                                    parameterPublished ''
                                }
                            }
                        }
                    }

                    def xml = XmlUtil.serialize(executionZone).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                    xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                    render contentType: "text/xml", xml
                }
                json {
                    def executionzonetemplate = [:]

                    def executionZoneProperties = properties.findAll { !nonrelevant_Properties.contains(it.name) }
                    executionzonetemplate.put('executionZoneProperties', executionZoneProperties.collect {
                        [propertyName: it.name, propertyValue: '']
                    })
                    executionzonetemplate.put('processingParameters', [[parameterName: '', parameterValue: '', parameterDescription: '', parameterExposed: '', parameterPublished: '']])

                    render(contentType: 'text/json') { executionzonetemplate } as JSON
                }
            }
        }
        else {
            this.renderRestResult(HttpStatus.UNAUTHORIZED, null, null, 'You have no permissions to request a execution zone template.')
        }
    }

    /**
     * This method creates a new execution zone.
     */
    def createzone = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            Boolean hasError = Boolean.FALSE
            HashMap parameters = new HashMap()
            Map processingParams = [:]

            request.withFormat {
                xml {
                    def xml
                    try {
                        xml = request.XML
                    }
                    catch (ConverterException e) {
                        this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, e.message)
                        hasError = Boolean.TRUE
                        return
                    }

                    def xmlExecutionZoneProperties = xml[0].children.findAll { it.name == 'executionZoneProperties' }

                    xmlExecutionZoneProperties.each { node ->
                        node.children.each { innerNode ->
                            def name = ''
                            def value = ''
                            innerNode.children.each {
                                if (it.name == 'propertyName') {
                                    name = it.text()
                                } else if (it.name == 'propertyValue') {
                                    value = it.text()
                                }
                            }
                            parameters[name] = value
                        }
                    }

                    def xmlExecutionZoneParameters = xml[0].children.findAll { it.name == 'processingParameters' }

                    if (xmlExecutionZoneParameters.size() != 0) {

                        String[] keys = new String[xmlExecutionZoneParameters.size()]
                        String[] values = new String[xmlExecutionZoneParameters.size()]
                        String[] descriptions = new String[xmlExecutionZoneParameters.size()]
                        String[] exposed = new String[xmlExecutionZoneParameters.size()]
                        String[] published = new String[xmlExecutionZoneParameters.size()]

                        xmlExecutionZoneParameters.eachWithIndex { processingParameters, index ->

                            if (processingParameters.children.size() == 0) {
                                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Processing parameters are empty')
                                hasError = Boolean.TRUE
                            }

                            processingParameters.children.each { parameter ->
                                parameter.children.each {

                                    if (it.text() == '') {
                                        this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The value of a processing parameter cannot be empty')
                                        hasError = Boolean.TRUE
                                    }

                                    if ('parameterName' == it.name) {
                                        keys[index] = it.text()
                                    } else if ('parameterValue' == it.name) {
                                        values[index] = it.text()
                                    } else if ('parameterDescription' == it.name) {
                                        descriptions[index] = it.text()
                                    } else if ('parameterExposed' == it.name) {
                                        String exposedText = it.text()

                                        if ('true' == exposedText.toLowerCase() || 'false' == exposedText.toLowerCase()) {
                                            exposed[index] = exposedText.toLowerCase()
                                        } else if (exposedText.isEmpty()) {
                                            exposed[index] = 'false'
                                        } else {
                                            renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Invalid value. parameterExposed has to be true or false.')
                                            hasError = Boolean.TRUE
                                            return
                                        }
                                    } else if ('parameterPublished' == it.name) {
                                        String publishedText = it.text()

                                        if ('true' == publishedText.toLowerCase() || 'false' == publishedText.toLowerCase()) {
                                            published[index] = publishedText.toLowerCase()
                                        } else if (publishedText.isEmpty()) {
                                            published[index] = 'false'
                                        } else {
                                            renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Invalid value. parameterPublished has to be true or false.')
                                            hasError = Boolean.TRUE
                                            return
                                        }
                                    }
                                }
                            }
                        }

                        processingParams.put('parameters.key', keys)
                        processingParams.put('parameters.value', values)
                        processingParams.put('parameters.exposed', exposed)
                        processingParams.put('parameters.published', published)
                        processingParams.put('parameters.description', descriptions)
                    }
                }
                json {
                    String text = request.getReader().text
                    def json

                    try {
                        json = new JSONObject(text)
                    }
                    catch (JSONException e) {
                        this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, e.getMessage())
                        hasError = Boolean.TRUE
                        return
                    }

                    if (json.executionZoneProperties) {
                        json.executionZoneProperties.each {
                            parameters[it.propertyName] = it.propertyValue
                        }
                    }

                    if (json.processingParameters && json.processingParameters.size() != 0) {

                        String[] keys = new String[json.processingParameters.size()]
                        String[] values = new String[json.processingParameters.size()]
                        String[] descriptions = new String[json.processingParameters.size()]
                        String[] exposed = new String[json.processingParameters.size()]
                        String[] published = new String[json.processingParameters.size()]

                        json.processingParameters.eachWithIndex { parameter, int index ->
                            parameter.each {
                                if(it.value == ''){
                                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The value of a processing parameter cannot be empty')
                                    hasError = Boolean.TRUE
                                }
                                if ('parameterName' == it.key) {
                                    keys[index] = it.value
                                } else if ('parameterValue' == it.key) {
                                    values[index] = it.value
                                } else if ('parameterDescription' == it.key) {
                                    descriptions[index] = it.value
                                } else if ('parameterExposed' == it.key) {
                                    String exposedText = it.value

                                    if ('true' == exposedText.toLowerCase() || 'false' == exposedText.toLowerCase()) {
                                        exposed[index] = exposedText.toLowerCase()
                                    } else if (exposedText.isEmpty()) {
                                        exposed[index] = 'false'
                                    } else {
                                        renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Invalid value. parameterExposed has to be true or false.')
                                        hasError = Boolean.TRUE
                                        return
                                    }
                                } else if ('parameterPublished' == it.key) {
                                    String publishedText = it.value

                                    if ('true' == publishedText.toLowerCase() || 'false' == publishedText.toLowerCase()) {
                                        published[index] = publishedText.toLowerCase()
                                    } else if (publishedText.isEmpty()) {
                                        published[index] = 'false'
                                    } else {
                                        renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Invalid value. parameterPublished has to be true or false.')
                                        hasError = Boolean.TRUE
                                        return
                                    }
                                }
                            }
                        }

                        processingParams.put('parameters.key', keys)
                        processingParams.put('parameters.value', values)
                        processingParams.put('parameters.exposed', exposed)
                        processingParams.put('parameters.published', published)
                        processingParams.put('parameters.description', descriptions)
                    }
                }
            }

            if (hasError) {
                return
            }

            if (parameters['type'] instanceof String) {
                parameters['type'] = ExecutionZoneType.findByName(parameters['type'] as String).id
            }

            ExecutionZone newExecutionZone = new ExecutionZone(parameters)

            if(processingParams) {
                ControllerUtils.synchronizeProcessingParameters(ControllerUtils.getProcessingParameters(processingParams), newExecutionZone)
            }

            if (!newExecutionZone.save(flush: true)) {
                renderRestResult(HttpStatus.INTERNAL_SERVER_ERROR, null, null, 'ERROR. ExecutionZone could not be saved. '
                        + newExecutionZone.errors.allErrors.join(' \n'))
            }

            withFormat {
                xml {
                    render newExecutionZone as XML
                }
                json {
                    JSON.use('deep')
                    render newExecutionZone as JSON
                }
            }
        }
        else {
            this.renderRestResult(HttpStatus.UNAUTHORIZED, null, null, 'You have no permissions to create an execution zone.')
        }
    }

    /**
     * This method clones an exiting execution zone.
     */
    def cloneexecutionzone = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {

            ExecutionZone executionZone
            ExecutionZone clonedExecutionZone

            if (params.execId && params.execId.isInteger()) {
                executionZone = ExecutionZone.findById(params.execId as Long)
            }
            else {
                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The parameter execId to find the execution zone by id is missing.')
                return
            }

            if (executionZone) {
                clonedExecutionZone = new ExecutionZone(executionZone.properties)
                clonedExecutionZone.actions = []
                clonedExecutionZone.hosts = []
                clonedExecutionZone.processingParameters = [] as SortedSet
                clonedExecutionZone.templates = [] as SortedSet

                executionZone.processingParameters.each {
                    ProcessingParameter clonedParameter = new ProcessingParameter(it.properties)
                    clonedExecutionZone.processingParameters.add(clonedParameter)
                }

                executionZone.templates.each {
                    Template template = new Template(it.properties)
                    clonedExecutionZone.templates.add(template)
                }

            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'The execution zone for id ' + params.execId + ' could not be found.')
                return
            }

            if (!clonedExecutionZone.save(flush: true)) {
                renderRestResult(HttpStatus.INTERNAL_SERVER_ERROR, null, null, 'ERROR. ExecutionZone could not be saved. '
                        + clonedExecutionZone.errors.allErrors.join(' \n'))
            }

            withFormat {
                xml {
                    render clonedExecutionZone as XML
                }
                json {
                    JSON.use('deep')
                    render clonedExecutionZone as JSON
                }
            }
        }
        else {
            this.renderRestResult(HttpStatus.UNAUTHORIZED, null, null, 'You have no permissions to clone execution zones.')
        }
    }

    /**
     * This method execute actions in zenboot. The 'quantity' parameter ensure that the caller of this method is aware
     * of the number of runs. The 'runs' parameter execute the same action 'runs' times. To execute an action multiple
     * times without the 'runs' parameters add executions to the data you send (see listparams). If this is done, the
     * action will be executed the number of 'executions' times.
     *
     * For more detailed information read the documentation in the wiki.
     */
    def execute = {
        ExecutionZone executionZone
        String executionZoneAction
        def referralsCol = []
        List<Map> execution = new ArrayList<Map>()
        Boolean hasError = Boolean.FALSE
        int runs = 1
        int quantity

        if (params.execId && params.execId.isInteger()) {
            if(ExecutionZone.findById(params.execId as Long)){
                executionZone = ExecutionZone.findById(params.execId as Long)
            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone with id ' + ${params.execId} + ' not found.')
                return
            }
        }
        else {
            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'ExecutionZone id (execId) not set.')
            return
        }

        if (params.execAction) {
            executionZoneAction = params.execAction
        }
        else {
            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Action name (execAction) not set.')
            return
        }

        if(params.quantity) {
            if(params.quantity.isInteger()) {
                quantity = params.int('quantity')
            }
            else {
                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The quantity has to be an integer.')
            }
        }
        else {
            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The quantity which ensure that the number of executions is like you expect is missing.')
            return
        }

        if(params.runs) {
            if(params.runs.isInteger()) {
                runs = params.int('runs')
            }
        }

        File stackDir = new File(scriptDirectoryService.getZenbootScriptsDir().getAbsolutePath()
                + "/" + executionZone.type.name + "/scripts/" + executionZoneAction)

        if(!isValidScriptDir(stackDir)) {
            return
        }

        Set<ProcessingParameter> origin_params = executionZoneService.getExecutionZoneParameters(executionZone, stackDir)

        // get data from incoming json or xml
        request.withFormat {
            xml {
                NodeChild xml
                try {
                    xml = request.XML as NodeChild
                }
                catch (ConverterException e) {
                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, e.message)
                    hasError = Boolean.TRUE
                    return
                }

                def executions = xml.childNodes().findAll { it.name == 'execution'}

                executions.each { exec ->

                    Map<String, List> parameters =[:]

                    origin_params.each { zoneparam ->
                        parameters[zoneparam.name] = exec.childNodes().find {it.name == 'parameters'}.childNodes().findAll {it.name == 'parameter'}.find{it.childNodes().find{it.text() == zoneparam.name}}.children[1].text()

                        if (!parameters[zoneparam.name] && zoneparam.value != '') {
                            parameters[zoneparam.name] = zoneparam.value
                        }
                    }
                    execution.add(parameters)
                }

            }
            json {
                String text = request.getReader().text
                def json

                try {
                   json = new JSONObject(text)
                }
                catch (JSONException e) {
                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, e.getMessage())
                    hasError = Boolean.TRUE
                    return
                }

                def executions = json.executions

                executions?.parameters?.each {exec ->
                    Map<String, List> parameters =[:]
                    origin_params.each { zoneparam ->

                        parameters[zoneparam.name] = exec.find{it.parameterName == zoneparam.name}?.parameterValue

                        if (!parameters[zoneparam.name] && zoneparam.value != '') {
                            parameters[zoneparam.name] = zoneparam.value
                        }
                    }
                    execution.add(parameters)
                }
            }
        }

        if (hasError) {
            return
        }

        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN) || userHasAccess(executionZone)) {

            execution.each {
                if (it.any { key, value -> value == '' || value == null}) {
                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'No empty parameter values allowed - please check your data.')
                    return
                }
            }

            if(!SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
                // check if it allowed to change the parameters

                execution.each { exec ->
                    origin_params.each {
                        ProcessingParameter org_parameter = new ProcessingParameter(name: it.name, value: it.value.toString())

                        List<ProcessingParameter> testParamsList = []

                        if (exec[it.name]) {
                            testParamsList.add(new ProcessingParameter(name: it.name, value: exec[it.name]))
                        } else {
                            if (it.value.toString()) {
                                testParamsList.add(new ProcessingParameter(name: it.name, value: it.value.toString()))
                            } else {
                                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'No empty parameter values allowed - please check your data. Empty parameter: ' + it.name)
                                return
                            }
                        }

                        testParamsList.each { new_parameter ->
                            if (org_parameter.value != new_parameter.value && !executionZoneService.actionParameterEditAllowed(new_parameter, org_parameter)) {
                                //not allowed to change this param so change back
                                exec[org_parameter.name] = org_parameter.value
                            }
                        }
                    }
                }
            }

            //get the name of all parameters which are not defined
            def names = origin_params.findAll {it.value == ''}.name
            int numberOfExecutions

            if (execution.size() == 0) {
                if (names.size() == 0) {
                    numberOfExecutions = runs
                    //prepare map and fill with fix values
                    def origin_parameters = [:]
                    origin_params.each {
                        origin_parameters[it.name] = it.value
                    }
                    //add it to execution
                    execution.add(origin_parameters)
                }
            }
            else if (execution.size() == 1) {
                if (params.runs) {
                    numberOfExecutions = runs
                }
                else {
                    numberOfExecutions = 1
                }
            }
            else {
                numberOfExecutions = execution.size()
            }

            if (numberOfExecutions != quantity) {
                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'The calculated number of executions does not match your expection. Calculated number of ' +
                        'Executions: ' + numberOfExecutions + '. Quantity: ' + quantity + '. Please check your data.')
                return
            }

            numberOfExecutions.times { int idx ->
                Map singleParams
                if (execution.size() > idx) {
                    singleParams = execution[idx]
                }
                else {
                    singleParams = execution.last()
                }

                // create action with zone, stackdir and parameters
                ExecutionZoneAction action = executionZoneService.createExecutionZoneAction(executionZone, stackDir, singleParams)
                //publish event to start execution
                applicationEventPublisher.publishEvent(new ProcessingEvent(action, springSecurityService.currentUser, "REST-call run"))
                URI referral = new URI(grailsLinkGenerator.link(absolute: true, controller: 'executionZoneAction', action: 'rest', params: [id: action.id]))
                referralsCol.add(referral)
            }

            withFormat {
                xml {
                    def builder = new StreamingMarkupBuilder()
                    builder.encoding = 'UTF-8'

                    String executedActions = builder.bind {
                        executedActions {
                            execId executionZone.id
                            execAction executionZoneAction
                            referrals {
                                referralsCol.each {
                                    referral it.path
                                }
                            }
                        }
                    }

                    def xml = XmlUtil.serialize(executedActions).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                    xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                    render contentType: "text/xml", xml
                }
                json {
                    def executedActions = [:]

                    executedActions.put('execId', executionZone.id)
                    executedActions.put('execAction', executionZoneAction)
                    executedActions.put('referrals', referralsCol.collect {it.path})
                    render executedActions as JSON
                }
            }
        }
        else {
            renderRestResult(HttpStatus.FORBIDDEN, null, null, 'This user has no permission to execute this execution Zone.')
        }
    }

    /**
     * The method returns the hosts. The result could be more specific if 'hostState' parameter is added to the request url e.g. ?hostState=completed to return all
     * hosts with the state completed. It is also possible to add multiple states. In this case call the url with ?hostState=completed,created . Furthermore it is possible
     * to restrict the search for a single execution zone. In this case add e.g. ?execId=104 to the url. You also can use both e.g. ?execId=104&hostState=completed,created .
     */
    def listhosts = {
        ExecutionZone executionZone

        if (params.execId && params.execId.isInteger()) {
            if(ExecutionZone.findById(params.execId as Long)){
                executionZone = ExecutionZone.findById(params.execId as Long)
            }
            else {
                this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'ExecutionZone with id ' + params.execId + ' not found.')
                return
            }
        }

        def hostsFromZone = []

        if (params.hostState) {
            def hostStates = []

            if (params.hostState.contains(',')){
                hostStates = params.hostState.split(',')
            }
            else {
                hostStates.add(params.hostState as String)
            }

            hostStates.each {
                String state = it as String
                state = state.toUpperCase()
                if (HostState.values().find { it.toString() == state }) {
                    HostState hostState = HostState.valueOf(state)
                    if (executionZone) {
                        hostsFromZone.addAll(Host.findAllByExecZoneAndState(executionZone, hostState))
                    }
                    else {
                        hostsFromZone.addAll(Host.findAllByState(hostState))
                    }
                } else {
                    this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No hoststate found for state: ' + params.hostState)
                    return
                }
            }
        }
        else {
            if (executionZone) {
                hostsFromZone = Host.findAllByExecZone(executionZone)
            }
            else {
                hostsFromZone = Host.list()
            }
        }

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String foundHosts = builder.bind {
                    hosts {
                        hostsFromZone.each { hostElement ->
                            host {
                                hostname hostElement.hostname.toString()
                                cname hostElement.cname
                                hoststate hostElement.state.toString()
                                ipadress hostElement.ipAddress
                                serviceUrls {
                                    hostElement.serviceUrls.each { singleurl ->
                                        serviceUrl singleurl.url
                                    }
                                }
                            }
                        }
                    }
                }

                def xml = XmlUtil.serialize(foundHosts).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                Map hosts = [:]
                List host = hostsFromZone.collect{[hostname: it.hostname.toString(), cname: it.cname, hoststate: it.state.toString(), ipadress: it.ipAddress, serviceUrls: [it.serviceUrls.collect{it.url}]]}
                hosts.put('hosts', host)
                render hosts as JSON
            }
        }
    }


    /**
     * The method returns a list of all existing states of a host.
     */
    def listhoststates = {
        def hostStates = HostState.findAll().collect{it.toString()}

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'

                String states = builder.bind {
                    hoststates {
                        hostStates.each {
                            hoststate it
                        }
                    }
                }

                def xml = XmlUtil.serialize(states).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                render contentType: "text/xml", xml
            }
            json {
                Map jsonhoststates = [:]
                jsonhoststates.put('hoststates', hostStates)
                render jsonhoststates as JSON
            }
        }
    }

    /**
     * Check if the user is already in the cache and has access to the requested execution zone.
     * @param executionZone - the execution zone which has to be checked for access.
     * @return true if the user has access otherwise false.
     */
    private Boolean userHasAccess(ExecutionZone executionZone) {
        Long currentUserId = springSecurityService.getCurrentUserId() as Long
        return accessService.accessCache[currentUserId] != null ?
                accessService.accessCache[currentUserId][executionZone.id] :
                accessService.userHasAccess(executionZone)
    }

    /**
     * Check if the script file exists. If not it renders NOT_FOUND with the error message that the script file does not exists.
      * @param scriptDir the script File object.
     * @return true if exists otherwise false.
     */
    private Boolean isValidScriptDir(File scriptDir) {
        if (scriptDir.exists()) {
            return Boolean.TRUE
        }
        else {
            renderRestResult(HttpStatus.NOT_FOUND, null, null, 'The script with path ' + scriptDir.getPath() + ' does not exists.')
        }
        return Boolean.FALSE
    }
}