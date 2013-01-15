#!/bin/bash

collection=$1
uuid=$2
timestamp=$3
machine=$4

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh $collection

# Determine if we run local or remote
if [ "$machine" = "local" ]
then
   SSH_COMMAND=""
else
   SSH_COMMAND="ssh $machine"
fi

# Setup up a trap that cleans out temporary files on exit
trap 'rm -f $transcoderOutput' 0 1 2 3 15

# Worker output goes here
transcoderOutput=$(mktemp --tmpdir=$workDir)

# Run transcode
$SSH_COMMAND $SCRIPT_PATH/transcodeFile.sh "$collection" "$uuid" "$timestamp" "$machine" &> $transcoderOutput
returncode=$?

if [ $returncode -eq 0 ]; then
   lockfile "$progressFile.lock"
   progress_timestamp=$(cat $progressFile)
   if [ $timestamp -gt $progress_timestamp ]; then
       echo $timestamp > $progressFile
   fi
   echo  "$collection" "$uuid" "$timestamp"  >> $stateDir/$collection.successes
   rm -f "$progressFile.lock"
else
    lockfile "$logDir/fails.lock"
    echo "$collection" "$uuid" "$timestamp"  >> $failureFile
    ##TODO: possibly cat the transcoderOutput
    rm -f "$logDir/fails.lock"
fi

exit $returncode
