#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $(readlink -f $BASH_SOURCE[0]))


develHost="bta@iapetus"

set +e
# Stop trancodering:
ssh $develHost '~/bta/bin/transcode-master.sh stop'
ssh $develHost "ps -ef | grep ffmpeg | grep ^bta | grep -v grep| sed 's/ \+/ /g' | cut -d' ' -f2 | xargs -r kill"
set -e

ssh $develHost 'rm -f streamingContent/*.mp3'
ssh $develHost 'rm -f streamingContent/*.flv'
ssh $develHost 'rm -f streamingContent/*.mp4'
ssh $develHost 'rm -f imageDirectory/*.png'

ssh $develHost 'rm -f logs/* || true'


#Initialisation
ssh $develHost "psql -d bta-devel -c 'update broadcasttranscodingrecord set transcodingstate=0 ;'"
ssh $develHost "~/bta/bin/enqueueJobs.sh Broadcast 0"
ssh $develHost '~/bta/bin/queryChangesDoms.sh Broadcast > ~/queue$(date +"%y%m%d").txt'
ssh $develHost 'head -n100 ~/queue$(date +"%y%m%d").txt > ~/queue.txt'
ssh $develHost '~/bta/bin/transcode-master.sh start -h localhost -n 8 -j ~/queue.txt -v'



#Info to port prod programs to devel
#http://naiad:7880/fedora/objects?pid=true&label=true&title=true&identifier=true&terms=&query=label~*teracom*&maxResults=20#
#
#curl -H "Content-Type: text/xml" -ufedoraAdmin:fedoraAdminPass http://alhena:7980/fedora/objects/new -X POST --data-binary @objectXML1.xml -vvv