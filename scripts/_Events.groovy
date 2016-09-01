import grails.util.Environment

// add test/common to the classpath for tests - used for helper classes
eventCompileStart = {
    projectCompiler.srcDirectories << "$basedir/test/common"
}
eventAllTestsStart = {
    classLoader.addURL(new File("$basedir/test/common").toURI().toURL())
}

eventCompileEnd = {

    //add logger configuration to the classpath (except for production, there we expect that the provided log-conf is provided)
    if(Environment.current != Environment.PRODUCTION) {
        ant.copy(todir:classesDirPath) {
            fileset(file:"${basedir}/log4j.properties")
        }
    }

    //add test resources to the classpath for test exeuction (won't work for Eclipse please see Wiki page "Grails" how to fix this)
    if(Environment.current == Environment.TEST) {
        ant.copy(todir:classesDirPath) {
            fileset(dir:"${basedir}/test/resources")
        }
    }
}