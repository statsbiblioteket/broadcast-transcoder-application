#!/bin/bash
#

SCRIPT_DIR=$(dirname $(readlink -f $BASH_SOURCE[0]))

echo "$(date "+%Y-%m-%d %H:%M:%S"): Starting transcode for collection: $1, of $2 with timestamp: $3"

sleep $[ ( $RANDOM % 60 )  + 1 ]
