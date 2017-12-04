package org.zenboot.portal.processing

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.springframework.http.HttpStatus
import org.zenboot.portal.AbstractRestController
import org.zenboot.portal.NotificationType
import org.zenboot.portal.UserNotification
import org.zenboot.portal.security.Role

class UserNotificationRestController extends AbstractRestController {

    static allowedMethods = [listusernotifications: "GET", editusernotification: "PUT", createusernotification: "POST", deleteusernotification: "DELETE"]

    def springSecurityService

    /**
     * The method returns a list of user notifications. It is possible to specify the enabled param to get all enabled or disabled user notifications. If the enabled parameter is not set, the method
     * return all available user notifications.
     */
    @Secured(['permitAll'])
    def listusernotifications() {
        List<UserNotification> userNotificationsData = []
        if (params.enabled != null) {
            userNotificationsData = UserNotification.findAllByEnabled(params.enabled.toBoolean())
        } else {
            userNotificationsData = UserNotification.getAll()
        }

        withFormat {
            xml {
                def builder = new StreamingMarkupBuilder()
                builder.encoding = 'UTF-8'
                String userNotificationsXML = builder.bind {
                    usernotifications {
                        userNotificationsData.each { UserNotification userNotificationData ->
                            usernotification {
                                id userNotificationData.id
                                replacemewithmessage userNotificationData.message
                                notificationtype userNotificationData.type.name()
                                enabled userNotificationData.enabled
                            }
                        }
                    }
                }
                def xml = XmlUtil.serialize(userNotificationsXML).replace('<?xml version="1.0" encoding="UTF-8"?>', '<?xml version="1.0" encoding="UTF-8"?>\n')
                xml = xml.replaceAll('<([^/]+?)/>', '<$1></$1>')
                xml = xml.replaceAll('replacemewithmessage', 'message')
                render contentType: "text/xml", xml
            }
            json {
                def usernotifications = []
                userNotificationsData.each { UserNotification userNotificationData ->
                    def usernotification = [:]
                    usernotification.put('id', userNotificationData.id)
                    usernotification.put('message', userNotificationData.message)
                    usernotification.put('notificationtype', userNotificationData.type.name())
                    usernotification.put('enabled', userNotificationData.enabled)
                    usernotifications.add(usernotification)
                }
                render usernotifications as JSON
            }
        }
    }

    /**
     * The method overrides the values of an existing user notification. Admin permissions are required to edit an user notification.
     */
    def editusernotification = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            Boolean hasError = Boolean.FALSE
            UserNotification userNotificationData
            if (params.notificationId && params.notificationId.isInteger()) {
                userNotificationData = UserNotification.findById(params.notificationId as Long)
                if (userNotificationData) {
                    // do nothing
                } else {
                    this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No UserNotification with id ' + params.notificationId + ' found.')
                    return
                }
            } else {
                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'UserNotificationId (notificationId) not set or wrong format.')
                return
            }

            request.withFormat {
                xml {
                    def xml = parseRequestDataToXML(request)

                    if (xml) {
                        def xmlParameters = xml[0].children
                        def parameters = [:]

                        xmlParameters.each { node ->
                            def name = ''
                            def value = ''
                            node.children.each { innerNode ->
                                if (innerNode.name == 'parameterName') {
                                    name = innerNode.text()
                                } else if (innerNode.name == 'parameterValue') {
                                    value = innerNode.text()
                                }
                            }
                            parameters.put(name, value)
                        }

                        parameters.each {
                            if (userNotificationData.hasProperty(it.key)) {
                                userNotificationData.properties[it.key] = it.value
                            } else {
                                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Property ' + it.key + ' not exists for UserNotifications.')
                                hasError = Boolean.TRUE
                                return
                            }
                        }
                    } else { hasError = Boolean.TRUE }
                }
                json {
                    def json = parseRequestDataToJSON(request)
                    if (json) {
                        if (json.parameters) {
                            json.parameters.each {
                                if (userNotificationData.hasProperty(it.parameterName)) {
                                    userNotificationData.properties[it.parameterName] = it.parameterValue
                                } else {
                                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Property ' + it.parameterName + ' not exists for UserNotifications.')
                                    hasError = Boolean.TRUE
                                    return
                                }
                            }
                        }
                    } else { hasError = Boolean.TRUE }
                }
            }

            if (hasError) {
                return
            }

            if (userNotificationData.save(flush: true)) {
                this.renderRestResult(HttpStatus.OK, null, null, 'Notification changed.')
            } else {
                this.renderRestResult(HttpStatus.INTERNAL_SERVER_ERROR, null, null, 'An error occurred while saving the usernotification.')
            }
        } else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'Only admins are allowed to request these resources.')
        }
    }

    /**
     * The method creates a new user notification. Admin permissions are required to create a new one.
     */
    def createusernotification = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            Boolean hasError = Boolean.FALSE

            request.withFormat {
                xml {
                    def xml = parseRequestDataToXML(request)
                    if (xml) {
                        def xmlParameters = xml[0].children
                        def parameters = [:]

                        xmlParameters.each { node ->
                            def name = ''
                            def value = ''
                            node.children.each { innerNode ->
                                if (innerNode.name == 'parameterName') {
                                    name = innerNode.text()
                                } else if (innerNode.name == 'parameterValue') {
                                    value = innerNode.text()
                                }
                            }
                            parameters.put(name, value)
                        }

                        if (parameters || parameters.every { it.value != '' && it.value != null }) {
                            if (parameters.containsKey('enabled') && parameters.containsKey('message') && parameters.containsKey('notificationtype')) {
                                if (NotificationType.values().any {
                                    it.name().toLowerCase() == parameters['notificationtype'].toString().toLowerCase()
                                }) {
                                    UserNotification newUserNotification = new UserNotification(enabled: parameters['enabled'], message: parameters['message'],
                                            type: NotificationType.valueOf(parameters['notificationtype'] as String))

                                    if (!newUserNotification.save(flush: true)) {
                                        this.renderRestResult(HttpStatus.INTERNAL_SERVER_ERROR, null, null, 'An error occured while saving the new user notification. Please check your data.')
                                        hasError = Boolean.TRUE
                                    }
                                } else {
                                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'No type of UserNotification found for value ' + parameters['notificationtype'] +
                                            '. Please check documentation.')
                                    hasError = Boolean.TRUE
                                    return
                                }
                            } else {
                                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Parameter missing. Please check documentation.')
                                hasError = Boolean.TRUE
                                return
                            }
                        } else {
                            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'No data received or wrong data structure. Please check documentation.')
                            hasError = Boolean.TRUE
                            return
                        }
                    } else { hasError = Boolean.TRUE }
                }
                json {
                    def json = parseRequestDataToJSON(request)
                    if (json) {
                        def parameters = [:]

                        if (json.parameters) {
                            json.parameters.each {
                                parameters[it.parameterName] = it.parameterValue
                            }
                        }

                        if (parameters || parameters.every { it.value != '' && it.value != null }) {
                            if (parameters.containsKey('enabled') && parameters.containsKey('message') && parameters.containsKey('notificationtype')) {
                                if (NotificationType.values().any {
                                    it.name().toLowerCase() == parameters['notificationtype'].toString().toLowerCase()
                                }) {
                                    UserNotification newUserNotification = new UserNotification(enabled: parameters['enabled'], message: parameters['message'],
                                            type: NotificationType.valueOf(parameters['notificationtype'] as String))

                                    if (!newUserNotification.save(flush: true)) {
                                        this.renderRestResult(HttpStatus.INTERNAL_SERVER_ERROR, null, null, 'An error occured while saving the new user notification. Please check your data.')
                                        hasError = Boolean.TRUE
                                    }
                                } else {
                                    this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'No type of UserNotification found for value ' + parameters['notificationtype'] +
                                            '. Please check documentation.')
                                    hasError = Boolean.TRUE
                                    return
                                }
                            } else {
                                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'Parameter missing. Please check documentation.')
                                hasError = Boolean.TRUE
                                return
                            }
                        } else {
                            this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'No data received or wrong data structure. Please check documentation.')
                            hasError = Boolean.TRUE
                        }
                    } else { hasError = Boolean.TRUE }
                }
            }
            if (hasError) {
                return
            }
            this.renderRestResult(HttpStatus.CREATED, null, null, 'Notification created.')
        } else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'Only admins are allowed to request these resources.')
        }
    }

    /**
     * The method deletes an user notification by id. Admin permissions are required to delete an user notification.
     */
    def deleteusernotification = {
        if (SpringSecurityUtils.ifAllGranted(Role.ROLE_ADMIN)) {
            if (params.notificationId) {
                UserNotification userNotification = UserNotification.findById(params.notificationId as Long)
                if (userNotification) {
                    try {
                        userNotification.delete(flush: true)
                        this.renderRestResult(HttpStatus.OK, null, null, 'UserNotification with id ' + params.notificationId + ' deleted.')
                    } catch (Exception e) {
                        this.renderRestResult(HttpStatus.INTERNAL_SERVER_ERROR, null, null, 'An error occurred while deleting the usernotification: ' + e.getMessage())
                    }
                } else {
                    this.renderRestResult(HttpStatus.NOT_FOUND, null, null, 'No UserNotification with id ' + params.notificationId + ' found.')
                }
            } else {
                this.renderRestResult(HttpStatus.BAD_REQUEST, null, null, 'UserNotificationId (notificationId) not set or wrong format.')
            }
        } else {
            this.renderRestResult(HttpStatus.FORBIDDEN, null, null, 'Only admins are allowed to request these resources.')
        }
    }
}
