#!/bin/bash
#set -x
TEST_OK=0
TEST_FAILURE=0
DEBUG=false

function debug() {
  if [ "$DEBUG" == "true" ]; then
    echo "DEBUG: $1"
  fi
}

function _result() {
  local RETURN_VALUE=$1
  local INTRO="$2"
  local ERROR_MESSAGE="$3"
  local FORMAT="%-100s [%s]\n"

  if [ $RETURN_VALUE -ne 0 ]; then
    printf "$FORMAT" "$INTRO:" "FAILED"
    if [ "$ERROR_MESSAGE" != "" ]; then
      echo $ERROR_MESSAGE
    fi
    TEST_FAILURE=`expr $TEST_FAILURE + 1`
  else
    printf "$FORMAT" "$INTRO :" "OK"
    TEST_OK=`expr $TEST_OK + 1`
  fi
}

function _tmpresult() {
  TMPRESULTEXITCODE=$1
  TMPRESULTINTROMSG=$2
  TMPRESULTERRORMSG=$3
}

function for_n_seconds_try() {
  _RESULT=_tmpresult
  seconds=$1
  echo "looping for max $seconds seconds"
  array=( $* )
  unset array[0]
  echo ${array[*]}
  for (( i=0; i<$seconds; i++ ))
  do
        ${array[*]}
        if [ $? -eq 0 ]; then
          break
        fi
        sleep 1
  done
  _RESULT=_result
  $_RESULT $TMPRESULTEXITCODE "$TMPRESULTINTROMSG" "$TMPRESULTERRORMSG"
}



function assert_http_code() {
  local URL="$1"
  local EXPECTED=$2

  local METHOD="$3"
  local HEADER="$4"
  local CREDENTIALS="$5"

  if [ "$METHOD" == "" ]; then
    METHOD="GET"
  fi
  local COMMAND="curl -sL -o /dev/null --write-out '%{http_code}' --request $METHOD --max-time 5"

  if [ "$CREDENTIALS" != "" ]; then
    COMMAND="$COMMAND --basic --user '$CREDENTIALS'"
  fi
  if [ "$HEADER" != "" ]; then
    COMMAND="$COMMAND -H '$HEADER'"
  fi

  debug "$COMMAND $URL"

  local OUTPUT=`eval $COMMAND $URL`
  echo $OUTPUT | grep -q $EXPECTED 
  local res=$?
  $_RESULT $res "Assert response code $EXPECTED for $URL" "Expected response code was $EXPECTED, got $OUTPUT"
  return $res
}

function assert_http_ok() {
  local URL="$1"
  assert_http_code $URL 200
}

function assert_http_response() {
  local URL="$1"
  local EXPECTED=$2

  debug "curl -s $URL"

  local OUTPUT=`curl -sL $URL`
  echo $OUTPUT | grep -q "$EXPECTED"
  local res=$?
  $_RESULT $res "Assert response of $URL" "Output does not contain '$EXPECTED'"
  return $res
}

function assert_tcp_output() {
  local HOST=$1
  local PORT=$2
  local INPUT=$3
  local EXPECTED=$4

  OUTPUT=`echo $INPUT | nc $HOST $PORT`
  echo $OUTPUT | grep -q "$EXPECTED"
  local res=$?
  $_RESULT $res "Assert output of $HOST:$PORT" "Output does not contain '$EXPECTED'"
  return $res
}

function assert_port_open() {
  local HOST=$1
  local PORT=$2

  OUTPUT=`nc -z $HOST $PORT`
  $_RESULT $? "Assert $HOST:$PORT" "Port $PORT is not open"
}

function assert_command() {
  local COMMAND=$1
  sh -c "$COMMAND" 2&> /dev/null
  local res=$?
  $_RESULT $res "Assert command result" "Command '$COMMAND' failed with return code $?"
  return $res
}

function test_setup() {
  TEST_OK=0
  TEST_FAILURE=0
  if [ "$1" == "true" ]; then
    DEBUG=true
  fi
  _RESULT=_result
}

function test_teardown() {
  RESULT="SUCCESS"
  EXIT_CODE=0
  if [ $TEST_FAILURE -gt 0 ]; then
    RESULT="FAILURE"
    EXIT_CODE=2
  fi
  printf "%s tests executed: %s OK, %s FAILED.\n" `expr $TEST_OK + $TEST_FAILURE` $TEST_OK $TEST_FAILURE
  echo "Test result: $RESULT"
  exit $EXIT_CODE
}
