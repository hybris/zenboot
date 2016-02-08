grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}.war"

grails.tomcat.nio = false

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        //excludes "log4j" , "grails-plugin-log4j"
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenLocal()
        mavenRepo "http://repository.codehaus.org"
    }

    dependencies {
        compile 'org.jyaml:jyaml:1.3'
        compile 'mysql:mysql-connector-java:5.1.21'
    }

    plugins {
        compile ":hibernate:3.6.10.2"
        compile ":version-update:1.2.0"
        compile ":joda-time:1.5"
        compile ":asset-pipeline:1.9.3"
        runtime ":jquery:1.7.2"
        //runtime ":yui-minify-resources:0.1.5"
        //compile ":cache-headers:1.1.5"
        runtime ":executor:0.3"
        runtime ":mail:1.0"
        runtime ":navigation:1.3.2"
        runtime ":quartz2:0.2.3"
        runtime ":spring-security-core:1.2.7.3"
        compile ":webxml:1.4.1"
        runtime ':twitter-bootstrap:2.3.2.2'
        runtime ':console:1.5.0'

        //spring security ui specific
        runtime ":spring-security-ui:0.2"
        runtime ":famfamfam:1.0.1"
        runtime ":jquery-ui:1.8.15"

        build ':tomcat:7.0.42'

        compile ':likeable:0.1.2'
        compile ":pretty-time:2.1.3.Final-1.0.1"

        compile ':audit-logging:1.1.0'
        compile "org.grails.plugins:filterpane:2.4.2"
    }
}

grails.war.copyToWebApp = { args ->
    fileset(dir:"web-app") {
        include(name: "js/**")
        include(name: "css/**")
        include(name: "images/**")
        include(name: "WEB-INF/**")
        include(name: "tspa/**")
    }
    //yscripts to WAR file
    fileset(dir:".") {
        include(name: "zenboot-scripts/**")
    }
}

forkConfig = [maxMemory: 1024, minMemory: 64, debug: false, maxPerm: 256]
grails.project.fork = [
        test: forkConfig,
        run: forkConfig,
        war: forkConfig,
        console: forkConfig
]
