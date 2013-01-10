#!/bin/bash

ENVIRONMENT=$1

globalLockfile=transcodeChanges.${ENVIRONMENT}.lock
if [ -e $globalLockfile ]; then
   echo Lockfile $globalLockfile exists. Exiting.
   exit 0
else
  touch $globalLockfile
fi

SCRIPT_PATH=$(dirname $(readlink -f $0))
ENVIRONMENT=$1

source $SCRIPT_PATH/setenv.sh $ENVIRONMENT

#Cleanup from previous run
for ((i=0;i<$WORKERS;i++)); do
            workerfile="${i}.$collection.workerFile"
            if [ -e  $workerfile ]; then
              uuid=$(cat $workerfile|cut -d ' ' -f2)
              timestamp=$(cat $workerfile|cut -d ' ' -f3)
              machine=$(cat $workerfile|cut -d ' ' -f4)

              lockfile "$failureFile.lock"
                      echo "$collection $uuid $timestamp" >> $failureFile
              rm -f "$failureFile.lock"

        rm $workerfile
        ##TODO do we need a $machine parameter to this call?
        $SCRIPT_PATH/cleanupUnfinished.sh $uuid $timestamp $machine
    fi
done
rm -f $SCRIPT_PATH/../*.lock *.lock *workerFile



#get list of changes from queryChanges with progress timestamp as input
timestamp=$(cat $progressFile | tail -1)
changes=$(mktemp)
$SCRIPT_PATH/queryChanges.sh  $collection $timestamp | grep "^uuid" > $changes

#cut list into pid/timestamp sets
#iterate through list,

programs=$(cat $changes | wc -l)
echo "number of programs to transcode is $programs"

IFS_OLD=$IFS
IFS=$(echo)

machineIndex=0
counter=0
cat $changes | while read line; do
    echo "trancoding program $counter of $programs"
    echo "Processing $line"
    ((counter++))


    uuid=$(echo $line | cut -d' ' -f1)
    time=$(echo $line | cut -d' ' -f2)
        #Skip until a line starts with uuid
    if [[ $uuid != uuid:* ]]; then
        continue
    fi
    started=0

    while [ $started -eq 0 ]; do
            [ $debug = 1 ] && echo "$uuid: Starting allocation to worker for uuid $uuid"
            for ((i=0;i<$WORKERS;i++)); do
                    workerfile="${i}$collection.workerFile"
                    [ $debug = 1 ] && echo "$uuid: Attempting to get lock on $workerfile"
                    lockfile "$workerfile.lock"
                    if [ ! -e $workerfile ]; then
                        [ $debug = 1 ] && echo "$uuid: $workerfile does not exist, creating"
                        touch $workerfile
                    fi

                    pid=$(cat $workerfile|cut -d' ' -f1)
                    [ $debug = 1 ] && echo "$uuid: Got pid '$pid' from $workerfile"
                    if [ "$pid" == "" ]; then
                        pid="0" #somthing that will nto match in the grep
                    fi

                    ps ax | grep "^\s*$pid " &> /dev/null
                    found=$?

                    if [ $found != 0 ]; then #ie this pid is free
                        machine=${machines[$machineIndex]}
                        [ $debug = 1 ] && echo "$uuid: Did not find found '$pid' among the running processes"
                        [ $debug = 1 ] && echo "$uuid: Starting transcoding for $uuid and $time on $machine"
                        $SCRIPT_PATH/transcoderWorker.sh $collection $uuid $time $machine &
                        echo "$! $uuid $time $machine" > $workerfile
                            #increment the machine index
                            ((machineIndex++))
                            max=${#machines[*]}
                            [ $machineIndex -ge $max ] && machineIndex=0
                         started=1
                    fi
                    [ $debug = 1 ] && echo "$uuid: releasing lock on $workerfile"
                    rm -f "$workerfile.lock"
                    if [ $started -gt 0 ]; then
                        [ $debug = 1 ] && echo "$uuid: Allocated to worker, moving to next entity"
                        break 1
                    fi
            done
            sleep 2
            echo ""
    done
    echo ""
done

IFS=$IFS_OLD

rm $changes
rm *workerFile
rm $globalLockfile

