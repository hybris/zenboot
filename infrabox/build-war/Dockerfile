ARG INFRABOX_BUILD_NUMBER
FROM v2-registry.dhcp.wdf.sap.corp:5000/executor:build_$INFRABOX_BUILD_NUMBER

RUN ./grailsw war

ENTRYPOINT cp target/zenboot.war /infrabox/archive/ \
    && cp zenboot-cli/bin/zenboot-linux-amd64 /infrabox/archive/ \
    && cp zenboot-cli/bin/zenboot-darwin-amd64 /infrabox/archive/
