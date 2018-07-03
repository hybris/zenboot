ARG INFRABOX_BUILD_NUMBER
FROM v2-registry.dhcp.wdf.sap.corp:5000/executor:build_$INFRABOX_BUILD_NUMBER

ENTRYPOINT ./grailsw test-app functional: 'org.zenboot.portal.api.**.*' -inline \
    && cp -rf target/test-reports/* /infrabox/upload/testresult/
