#!/bin/bash

# @Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Call Zenboot")
# @Parameters([
#   @Parameter(name="USERNAME", description="User who is allowed to login to Zenboot",       type=ParameterType.CONSUME, defaultValue="zenboot"),
#   @Parameter(name="PASSWORD", description="Password of the user who is allowed to login", type=ParameterType.CONSUME, defaultValue="zenboot", visible=false),
# ])

HTTP_CODE=`curl -sL -o /dev/null --write-out %{http_code} --basic --user "${USERNAME}:${PASSWORD}" --request "POST" "http://localhost:8080/zenboot/"`

if [ "$HTTP_CODE" = "200" ]; then
  echo "Zenboot is running :)"
  exit 0
else
  echo "Zenboot is not running ($HTTP_CODE) :("  >&2
  exit 2
fi