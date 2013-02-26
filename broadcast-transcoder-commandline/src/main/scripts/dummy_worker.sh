#!/bin/bash
#

echo "$(date "+%Y-%m-%d %H:%M:%S"): Starting transcode for collection: $1, of $2 with timestamp: $3"

sleep $[ ( $RANDOM % 60 )  + 1 ]
