#!/bin/bash


SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

collection=$1
if [ "$collection" = "" ]; then
    collection="broadcast"
fi

## This is 2012-04-01
INITIAL_TIMESTAMP=1333231200000

debug=1

#The number of worker processes. If this is changed from a previous run then the
#cleanup loop needs to be executed by hand.
WORKERS=4

machines=( "machine1" "machine2" )



#Cleanup from previous run
for ((i=0;i<$WORKERS;i++)); do
            workerfile="${i}workerFile"
            if [ -e  $workerfile ]; then
              uuid=$(cat $workerfile|cut -d ' ' -f2)
              timestamp=$(cat $workerfile|cut -d ' ' -f3)
              machine=$(cat $workerfile|cut -d ' ' -f4)

              lockfile "$SCRIPT_PATH/../fails.lock"
                      echo "$uuid   $timestamp" >> $SCRIPT_PATH/../fails
              rm -f "$SCRIPT_PATH/../fails.lock"

              rm $workerfile
              $SCRIPT_PATH/cleanupUnfinished.sh $uuid $timestamp $machine
            fi
done
rm -f $SCRIPT_PATH/../*.lock *.lock *workerFile

if [ ! -e "$SCRIPT_PATH/../progress" ]; then
    echo $INITIAL_TIMESTAMP > $SCRIPT_PATH/../progress
fi

#get list of changes from queryChanges with progress timestamp as input
timestamp=$(cat $SCRIPT_PATH/../progress | tail -1)
changes=$(mktemp)
$SCRIPT_PATH/queryChanges.sh $collection $timestamp > $changes

#cut list into pid/timestamp sets
#iterate through list,

echo "number of programs to transcode is $(cat $changes | wc -l)"

machineIndex=0
while read line; do
    uuid=$(echo $line | cut -d' ' -f1)
    time=$(echo $line | cut -d' ' -f2)
        #Skip until a line starts with uuid
    if [[ $uuid != uuid:* ]]; then
        continue
    fi
    while [ 1 ]; do
            [ $debug = 1 ] && echo "$uuid: Starting allocation to worker for uuid $uuid"
            for ((i=0;i<$WORKERS;i++)); do
                    workerfile="${i}workerFile"
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
                        $SCRIPT_PATH/transcodeFile.sh $collection $uuid $time $machine &
                        echo "$! $uuid $time $machine" > $workerfile

                            #increment the machine index
                            ((machineIndex++))
                            max=${#machines[*]}
                            [ $machineIndex -ge $max ] && machineIndex=0

                        [ $debug = 1 ] && echo "$uuid: writing info to $workerfile"
                        [ $debug = 1 ] && echo "$uuid: releasing lock on $workerfile and go to next line"
                        rm -f "$workerfile.lock"
                        break 2
                    fi
                    [ $debug = 1 ] && echo "$uuid: releasing lock on $workerfile"
                    rm -f "$workerfile.lock"
            done
            sleep 10s
            echo
    done
    echo
done < $changes

rm $changes


