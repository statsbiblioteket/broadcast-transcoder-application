#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

collection=${1}
uuid=$2
timestamp=$3
machine=$4
logDir=$5
confDir=$6


[ -z "$logDir" ] && logDir="$SCRIPT_PATH/.."
[ -z "$confDir" ] && confDir="$SCRIPT_PATH/../config"

#use machine here to ssh to a machine to run this on
$SCRIPT_PATH/transcodeFile.sh "$collection" "$uuid" "$timestamp" "$logDir" "$confDir"

returncode=$?

## Consider placing progress, successes, failures outside deploy directory so they don't get nuked by deploy.

if [ $returncode -eq 0 ]; then
   progressFile="$logDir/$collection.progress"
   lockfile "$progressFile.lock"
       progress_timestamp=$(cat "$progressFile" | tail -1)
       if [ $timestamp -gt $progress_timestamp ]; then
          echo $timestamp > $progressFile
       fi
       echo "$uuid   $timestamp $machine" >> $logDir/$collection.successes
   rm -f "$progressFile.lock"
else
    lockfile "$logDir/fails.lock"
        echo "$uuid   $timestamp $machine" >> $logDir/$collection.failures
    rm -f "$logDir/fails.lock"
fi

exit $returncode