import org.zenboot.portal.security.ZenbootUserDetailsContextMapper

import java.util.concurrent.Executors;

import org.zenboot.portal.security.AdminRoleVoter;

// Place your Spring DSL code here
beans = {
    log4jConfigurer(org.springframework.beans.factory.config.MethodInvokingFactoryBean) {
        targetClass = "org.springframework.util.Log4jConfigurer"
        targetMethod = "initLogging"
        arguments = ["classpath:log4j.properties"]
    }

    //configure type converters for processing
    hostnameParameterConverter(org.zenboot.portal.processing.converter.HostnameParameterConverter) { bean ->
    }
    hostParameterConverter(org.zenboot.portal.processing.converter.HostParameterConverter) { bean ->
    }

    if (grailsApplication.config.zenboot.processing.workerThreads.respondsTo("toInteger") &&
        grailsApplication.config.zenboot.processing.workerThreads.toInteger() > 0) {
        executorService(grails.plugin.executor.PersistenceContextExecutorWrapper) { bean->
            bean.destroyMethod = 'destroy'
            persistenceInterceptor = ref("persistenceInterceptor")
            executor = Executors.newFixedThreadPool(grailsApplication.config.zenboot.processing.workerThreads.toInteger())
        }
    }

    //Spring security role hierarchy voter
    roleVoter(AdminRoleVoter, ref('roleHierarchy'))

    // map ldap users to db users
    ldapUserDetailsMapper(ZenbootUserDetailsContextMapper) {
        // bean attributes
    }
}
