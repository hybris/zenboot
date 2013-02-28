import grails.util.Environment

eventCompileEnd = {

    //add logger configuration to the classpath (except for production, there we expect that the provided log-conf is provided)
    if(Environment.current != Environment.PRODUCTION) {
        ant.copy(todir:classesDirPath) {
            fileset(file:"${basedir}/log4j.properties")
        }
    }

    //add always yconfig to the classpath
    ant.copy(todir:classesDirPath) {
        fileset(file:"${basedir}/grails-app/conf/NavigationConfig.groovy")
    }

    //add test resources to the classpath for test exeuction (won't work for Eclipse please see Wiki page "Grails" how to fix this)
    if(Environment.current == Environment.TEST) {
        ant.copy(todir:classesDirPath) {
            fileset(dir:"${basedir}/test/resources")
        }
    }
}