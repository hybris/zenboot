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

    def buildResult
    def errStat = 0

    buildResult = ['go', 'get', '-d', './...'].execute()
    buildResult.waitForProcessOutput(System.out, System.err)
    errStat = buildResult.exitValue() != 0 ? buildResult.exitValue(): errStat

    new File('zenboot-cli/bin').mkdirs()
    def env = System.getenv().collect { k, v -> "$k=$v" }
    goga = [['linux', 'amd64'], ['linux', '386'], ['darwin', 'amd64'], ['darwin', '386']].each {
      buildResult = ['go', 'build', '-o', 'zenboot-cli/bin/zenboot-' + it[0] + '-' + it[1], 'zenboot-cli/zenboot.go'].execute(env.plus(["GOOS=${it[0]}", "GOARCH=${it[1]}"]), null)
      buildResult.waitForProcessOutput(System.out, System.err)
      errStat = buildResult.exitValue() != 0 ? buildResult.exitValue(): errStat
    }

    if (errStat != 0) {
        System.err << "Building the CLI failed with exit code " + errStat
    }
}
