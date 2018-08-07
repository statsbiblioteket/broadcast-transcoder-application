#!/bin/bash

#This queries doms for jobs and stores them in the bta database

# See queryChangesDoms.sh for the script that fetches the jobs from the database for the transcoder

collection=$1
timestamp=$2

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $BASH_SOURCE[0]))
source $SCRIPT_PATH/setenv.sh

java -Dlogback.configurationFile=$confDir/logback-enqueueJobs.xml $hibernate_log_config \
 -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.BtaDomsFetcher \
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.fetcher.${collection}.properties \
 --hibernate_configfile=$confDir/hibernate.cfg.xml\
 --since=$timestamp
