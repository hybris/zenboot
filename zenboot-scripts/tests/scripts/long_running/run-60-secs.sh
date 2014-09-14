#!/bin/bash

for ten in 0 1 2 3 4 5; do
for seconds in 1 2 3 4 5 6 7 8 9
do
  echo "#${ten}${seconds}"
  echo "#ERROR ${ten}${seconds}" 1>&2
  sleep 1
done
done
