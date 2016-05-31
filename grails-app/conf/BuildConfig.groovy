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
        compile ":hibernate:3.6.10.14"
        compile ":version-update:1.2.0"
        compile ":joda-time:1.5"
        runtime ":jquery:1.11.1"
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.5"
        //compile ":cache-headers:1.1.5"
        runtime ":executor:0.3"
        runtime ":mail:1.0.7"
        //runtime ":navigation:1.3.2"
        compile ":platform-core:1.0.0"
        runtime ":quartz2:0.2.3"
        runtime ":resources:1.2.14"
        runtime ":lesscss-resources:1.3.3"
        runtime ":spring-security-core:2.0.0"
        compile ":webxml:1.4.1"
        runtime ':twitter-bootstrap:2.3.2.2'
        runtime ':console:1.5.0'

        //spring security ui specific
        runtime ":spring-security-ui:1.0-RC3"
        runtime ":famfamfam:1.0.1"
        runtime ":jquery-ui:1.10.4"

        build ':tomcat:7.0.52.1'
    }
}

grails.war.copyToWebApp = { args ->
    fileset(dir:"web-app") {
        include(name: "js/**")
        include(name: "css/**")
        include(name: "images/**")
        include(name: "WEB-INF/**")
    }
    //yscripts to WAR file
    fileset(dir:".") {
        include(name: "zenboot-scripts/**")
    }
}
