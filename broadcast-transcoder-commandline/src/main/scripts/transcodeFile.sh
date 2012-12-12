#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

collection=${1^}
uuid=$2
timestamp=$3
machine=$4
logDir=$5
confDir=$6

[ -z "$logDir" ] && logDir="$SCRIPT_PATH/.."
[ -z "$confDir" ] && confDir="$SCRIPT_PATH/../conf"



java \
  -Dlogback.configurationFile=$confDir/logback.xml  \
 -cp "$CLASSPATH" \
  dk.statsbiblioteket.broadcasttranscoder.${collection}TranscoderApplication \
 --hibernate_configfile=$confDir/hibernate.cfg.xml\
 --infrastructure_configfile=$confDir/bta.infrastructure.properties \
 --behavioural_configfile=$confDir/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

returncode=$?

## Consider placing progress, successes, failures outside deploy directory so they don't get nuked by deploy.

if [ $returncode -eq 0 ]; then
   progressFile="$logDir/progress"
   lockfile "$progressFile.lock"
       progress_timestamp=$(cat "$progressFile" | tail -1)
       if [ $timestamp -gt $progress_timestamp ]; then
          echo $timestamp > $progressFile
       fi
       echo "$uuid   $timestamp $machine" >> $logDir/successes
   rm -f "$progressFile.lock"
else
    lockfile "$logDir/fails.lock"
        echo "$uuid   $timestamp $machine" >> $logDir/failures
    rm -f "$logDir/fails.lock"
fi

exit $returncode