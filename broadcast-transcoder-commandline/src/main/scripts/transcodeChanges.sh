#!/bin/bash

collection=$1

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh $collection

# Ensure only one copy of this script runs for a given collection
globalLock=$workDir/transcodeChanges.${collection}.lockdir
if mkdir $globalLock 2>/dev/null; then
    # We're good
    true
else
    # Fail, another copy is running
    echo "$(basename $0): ERROR this script is already running for collection $collection"
    exit 1
fi

# Cleanup from previous run

function cleanup {
[ $debug = 1 ] && echo "Cleaning Up"
for ((i=0;i<$WORKERS;i++)); do
    workerfile="$workDir/$i$collection.workerFile"
    if [ -e  "$workerfile" ]; then
        [ $debug = 1 ] && echo Cleaning from $workerfile
        pid=$(cat $workerfile|cut -d' ' -f1)
        uuid=$(cat $workerfile|cut -d ' ' -f2)
        timestamp=$(cat $workerfile|cut -d ' ' -f3)
        machine=$(cat $workerfile|cut -d ' ' -f4)
        [ $debug = 1 ] && echo Cleaning up after $uuid
        kill $pid
        rm $workerfile
        $SCRIPT_PATH/cleanupUnfinished.sh $uuid $timestamp $machine
    fi
done
}

cleanup;
trap 'cleanup; rmdir $globalLock; rm -rf $changes; exit 1' INT TERM

##
# This is probably not necessary, unless we have an unusual race condition that leaves lockfiles behind from a
# previous run.
rm -f $workDir/*${collection}*.lock



# Get list of changes from queryChanges with progress timestamp as input
changes=$( mktemp -p $workDir )
$SCRIPT_PATH/queryChanges.sh $collection 0 | grep "^uuid" > $changes

# Cut list into pid/timestamp sets
# Iterate through list,

programs=$(wc -l < $changes)
echo "number of programs to transcode is $programs"

machineIndex=0
counter=1
while read uuid time; do
    echo "Transcoding program $counter of $programs"
    echo "Processing $uuid" "$time"
    ((counter++))

    started=0
    # Iterate over workers until one picks up the current uuid
    while [ $started -eq 0 ]; do
        [ $debug = 1 ] && echo "$uuid: Starting allocation to worker for uuid $uuid"
        for ((i=0;i<$WORKERS;i++)); do
            workerfile="$workDir/$i$collection.workerFile"
            [ $debug = 1 ] && echo "$uuid: Attempting to get lock on $workerfile"
            lockfile "$workerfile.lock"
            if [ ! -e $workerfile ]; then
                [ $debug = 1 ] && echo "$uuid: $workerfile does not exist, creating"
                touch $workerfile
                found=1
            else
                pid=$(cat $workerfile|cut -d' ' -f1)
                workeruuid=$(cat $workerfile|cut -d' ' -f2)
                workertime=$(cat $workerfile|cut -d' ' -f3)
                [ $debug = 1 ] && echo "$uuid: Got pid '$pid' from $workerfile"

                kill -0 $pid &> /dev/null
                found=$?
                if [ $found != 0 ]; then #workerfile found, but proces stopped
                    #the content of workerfile never completed. Add it to the incomplete list
                    echo $workeruuid $workertime >> $stateDir/$collection.incompletes
                    # TODO log this
                fi
            fi
            if [ $found != 0 ]; then # pid is no longer running or does not belong to us
                machine=${machines[$machineIndex]}
                [ $debug = 1 ] && echo "$uuid: Did not find found '$pid' among the running processes"
                [ $debug = 1 ] && echo "$uuid: Starting transcoding for $uuid and $time on $machine"
                $SCRIPT_PATH/transcoderWorker.sh $collection $uuid $time $machine $i &
                echo "$! $uuid $time $machine" > $workerfile
                # Increment the machine index
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
        echo
    done
    echo
done < $changes

while ( true ); do
    sleep 2
    workerFiles=$(ls -1 $workDir/*$collection.workerFile 2>/dev/null | wc -l)
    if [  $workerFiles -eq 0 ]; then
        break 1
    else
        [ $debug = 1 ] && echo "Waiting for $workerFiles"
    fi
done

# TODO find incompletes in successes and remove them from incompletes

# Merge state files here

echo "All transcoding finished. Exiting normally."
rm $changes
rmdir $globalLock
