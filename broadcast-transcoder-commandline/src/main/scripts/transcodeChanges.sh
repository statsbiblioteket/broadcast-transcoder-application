#!/bin/bash

collection=$1

# Get settings
SCRIPT_PATH=$(dirname $(readlink -f $0))
source ${SCRIPT_PATH}/setenv.sh ${collection}

# Ensure only one copy of this script runs for a given collection
globalLock=$workDir/transcodeChanges.${collection}.lockdir
if ! mkdir $globalLock 2>/dev/null; then
    echo "$(basename $0): ERROR this script is already running for collection $collection"
    exit 1
fi

# Cleanup from previous run

function getWorkerfile(){
   local workerID=$1
   local collection$2
   echo  "${workDir}/${i}${collection}.workerFile"

}
function checkWorkerfile(){
    local workerfile=$1
    local found=0

    if [ ! -e "${workerfile}" ]; then #worker id is free
        touch ${workerfile}
        found=1
    else #worker id is occupied
        local pid=$(cat ${workerfile}|cut -d' ' -f1)
        local workeruuid=$(cat ${workerfile}|cut -d' ' -f2)
        local workertime=$(cat ${workerfile}|cut -d' ' -f3)

        if [ -n "$pid" ]; then
                kill -0 ${pid} &> /dev/null
                found=$?
        else
                found=1
        fi
    fi
    echo ${found}
}



function transcoderWorker() {

    local collection=$1
    local uuid=$2
    local timestamp=$3
    local machine=$4
    local workerID=$5

    # Determine if we run local or remote
    if [ "${machine}" = "local" ]
    then
       SSH_COMMAND=""
    else
       SSH_COMMAND="ssh ${machine}"
    fi

    # Run transcode
    
    ${SSH_COMMAND} ${SCRIPT_PATH}/transcodeFile.sh "${collection}" "${uuid}" "${timestamp}" "${machine}" &> /dev/null


    returncode=$?
    return ${returncode}
}


function startWorker(){

    local collection=$1
    local uuid=$2
    local time=$3
    local machine=$4
    local workerID=$5
    local workerfile=$6

    transcoderWorker $@ &
    echo "$! ${uuid} ${time} ${machine}" > ${workerfile}

}


function getNextMachine(){
    if [ -z "${machineIndex}" ]; then
        machineIndex=0
    fi
    machine=${machines[$machineIndex]}
    ((machineIndex++))
    max=${#machines[*]}
    [ ${machineIndex} -ge ${max} ] && machineIndex=0
    echo ${machine}
}


trap 'rmdir $globalLock; rm -rf $changes; exit 1' INT TERM

# Get list of changes from queryChanges with progress timestamp as input
changes=$( mktemp -p ${workDir} )
${SCRIPT_PATH}/queryChangesDoms.sh ${collection} | grep "^uuid" > ${changes}

# Cut list into pid/timestamp sets
# Iterate through list,

programs=$(wc -l < ${changes})
echo "number of programs to transcode is ${programs}"


counter=0
while read uuid timestamp rest; do
    ((counter++))

    while true ; do # Iterate over workers until one picks up the current uuid
        echo "$(date) ${uuid}: Attempting allocation to worker, ${counter} of ${programs}"
        for ((i=0;i<${WORKERS};i++)); do

            workerfile=$(getWorkerfile ${i} ${collection})
            running=$(checkWorkerfile ${workerfile})
            if [ ! ${running} == 0 ]; then # pid is no longer running or does not belong to us
                machine=$(getNextMachine)
                echo "$(date) ${uuid}: Starting transcoding for ${uuid} and ${time} on ${machine}"
                startWorker ${collection} ${uuid} ${timestamp} ${machine} ${i} ${workerfile}
                break 2
            fi
        done
        sleep 10
    done

done < ${changes}
rm ${changes}


# Now everything have been started. Wait for the last workers to finish

while true; do
    sleep 2
    workersRunning=0
    for ((i=0;i<${WORKERS};i++)); do
        workerfile=$(getWorkerfile ${i} ${collection})
        if [ -e ${workerfile} ]; then
            running=$(checkWorkerfile ${workerfile})
            if [ ! ${running} ]; then
                 rm ${workerfile}
            else
               workersRunning=${workersRunning}+1
            fi
        fi
    done
    [ ${workersRunning} == 0 ] && break
done



echo "All transcoding finished. Exiting normally."
rmdir $globalLock