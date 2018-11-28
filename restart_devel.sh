#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $(readlink -f $BASH_SOURCE[0]))

develHost="bta@iapetus"
NPROCS=8

type="${1:-Broadcast}"
#Alternative is Reklamefilm

set +e
# Stop trancodering:
ssh $develHost '~/bta/bin/transcode-master.sh stop'
ssh $develHost "ps -ef | grep ffmpeg | grep ^bta | grep -v grep| sed 's/ \+/ /g' | cut -d' ' -f2 | xargs -r kill"
set -e

set -x
ssh $develHost 'rm -fr streamingContent/?/?/?/?/*.mp3'
ssh $develHost 'rm -fr streamingContent/?/?/?/?/*.flv'
ssh $develHost 'rm -fr streamingContent/?/?/?/?/*.mp4'
ssh $develHost 'rm -fr imageDirectory/?/?/?/?/*.png'

ssh $develHost 'rm -f logs/* || true'


#Initialisation
if [ "$type" == "Reklamefilm" ]; then
    echo "Reklamefilm transcoding"
    ssh $develHost "psql -d bta-devel -c 'update reklamefilmtranscodingrecord set transcodingstate=0 ;'"
    ssh $develHost "~/bta/bin/enqueueJobs.sh Reklamefilm 0"
    ssh $develHost '~/bta/bin/queryChangesDoms.sh Reklamefilm > ~/Reklamefilm_queue.txt'
    ssh $develHost "~/bta/bin/transcode-master.sh start -h localhost -n $NPROCS -j ~/Reklamefilm_queue.txt -v"
fi

if [ "$type" == "Broadcast" ]; then
    echo "Broadcast transcoding"
    ssh $develHost "psql -d bta-devel -c 'update broadcasttranscodingrecord set transcodingstate=0 ;'"
    ssh $develHost "~/bta/bin/enqueueJobs.sh Broadcast 0"
    ssh $develHost '~/bta/bin/queryChangesDoms.sh Broadcast > ~/Broadcast_queue$(date +"%y%m%d").txt'
    ssh $develHost 'head -n10 ~/Broadcast_queue$(date +"%y%m%d").txt > ~/Broadcast_queue.txt'
    ssh $develHost "~/bta/bin/transcode-master.sh start -h localhost -n $NPROCS -j ~/Broadcast_queue.txt -v"
fi

set +x

#Info to port prod programs to devel
#http://naiad:7880/fedora/objects?pid=true&label=true&title=true&identifier=true&terms=&query=label~*teracom*&maxResults=20#
#
#curl -H "Content-Type: text/xml" -ufedoraAdmin:fedoraAdminPass http://alhena:7980/fedora/objects/new -X POST --data-binary @objectXML1.xml -vvv

#Count transcoded programs
ssh $develHost "find ~/streamingContent/ -type f -path './?/?/?/?/*.mp?' | grep -v temp | wc -l"