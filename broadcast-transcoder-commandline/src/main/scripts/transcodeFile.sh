#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

uuid=$1
timestamp=$2

java -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.BroadcastTranscoderApplication \
 --hibernate_configfile=$SCRIPT_PATH/../conf/bta.iapetus.hibernate.cfg.xml\
 --infrastructure_configfile=$SCRIPT_PATH/../conf/bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../conf/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

returncode=$?

if [ $returncode -eq 0 ]; then
   progress_timestamp=$(cat $SCRIPT_PATH/../progress | tail -1)
   if [ $timestamp -gt $progress_timestamp ]; then
      echo $timestamp > $SCRIPT_PATH/../progress
   fi
else
   echo "$uuid   $timestamp" >> $SCRIPT_PATH/../fails
fi

exit $returncode