#!/bin/bash

programList=$1

collection=Broadcast

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh

java -Dlogback.configurationFile=$confDir/logback.xml $hibernate_log_config \
 -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.MarkAsComplete \
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --hibernate_configfile=$confDir/hibernate.cfg.xml \
 --programList=$programList

exit $?
