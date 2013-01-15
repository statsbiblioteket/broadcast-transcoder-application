#!/bin/bash

uuid=$1
timestamp=$2

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh cleanup

java -Dlogback.configurationFile=$confDir/logback.xml $hibernate_log_config \
  -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.Cleanup \
 --hibernate_configfile=$confDir/bta.iapetus.hibernate.cfg.xml \
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp
