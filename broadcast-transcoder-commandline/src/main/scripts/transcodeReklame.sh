#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

uuid=$1
timestamp=$2
machine=$3
collection=reklamer


java -Dlogback.configurationFile=$SCRIPT_PATH/../config/logback.xml  -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.ReklamefilmTranscoderApplication \
 --hibernate_configfile=$SCRIPT_PATH/../config/hibernate.cfg.xml\
 --infrastructure_configfile=$SCRIPT_PATH/../config/bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../config/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

returncode=$?

## Consider placing progress, successes, failures outside deploy directory so they don't get nuked by deploy.

if [ $returncode -eq 0 ]; then
   progressFile="$SCRIPT_PATH/../$collection.progress"
   lockfile "$progressFile.lock"
       progress_timestamp=$(cat "$progressFile" | tail -1)
       if [ $timestamp -gt $progress_timestamp ]; then
          echo $timestamp > $progressFile
       fi
       echo "$uuid   $timestamp $machine" >> $SCRIPT_PATH/../$collection.successes
   rm -f "$progressFile.lock"
else
    failureFile= $SCRIPT_PATH/../$collection.failures
    lockfile "$failureFile.lock"
        echo "$uuid   $timestamp $machine" >> $failureFile
    rm -f "$failureFile.lock"
fi

exit $returncode