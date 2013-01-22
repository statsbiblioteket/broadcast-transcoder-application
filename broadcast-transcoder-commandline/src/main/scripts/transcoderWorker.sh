#!/bin/bash

collection=$1
uuid=$2
timestamp=$3
machine=$4
workerID=$5

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
trap 'rm -f $transcoderOutput;  exit 1' 0 1 2 3 15

# Worker output goes here
transcoderOutput=$(mktemp -p $workDir)

# Run transcode
$SSH_COMMAND $SCRIPT_PATH/transcodeFile.sh "$collection" "$uuid" "$timestamp" "$machine" &> $transcoderOutput
returncode=$?
[ $debug = 1 ] && echo transcodeFile.sh returned with exit code $returncode


if [ $returncode -eq 0 ]; then
    progressFile=$stateDir/$workerID.$collection.progress
   progress_timestamp=$(cat $progressFile)
   if [ $timestamp -gt $progress_timestamp ]; then
        [ $debug = 1 ] && echo transcodeFile.sh wrote timestamp $timestamp to $progressFile
       echo $timestamp > $progressFile
   fi
   echo  "$collection" "$uuid" "$timestamp"  >> $stateDir/$workerID.$collection.successes
elif [  $returncode -eq 111 ]; then
    echo "$collection" "$uuid" "$timestamp"  >> $stateDir/$workerID.$collection.rejects
else
    echo "$collection" "$uuid" "$timestamp"  >> $stateDir/$workerID.$collection.failures
    ##TODO: possibly cat the transcoderOutput
fi
#when we have categorized the file, mark is as complete
rm $workDir/$workerID$collection.workerFile

exit $returncode
