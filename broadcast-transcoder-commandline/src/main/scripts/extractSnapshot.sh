#!/bin/sh

collection=$1
uuid=$2
timestamp=$3

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh

java -Dlogback.configurationFile=$confDir/logback-transcodeFile.xml $hibernate_log_config \
 -cp "$CLASSPATH" \
  dk.statsbiblioteket.broadcasttranscoder.thumbnailer.BroadcastThumbnailApplication \
 --hibernate_configfile=$confDir/hibernate.cfg.xml\
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

