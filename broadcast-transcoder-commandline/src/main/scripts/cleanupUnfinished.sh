#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

uuid=$1
timestamp=$2

java -Dlogback.configurationFile=$SCRIPT_PATH/../conf/logback.xml $hibernate_log_config -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.Cleanup \
 --hibernate_configfile=$SCRIPT_PATH/../config/bta.iapetus.hibernate.cfg.xml\
 --infrastructure_configfile=$SCRIPT_PATH/../config/bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../config/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp