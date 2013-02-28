#!/bin/bash

for var in "$@"
do
    echo $var
    echo $var 1>&2
done


for var in "$@"
do
  if [ "var" == "exit1" ]; then
    exit 1
  fi
  if [ "var" == "exit2" ]; then
    exit 2
  fi
done