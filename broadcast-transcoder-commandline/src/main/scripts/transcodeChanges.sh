#!/bin/bash


SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

#TODO start by looking for hanging workfiles from a previous run, and from these parse the uuids into "fails"

#get list of changes from queryChanges with progress timestamp as input
if [ -r "$SCRIPT_PATH/../progress" ]; then
    timestamp=$(cat $SCRIPT_PATH/../progress | tail -1)
fi
if [ -z "$timestamp" ]; then
    timestamp="0"
fi
changes=$(mktemp)
$SCRIPT_PATH/queryChanges.sh $timestamp > $changes

#cut list into pid/timestamp sets
#iterate through list, write timestamp back into progress file

while read line; do
    time=$(echo $line | cut -d' ' -f2)
    pid=$(echo $line | cut -d' ' -f1)

    $SCRIPT_PATH/delegateWork.sh $pid $time
    returncode=$?
##TODO Does this work? The concurrent programs finish in random order so this program is not necessarily later than the
##time currently in "progress".[csr]
    echo $time > $SCRIPT_PATH/../progress

##TODO This doesn't work. Move this to transcodeFile.
    if [ $returncode -ne 0 ]; then
        # if file fails, write pid/timestamp combo to fails
        echo $line >> $SCRIPT_PATH/../fails
    fi
done < $changes

rm $changes


