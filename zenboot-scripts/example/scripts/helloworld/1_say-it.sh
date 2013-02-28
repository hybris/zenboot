#!/bin/bash

# @Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Echo a string to stdout and sterr")
# @Parameter(name="WHAT_TO_SAY", type=ParameterType.CONSUME, description="What the script should say", defaultValue="Hello world!"),

echo "#Written to stdout:"
echo $WHAT_TO_SAY

echo "#Written to errout:" >&2
echo $WHAT_TO_SAY >&2