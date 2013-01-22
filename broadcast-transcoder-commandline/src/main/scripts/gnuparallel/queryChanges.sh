#!/bin/bash

collection=$1
progressFile=$2

timestamp=$(cat $progressFile)


# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh

changes=$(mktemp)

java -Dlogback.configurationFile=$confDir/logback.xml $hibernate_log_config \
 -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.fetcher.BtaDomsFetcher \
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.fetcher.${collection}.properties \
 --since=$timestamp > $changes
returncode=$?

if [ $returncode = 0 ]; then
    tail -1 $changes | cut -d' ' -f2 > $progressFile
fi
 cat $changes

 rm $changes

