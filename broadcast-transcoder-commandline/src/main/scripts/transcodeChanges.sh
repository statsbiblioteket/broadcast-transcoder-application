#!/bin/bash


SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

## This is 2012-04-01
INITIAL_TIMESTAMP=1333231200000

#The number of worker processes. If this is changed from a previous run then the
#cleanup loop needs to be executed by hand.
WORKERS=5

#Cleanup from previous run
for ((i=0;i<$WORKERS;i++)); do
            workerfile="${i}workerFile"
            if [ -e  $workerfile ]; then
              uuid=$(cat $workerfile|cut -d ' ' -f2)
              timestamp=$(cat $workerfile|cut -d ' ' -f3)
              echo "$uuid   $timestamp" >> $SCRIPT_PATH/../fails
              rm $workerfile
              $SCRIPT_PATH/cleanupUnfinished.sh $uuid $timestamp
            fi
done

if ![ -e "$SCRIPT_PATH/../progress" ]; then
    echo $INITIAL_TIMESTAMP > $SCRIPT_PATH/../progress
fi

#get list of changes from queryChanges with progress timestamp as input
timestamp=$(cat $SCRIPT_PATH/../progress | tail -1)
changes=$(mktemp)
$SCRIPT_PATH/queryChanges.sh $timestamp > $changes

#cut list into pid/timestamp sets
#iterate through list,
while read line; do
    time=$(echo $line | cut -d' ' -f2)
    uuid=$(echo $line | cut -d' ' -f1)
while [ 1 ]; do
        for ((i=0;i<$WORKERS;i++)); do
            workerfile="${i}workerFile"
            if [ -e $workerfile ]; then
                pid=$(cat $workerfile|cut -d ' ' -f1)
                ps ax | grep -v grep | cut -d' ' -f1 | grep $pid
                found=$?
                if [ $found != 0 ]; then
                    rm $workerfile
                fi
            fi
            if [ ! -e $workerfile ]; then
                touch $workerfile
                $SCRIPT_PATH/transcodeFile.sh $uuid $time &
                echo "$! $uuid $time" > $workerfile
                break 2
            fi
        done
        sleep 10
        echo
done
done < $changes

rm $changes


