#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"


collection=$1
timestamp=$2


java -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.fetcher.BtaDomsFetcher \
 --infrastructure_configfile=$SCRIPT_PATH/../conf/bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../conf/bta.$collection.properties \
 --since=$timestamp

