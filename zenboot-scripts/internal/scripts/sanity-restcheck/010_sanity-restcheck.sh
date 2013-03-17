#!/bin/bash

. sanitycheck_functions.sh

NODENAME=127.0.0.1
PORT=8080

URL="http://${NODENAME}:${PORT}/zenboot/rest/sanitycheck"

test_setup
# First call it to verify that this job get created
assert_http_code $URL 201 "POST" "Content-Type:text/xml" "sanitycheck:sanitycheck"

echo "# Make a second call " 
TMPFILE=`tempfile`
curl -sL  --write-out '%{http_code}' --request POST --max-time 5 --basic --user 'sanitycheck:sanitycheck' -H 'Content-Type:text/xml' http://127.0.0.1:8080/zenboot/rest/sanitycheck | xmlindent -l 70 -nas | sed -e "s/<status>//" > $TMPFILE
CALLBACK=$(xml_parse referral $TMPFILE )
echo "# CALLBACK is $CALLBACK"
sleep 1
assert_http_response $CALLBACK "RUNNING" "-H 'Content-Type:text/xml' --user sanitycheck:sanitycheck"
sleep 3
assert_http_response $CALLBACK "SUCCESS" "-H 'Content-Type:text/xml' --user sanitycheck:sanitycheck "



test_teardown
