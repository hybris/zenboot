#!/bin/bash

#@Scriptlet(author="Kim Neunert (kim.neunert@hybris.com)", description="sets the shortname")
#@Parameters([
#  @Parameter(name="HOSTNAME",      type=ParameterType.CONSUME),
#  @Parameter(name="SHORTNAME",     type=ParameterType.EMIT),
#])



NUMBER=`echo $HOSTNAME | cut -d- -f3`
echo "SHORTNAME=jks-$NUMBER"
