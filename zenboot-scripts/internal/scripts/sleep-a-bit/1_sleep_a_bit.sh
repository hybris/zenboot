#!/bin/bash

# @Scriptlet(description="sleeps a bit.")
# @Parameters([
#   @Parameter(name="SLEEP_SECONDS", description="what do you think it does?", type=ParameterType.CONSUME),
# ])

echo "# sleeping for ${SLEEP_SECONDS:-10} seconds.."

sleep "${SLEEP_SECONDS:-10}"
