#!/bin/bash

# @Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Echo a string to stdout and sterr")
# @Parameter(name="WHAT_TO_SAY", type=ParameterType.CONSUME, description="Set what the you want to say"),

me=`basename $0`

echo "WHAT_TO_SAY=$WHAT_TO_SAY (this value was set in script $me ;) )"