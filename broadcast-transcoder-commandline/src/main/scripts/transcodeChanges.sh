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
[ $debug = 1 ] && echo Cleaning Up
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
	lockfile "$failureFile.lock"
	echo "$collection $uuid $timestamp" >> $failureFile
	rm -f "$failureFile.lock"
	rm $workerfile
	$SCRIPT_PATH/cleanupUnfinished.sh $uuid $timestamp $machine
	lockfile "$progressFile.lock"
    current_progress_timestamp=$(cat $progressFile)
    #
    # Check whether the process to be cleaned up is from an earlier timestamp and,
    # if necessary, set the clock of progress back.
    #
    if [ $timestamp -lt $current_progress_timestamp ]; then
       echo $(( $timestamp - 1)) > $progressFile
    fi
    echo  "$collection" "$uuid" "$timestamp"  >> $stateDir/$collection.successes
    rm -f "$progressFile.lock"
    fi
done
}

cleanup;
trap 'cleanup; rmdir $globalLock; rm -r $changes; exit 1' INT TERM

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

all_finished=$(false)
while ( !all_finished ); do
   sleep 2
   workerFiles="$workDir/*$collection.workerFile"
   set $workerFiles
   if [ $# -eq 0 ]; then
       all_finished=$(true)
   else
      for workerfile in $workerFiles; do
         pid=$(cat $workerfile|cut -d' ' -f1)
         kill -0 $pid &> /dev/null
	     found=$?
	     if [ $found != 0 ]; then
	         [ $debug = 1 ] && echo "$uuid: Did not find '$pid' among the running processes"
             [ $debug = 1 ] && echo "Deleting worker file $workerfile"
             rm $workerfile
	     fi
      done
   fi
done
echo "All transcoding finished. Exiting normally."
rm $changes
rmdir $globalLock
