#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"


collection=$1
timestamp=$2


java -Dlogback.configurationFile=$SCRIPT_PATH/../config/logback.xml $hibernate_log_config -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.fetcher.BtaDomsFetcher \
 --infrastructure_configfile=$SCRIPT_PATH/../config/bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../config/bta.fetcher.${collection}.properties \
 --since=$timestamp

