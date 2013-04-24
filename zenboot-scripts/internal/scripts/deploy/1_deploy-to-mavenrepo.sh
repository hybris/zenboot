#!/bin/bash

# @Scriptlet(author="Kim Neunert (kim.neunert@hybris.com)", description="Upload build artifacts to Artifactory")
# @Parameters([
#   @Parameter(name="MAVEN_BIN_DIR", description="Path to the Maven bin directory", type=ParameterType.CONSUME),
#   @Parameter(name="MAVEN_GROUPID", description="Group of the Maven artifact", type=ParameterType.CONSUME, defaultValue="zenboot"),
#   @Parameter(name="MAVEN_VERSION", description="Version of the Maven artifact", type=ParameterType.CONSUME),
#   @Parameter(name="MAVEN_ARTIFACTID", description="Name of the Maven artifact", type=ParameterType.CONSUME, defaultValue="zenboot"),
#   @Parameter(name="REPOSITORY_URL", description="The URL to the Maven compatible repository", type=ParameterType.CONSUME),
#   @Parameter(name="REPOSITORY_ID", description="Name of the Maven repository", type=ParameterType.CONSUME),
#   @Parameter(name="FILE", description="Path to the file which will be deployed", type=ParameterType.CONSUME, defaultValue="target/zenboot.war"),
# ])

[ -z "${MAVEN_GROUPID:+x}" ]    && echo "Env-variable MAVEN_GROUP_ID is missing!"   && exit 1
[ -z "${MAVEN_ARTIFACTID:+x}" ] && echo "Env-variable MAVEN_ARTIFACTID is missing!" && exit 1
[ -z "${REPOSITORY_URL:+x}" ]   && echo "Env-variable REPOSITORY_URL is missing!"   && exit 1
[ -z "${REPOSITORY_ID:+x}" ]    && echo "Env-variable REPOSITORY_ID is missing!"    && exit 1
[ -z "${FILE:+x}" ]             && echo "Env-variable FILE is missing!"             && exit 1

if [ -z "${MAVEN_BIN_DIR:+x}" ]; then
    # workaround for running on Jenkins
    if [ -d /var/lib/jenkins ]; then
        MAVEN_BIN_DIR=/var/lib/jenkins/tools/Maven/Maven_3.0.4/bin
    elif [ -d /var/jenkins ]; then
        MAVEN_BIN_DIR=/var/jenkins/tools/Maven/Maven_3.0.4/bin
    fi
    echo "Variable MAVEN_BIN_DIR is missing, fallback to Maven in Jenkins: '${MAVEN_BIN_DIR}'"
    export PATH="${PATH}:${MAVEN_BIN_DIR}"
fi

mvn --version
if [ $? -ne 0 ]; then
    echo "Not able to resolve Maven version. Please make sure that the Maven bin directory is in the PATH: ${PATH}"
    exit 1
fi

mvn --version | head -1 | grep -q " Maven 3."
if [ $? -ne 0 ]; then
    echo "Maven 3.x seems to be not installed properly, aborting"
    exit 1
fi

# defaults for command-line-parameter
update_release_info=false
# passing command-line
while [ $# -gt 0 ]; do
  case "$1" in
    -r)
      update_release_info=$2 # hopefully true or false
      shift 2 ;;
    *)
      ;;
  esac
done

# Let's check whether a MAVEN_VERSION is given
if [[ -z "$MAVEN_VERSION" ]]; then
    # This will get a snapshotversion, to get a proper app.version-string in the application.properties
    # There is hopefully a prebuild-bash-script installed like this
    #
    #  #!/bin/bash
    #  HG_VERSIONSTRING=`hg summary | head -1 | cut -d: -f3 | cut -d\  -f1`
    #  APPVERSION_BASE=`cat application.properties | grep app.version | cut -d= -f2`
    #  sed -i "s/app.version.*/app.version=${APPVERSION_BASE}-${HG_VERSIONSTRING}/" application.properties

    APPVERSION=`cat application.properties | grep app.version | cut -d= -f2`
    MAVEN_VERSION=${APPVERSION}-SNAPSHOT
    REPOSITORY_ID=infrastructure-snapshot
    # if no $MAVEN_VERSION is specified, then we won't do a release
    update_release_info=false
fi

# ------------------------------------ parse options -------------------------------------------

# We have a pom.xml where such stuff SHOULD get picked up from, unfortunately, that does not work for some reason. 
# However we use that pom.xml to be able to define repositories and so we can avoid injecting that in the settings.xml
echo ""
echo "Deployment-details:"
echo "=================="
echo "groupId=$MAVEN_GROUPID"
echo "version=$MAVEN_VERSION"
echo "artifactId=$MAVEN_ARTIFACTID"
echo "file=$FILE"
echo "repositoryId=$REPOSITORY_ID"
echo "url=${REPOSITORY_URL}/${REPOSITORY_ID}"
echo "updateReleaseInfo=$update_release_info"
echo ""

mvn deploy:deploy-file -DgroupId=$MAVEN_GROUPID -Dversion=$MAVEN_VERSION -DartifactId=$MAVEN_ARTIFACTID -Dfile=$FILE -DrepositoryId=$REPOSITORY_ID  -Durl=$REPOSITORY_URL/$REPOSITORY_ID -DupdateReleaseInfo=$update_release_info

# Just to be sure, revert the formerly modified application.properties (Let's keep the orig for the reference)

#hg revert application.properties
