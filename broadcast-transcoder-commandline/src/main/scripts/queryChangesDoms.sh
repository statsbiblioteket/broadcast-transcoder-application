#!/bin/bash

collection=$1

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh

java -Dlogback.configurationFile=$confDir/logback.xml $hibernate_log_config \
 -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.GetAllScheduledJobs \
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.fetcher.${collection}.properties \
 --hibernate_configfile=$confDir/hibernate.cfg.xml \
 --timestamp=0

