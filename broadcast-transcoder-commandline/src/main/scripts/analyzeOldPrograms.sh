#!/bin/bash

collection=Broadcast

programList=$1
fileSizes=$2
succesList=$3
failureList=$4

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh

java -Dlogback.configurationFile=$confDir/logback.xml $hibernate_log_config \
 -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.ProgramAnalyzer \
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
  --hibernate_configfile=$confDir/hibernate.cfg.xml\
  --behavioural_configfile=$confDir/bta.behaviour.properties \
 --programList=$programList \
  --fileSizes=$fileSizes 2> $failureList 1> $succesList

exit $?
