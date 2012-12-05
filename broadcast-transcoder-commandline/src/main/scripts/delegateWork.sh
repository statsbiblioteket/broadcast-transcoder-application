#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
WORKERS=5

#TODO Surely this pid is the uuid of the program but the pid defined below is the process id of the transcoding job!!!
pid=$1
time=$2


#TODO: this in some locking mechanish

while [ 1 ]; do
        for ((i=0;i<$WORKERS;i++)); do
            workerfile="${i}workerFile"
            if [ -e $workerfile ]; then
                pid=$(cat $workerfile)
                ps ax | grep -v grep | cut -d' ' -f1 | grep $pid
                found=$?
                if [ $found != 0 ]; then
                    rm $workerfile
                fi
            fi
            if [ ! -e $workerfile ]; then
                touch $workerfile
                $SCRIPT_PATH/transcodeFile.sh $pid $time &
                echo $! > $workerfile
                break 2
            fi
        done
        sleep 10
        echo
done 



