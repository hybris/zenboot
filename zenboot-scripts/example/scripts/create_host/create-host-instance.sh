#!/bin/bash

#@Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Create a host instance (FAKE!)")
#@Parameters([
#  @Parameter(name="IP",       type=ParameterType.EMIT,    description="A random IP address"),
#  @Parameter(name="MAC",      type=ParameterType.EMIT,    description="A random MAC address"),
#  @Parameter(name="HOSTNAME", type=ParameterType.CONSUME, description="The name of the host which will be set"),
#])

sleep 1


echo "#These random values will be available as env-parameters in all following scripts"
echo "IP=123.456.789.123"
echo "MAC=ab:12:cd:34:ef:56"
