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


if [ $returncode -ne 0 ]; then
    echo "$collection" "$uuid" "$timestamp"  >> $stateDir/$workerID.$collection.failures
fi
#when we have categorized the file, mark is as complete
rm $workDir/$workerID$collection.workerFile

exit $returncode
