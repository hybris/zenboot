#!/bin/bash

#@Scriptlet(author="Tobias Schuhmacher (tschuhmacher@nemeses.de)", description="Create a host instance (FAKE!)")
#@Parameters([
#  @Parameter(name="IP",       type=ParameterType.EMIT,    description="A random IP address"),
#  @Parameter(name="ID",       type=ParameterType.EMIT,    description="A random IP address"),
#  @Parameter(name="MAC",      type=ParameterType.EMIT,    description="A random MAC address"),
#  @Parameter(name="HOSTNAME", type=ParameterType.CONSUME, description="The name of the host which will be set"),
#  @Parameter(name="DO_API_KEY", type=ParameterType.CONSUME, description="Digital Occean API key"),
#])

REQUEST_JSON="{\"name\":\"${HOSTNAME}\",\"region\":\"ams1\",\"size\":\"512mb\",\"image\":757789}"

echo "# REQUEST_JSON ..."
echo $REQUEST_JSON

RESPONSE_JSON=`curl -X POST "https://api.digitalocean.com/v2/droplets" \
    -d $REQUEST_JSON \
    -H "Authorization: Bearer $DO_API_KEY" \
    -H "Content-Type: application/json"`
echo "done"

echo ID=`echo $RESPONSE_JSON | jq .droplet.id`


while [ "$STATUS" != "\"active\"" ]; do
RESPONSE_JSON=`curl -X GET "https://api.digitalocean.com/v2/droplets/${ID}" \
    -H "Authorization: Bearer $DO_API_KEY"`
STATUS=`echo $RESPONSE_JSON | jq .droplet.status`
echo $STATUS
sleep 5
done

echo "ready!"

echo $RESPONSE_JSON
