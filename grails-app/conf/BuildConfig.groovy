grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
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
    //legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        compile 'org.jyaml:jyaml:1.3'
        compile 'mysql:mysql-connector-java:5.1.21'

        test "org.gebish:geb-spock:0.13.1"
        test "org.seleniumhq.selenium:selenium-support:3.0.0-beta2"
        test "org.seleniumhq.selenium:selenium-chrome-driver:3.0.0-beta2"
        test "org.seleniumhq.selenium:selenium-firefox-driver:3.0.0-beta2"
        test ("org.codehaus.groovy.modules.http-builder:http-builder:0.7.1") {
            excludes "org.apache.httpcomponents:httpclient", "commons-logging:commons-logging"
        }
    }

    plugins {
        runtime ':hibernate:3.6.10.18'

        // TODO
        //runtime ':hibernate4:4.3.5.2'
        build ":codenarc:0.25.2"
        compile ":version-update:1.6.0"
        compile ":joda-time:1.5"
        compile ":asset-pipeline:2.11.0"
        runtime ":jquery:1.11.1"
        //runtime ":yui-minify-resources:0.1.5"
        //compile ":cache-headers:1.1.5"
        runtime ":executor:0.3"
        runtime ":mail:1.0.7"
        runtime ":platform-core:1.0.0"
        runtime ":quartz2:2.1.6.2"
        runtime ":spring-security-core:2.0.0"


        String springSecurityVersion = '3.2.9.RELEASE'

        compile ":webxml:1.4.1"
        runtime ':twitter-bootstrap:2.3.2.2'
        runtime ':console:1.5.11'

        //spring security ui specific
        runtime ":spring-security-ui:1.0-RC3"
        // TODO needed?
        runtime ":famfamfam:1.0.1"
        runtime ":jquery-ui:1.8.15"

        build ':tomcat:8.0.33'

        compile ':likeable:0.4.0'
        compile ":pretty-time:2.1.3.Final-1.0.1"

        compile ':audit-logging:1.1.0'
        compile ":filterpane:2.5.0"

        test ":geb:0.13.1"
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

codenarc {
    reports = {
        CodeNarcXmlReport('xml') {
            outputFile = 'target/CodeNarcReport.xml'
            title = 'CodeNarc Report'
        }
        CodeNarcHtmlReport('html') {
            outputFile = 'target/CodeNarcReport.html'
            title = 'CodeNarc Report'
        }
    }
    ruleSetFiles = [
        'file:grails-app/conf/codenarc/codenarc.xml'
    ]
}

forkConfig = [maxMemory: 1024, minMemory: 64, debug: false, maxPerm: 256]
grails.project.fork = [
        test: forkConfig,
        run: forkConfig,
        war: forkConfig,
        console: forkConfig
]
