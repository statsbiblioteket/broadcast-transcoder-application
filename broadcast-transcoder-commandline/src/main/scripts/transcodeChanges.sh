#!/bin/bash

collection=$1

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source $SCRIPT_PATH/setenv.sh $collection

# Ensure only one copy of this script runs for a given collection
globalLock=$workDir/transcodeChanges.${collection}.lockdir
if mkdir transcodeChanges.${collection}.lock 2>/dev/null; then
    # We're good
    echo -n
else
    # Fail, another copy is running
    echo "$(basename $0): ERROR this script is already running for collection $collection"
    exit 1
fi

# Cleanup from previous run
for ((i=0;i<$WORKERS;i++)); do
    workerfile="$workDir/$i.$collection.workerFile"
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
##
# This is probably not necessary, unless we have an unusual race condition that leaves lockfiles behind from a
# previous run.
rm -f $workDir/*${collection}*.lock

# Get list of changes from queryChanges with progress timestamp as input
timestamp=$(cat $progressFile)
changes=$( mktemp -p $workDir )
$SCRIPT_PATH/queryChanges.sh $collection $timestamp | grep "^uuid" > $changes

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
	    fi

	    pid=$(cat $workerfile|cut -d' ' -f1)
	    [ $debug = 1 ] && echo "$uuid: Got pid '$pid' from $workerfile"

	    kill -0 $pid &> /dev/null
	    found=$?

	    if [ $found != 0 ]; then # pid is no longer running or does not belong to us
		machine=${machines[$machineIndex]}
		[ $debug = 1 ] && echo "$uuid: Did not find found '$pid' among the running processes"
		[ $debug = 1 ] && echo "$uuid: Starting transcoding for $uuid and $time on $machine"
		$SCRIPT_PATH/transcoderWorker.sh $collection $uuid $time $machine &
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

rm $changes
rm $workDir/*${collection}.workerFile
rm -rf $globalLock
