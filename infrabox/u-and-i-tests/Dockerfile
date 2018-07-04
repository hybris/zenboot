ARG INFRABOX_BUILD_NUMBER
FROM v2-registry.dhcp.wdf.sap.corp/zenboot-builder:build_$INFRABOX_BUILD_NUMBER

ENTRYPOINT ./grailsw test-app unit: integration: \
    && cp -rf target/test-reports/* /infrabox/upload/testresult/
