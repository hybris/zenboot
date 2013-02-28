#!/bin/bash

. sanitycheck_functions.sh

NODENAME=127.0.0.1
PORT=8080

URL="http://${NODENAME}:${PORT}/zenboot/rest/sanitycheck"

test_setup
assert_http_code $URL 400 "POST" "Content-Type:text/xml" "sanitycheck:!sanitycheck!"
test_teardown
