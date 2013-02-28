grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.tomcat.nio = false

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
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.5"
        //compile ":cache-headers:1.1.5"
        runtime ":executor:0.3"
        runtime ":mail:1.0"
        runtime ":navigation:1.3.2"
        runtime ":quartz2:0.2.3"
        runtime ":spring-security-core:1.2.7.3"
        runtime ":svn:1.0.2"
        runtime ":twitter-bootstrap:2.1.1"

        //spring security ui specific
        runtime ":spring-security-ui:0.2"
        runtime ":famfamfam:1.0.1"
        runtime ":jquery-ui:1.8.15"

        //DB console
        runtime ":dbconsole:1.1"

        build ":tomcat:$grailsVersion"
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
