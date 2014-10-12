#!/bin/bash

#@Scriptlet(author="Kim Neunert (kim.neunert@hybris.com)", description="list the images")
#@Parameters([
#  @Parameter(name="DO_API_KEY", type=ParameterType.CONSUME, description="Digital Occean API key"),
#])


curl -X GET "https://api.digitalocean.com/v2/images/" \
	-H "Authorization: Bearer $DO_API_KEY"
