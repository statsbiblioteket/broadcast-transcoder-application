#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

collection=${1}
uuid=$2
timestamp=$3
logDir=$4
confDir=$5

[ -z "$logDir" ] && logDir="$SCRIPT_PATH/.."
[ -z "$confDir" ] && confDir="$SCRIPT_PATH/../conf"

java \
  -Dlogback.configurationFile=$confDir/logback.xml  \
 -cp "$CLASSPATH" \
  dk.statsbiblioteket.broadcasttranscoder.${collection}TranscoderApplication \
 --hibernate_configfile=$confDir/hibernate.cfg.xml\
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

returncode=$?

exit $returncode