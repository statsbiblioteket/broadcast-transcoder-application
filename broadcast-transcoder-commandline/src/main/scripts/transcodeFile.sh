#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

uuid=$1
timestamp=$2
machine=$3

java -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.BroadcastTranscoderApplication \
 --hibernate_configfile=$SCRIPT_PATH/../conf/bta.iapetus.hibernate.cfg.xml\
 --infrastructure_configfile=$SCRIPT_PATH/../conf/bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../conf/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

returncode=$?

if [ $returncode -eq 0 ]; then
   progressFile="$SCRIPT_PATH/../progress.lock"
   lockfile "$progressFile.lock"
       progress_timestamp=$(cat "$progressFile" | tail -1)
       if [ $timestamp -gt $progress_timestamp ]; then
          echo $timestamp > $progressFile
       fi
       echo "$uuid   $timestamp $machine" >> $SCRIPT_PATH/../succeses
   rm -f "$progressFile.lock"
else
    lockfile "$SCRIPT_PATH/../fails.lock"
        echo "$uuid   $timestamp $machine" >> $SCRIPT_PATH/../fails
    rm -f "$SCRIPT_PATH/../fails.lock"
fi

exit $returncode