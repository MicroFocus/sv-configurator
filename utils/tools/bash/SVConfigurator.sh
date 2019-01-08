#!/bin/bash

MY_PATH="`dirname \"$0\"`"
PARAMS=""

SVCONF_WAIT_MS=30000

for PARAM in "$@"
do
  PARAMS="${PARAMS} \"${PARAM}\""
done

bash -c "java -Dsvconf_wait_ms=${SVCONF_WAIT_MS} -jar \"${MY_PATH}/SVConfigurator.jar\" ${PARAMS}"