#!/bin/bash

# @Scriptlet(author="Kim Neunert (kim.neunert@hybris.de)", description="really not that much apart from exiting")
# @Parameters([
#   @Parameter(name="EXITCODE", description="the exitcode",   type=ParameterType.CONSUME, defaultValue="1"),
# ])

echo "Let's exit ... with $EXITCODE"

exit $EXITCODE
