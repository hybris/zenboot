#!/bin/bash

# @Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Write received parameters to stdout")
#@Parameters([
#  @Parameter(name="FQDN",     type=ParameterType.CONSUME, description="The fqdn of the host"),
#  @Parameter(name="HOSTNAME", type=ParameterType.CONSUME, description="The host name"),
#  @Parameter(name="IP",       type=ParameterType.CONSUME, description="The IP address of the host"),
#  @Parameter(name="MAC",      type=ParameterType.CONSUME, description="The MAC address of the host"),
#])

sleep 1

echo "#The script received following variables:"
echo ""
echo "#IP address:  ${IP}"
echo "#MAC address: ${MAC}"
echo "#hostname:    ${HOSTNAME}"
echo "#fqdn:        ${FQDN}"
