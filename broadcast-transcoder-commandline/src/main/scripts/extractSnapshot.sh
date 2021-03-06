#!/bin/sh

uuid=$1

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $BASH_SOURCE[0]))
source $SCRIPT_PATH/setenv.sh

java -Dlogback.configurationFile=$confDir/logback-extractSnapshot.xml $hibernate_log_config \
 -cp "$CLASSPATH" \
  dk.statsbiblioteket.broadcasttranscoder.BroadcastThumbnailApplication \
 --hibernate_configfile=$confDir/hibernate.cfg.xml\
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=0

