#!/bin/bash

#This actually queries the bta database for jobs

collection=$1

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $BASH_SOURCE[0]))
source $SCRIPT_PATH/setenv.sh

java -Dlogback.configurationFile=$confDir/logback-queryChangesDoms.xml $hibernate_log_config \
 -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.GetAllScheduledJobs \
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.fetcher.${collection}.properties \
 --hibernate_configfile=$confDir/hibernate.cfg.xml \
 --timestamp=0 | grep "^uuid" |
while read line
do
    # Prepend collection id to each line since run_transcoder.sh expects jobs formatted like this
    echo "$collection $line"
done
