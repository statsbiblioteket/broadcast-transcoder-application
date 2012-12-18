#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

collection=${1}
uuid=$2
timestamp=$3

source $SCRIPT_PATH/setenv.sh $collection

java \
  -Dlogback.configurationFile=$confDir/logback.xml  \
 -cp "$CLASSPATH" \
 $hibernate_log_config \
  dk.statsbiblioteket.broadcasttranscoder.${collection}TranscoderApplication \
 --hibernate_configfile=$confDir/hibernate.cfg.xml\
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

returncode=$?

exit $returncode