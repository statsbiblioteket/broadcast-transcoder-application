#!/bin/bash

#
# Given the name of a flash video file, prints out the filename and corresponding start-time and channel.
#

flashFileName=$1
SCRIPT_PATH=$(dirname $(readlink -f $0))
source ${SCRIPT_PATH}/settings.sh

get_program_pid() {
   echo $1 | sed -rn 's/(.*).flv/uuid:\1/p'
}

get_program_info() {
url=http://naiad:7880/fedora/objects/${1}/datastreams/PROGRAM_BROADCAST/content
    wget -q --user=fedoraReadOnlyAdmin --password=${DOMS_PWD} -O   - ${url} |sed -rn 's/(.*timeStart>(.*)<.*)|  (.*channelId>(.*)<.*)/\4\2/p'|tr '\n' ' '|xargs echo
}

pid=$(get_program_pid $flashFileName)
echo $pid $(get_program_info $pid)|grep '[0-9]\{4\}-[0-9]\{2\}-[0-9]\{2\}'
