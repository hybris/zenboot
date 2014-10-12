#!/bin/bash

#@Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Create a host instance (FAKE!)")
#@Parameters([
#  @Parameter(name="DO_API_KEY", type=ParameterType.CONSUME, description="Digital Occean API key"),
#])


curl -X GET "https://api.digitalocean.com/v2/images/" \
	-H "Authorization: Bearer $DO_API_KEY"
