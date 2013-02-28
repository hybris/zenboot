package org.zenboot.portal

import groovy.text.SimpleTemplateEngine

class HostService {

    def grailsApplication

    Environment getEnvironment() {
        try {
            return Environment.valueOf(this.grailsApplication.config.zenboot.environment.toString().toUpperCase())
        } catch (IllegalArgumentException exc) {
            throw new ZenbootException("Environment ${this.grailsApplication.config.zenboot.environment.toString()} is not valid. Please use ${Environment.values()}", exc)
        }
    }

    int countAvailableHosts() {
        int existingHosts = Host.countByStateNotEqual(HostState.DELETED)
        return (grailsApplication.config.zenboot.host.instances.poolSize.toInteger() - existingHosts)
    }

    Date getExpiryDate() {
        int lifetime = grailsApplication.config.zenboot.host.instances.lifetime.toInteger()
        if (lifetime > 0) {
            GregorianCalendar calendar = GregorianCalendar.getInstance()
            calendar.add(GregorianCalendar.SECOND, lifetime)
            return calendar.getTime()
        }
        return null
    }

    Hostname nextHostName(String namePattern) {
        return this.nextHostName(this.getEnvironment(), namePattern)
    }

    Hostname nextHostName(Environment environment, String namePattern) {
        Hostname hostname = new Hostname()
        hostname.save() //initial save to get an unique id
        def binding = [
                    env: environment.acronym,
                    vmId: hostname.id
                ]
        def template = new SimpleTemplateEngine().createTemplate(namePattern)
        hostname.name = template.make(binding).toString()
        hostname.save() //hostname is now complete: update it
        return hostname
    }

    Hostname nextHostName() {
        return this.nextHostName(this.getEnvironment(), this.grailsApplication.config.zenboot.host.instances.defaultNamePattern.toString())
    }

    Hostname nextHostName(Environment environment) {
        return this.nextHostName(environment, this.grailsApplication.config.zenboot.host.instances.defaultNamePattern.toString())
    }
}
