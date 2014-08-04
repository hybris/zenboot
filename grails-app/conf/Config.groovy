import org.zenboot.portal.security.Role

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.config.locations = [
    "file:${basedir}/grails-app/conf/NavigationConfig.groovy", //won't work in WAR
    "classpath:NavigationConfig.groovy", //copied to classpath in scripts/_Events.groovy
    "file:${basedir}/zenboot.properties", //won't work in WAR
    "classpath:zenboot.properties", //${basedir}/*.properties (except log4.properties) is automatically copied to classpath by Grails
    "file:/etc/zenboot/zenboot.properties",         // Mainly for Docker-Usage
    "file:${userHome}/zenboot/zenboot.Docker.properties"  // Mainly for Docker-Usage
]

grails.project.groupId = 'org.zenboot.portal' // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: [
        'text/html',
        'application/xhtml+xml'
    ],
    xml: [
        'text/xml',
        'application/xml'
    ],
    text: 'text/plain',
    js: 'text/javascript',
    rss: 'application/rss+xml',
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    all: '*/*',
    json: [
        'application/json',
        'text/json'
    ],
    yaml: [
        'text/x-yaml',
        'application/x-yaml'
    ],
    properties: [
        'text/x-java-properties'
    ],
    form: 'application/x-www-form-urlencoded',
    multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = [
    '/images/*',
    '/css/*',
    '/js/*',
    '/plugins/*'
]

// The default codec used to encode data with ${}
grails.views.default.codec = "html" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.converters.xml.default.deep = true
grails.converters.json.default.deep = true

// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

grails.gorm.default.constraints = {'*'(nullable: true)}

// enable query caching by default
grails.hibernate.cache.queries = true

// Use spring binder (http://grails.org/doc/2.3.x/guide/upgradingFromPreviousVersionsOfGrails.html)
grails.databinding.useSpringBinder = true

// email configuration
grails.mail.default.from="zenboot-noreply@yourdomain.com"

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
        grails.resources.debug = true
        grails.mail.host = "smtp.googlemail.com"
        grails.mail.port = 465
        grails.mail.username = ""
        grails.mail.password = ""
        grails.mail.props = [
                "mail.smtp.auth":"true",
                "mail.smtp.socketFactory.port":"465",
                "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
                "mail.smtp.socketFactory.fallback":"false"
            ]
    }

    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
        grails.mail.host = "localhost"
        grails.mail.port = 25
    }
}

//Spring security configuration
grails.plugins.springsecurity.password.algorithm='SHA-512'
grails.plugins.springsecurity.userLookup.userDomainClassName = 'org.zenboot.portal.security.Person'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'org.zenboot.portal.security.PersonRole'
grails.plugins.springsecurity.authority.className = 'org.zenboot.portal.security.Role'
grails.plugins.springsecurity.rejectIfNoRule = true

grails.plugins.springsecurity.useBasicAuth = true
grails.plugins.springsecurity.filterChain.chainMap = [
    '/rest/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
    '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
 ]

grails.plugins.springsecurity.controllerAnnotations.staticRules = [
    '/js/**':                               ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/images/**':                           ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/css/**':                              ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/login/**':                            ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/logout/**':                           ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/plugins/**':                          ['IS_AUTHENTICATED_ANONYMOUSLY'],
    //'/**/rest/**':                          ['IS_AUTHENTICATED_ANONYMOUSLY'],
    '/**/rest/**':                          ['IS_AUTHENTICATED_FULLY'],
    '/home/index':                          [Role.ROLE_USER],
    '/exposedExecutionZoneAction/list':     [Role.ROLE_USER],
    '/exposedExecutionZoneAction/show':     [Role.ROLE_USER],
    '/exposedExecutionZoneAction/execute':  [Role.ROLE_USER],
    '/scriptletBatch/ajaxList':             [Role.ROLE_USER],
    //default
    '/**':                                  [Role.ROLE_ADMIN],
 ]

//fix pagination bug in bootstrap
grails.plugins.twitterbootstrap.fixtaglib = true

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
