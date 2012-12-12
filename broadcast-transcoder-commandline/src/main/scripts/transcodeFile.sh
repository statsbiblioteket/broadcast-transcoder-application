#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

collection=${1^}
uuid=$2
timestamp=$3
machine=$4

java -Dlogback.configurationFile=$SCRIPT_PATH/../conf/logback.xml  -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.BroadcastTranscoderApplication \
 --hibernate_configfile=$SCRIPT_PATH/../conf/hibernate.cfg.xml\
 --infrastructure_configfile=$SCRIPT_PATH/../conf/bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../conf/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

returncode=$?

## Consider placing progress, successes, failures outside deploy directory so they don't get nuked by deploy.

if [ $returncode -eq 0 ]; then
   progressFile="$SCRIPT_PATH/../progress"
   lockfile "$progressFile.lock"
       progress_timestamp=$(cat "$progressFile" | tail -1)
       if [ $timestamp -gt $progress_timestamp ]; then
          echo $timestamp > $progressFile
       fi
       echo "$uuid   $timestamp $machine" >> $SCRIPT_PATH/../successes
   rm -f "$progressFile.lock"
else
    lockfile "$SCRIPT_PATH/../fails.lock"
        echo "$uuid   $timestamp $machine" >> $SCRIPT_PATH/../failures
    rm -f "$SCRIPT_PATH/../fails.lock"
fi

exit $returncode