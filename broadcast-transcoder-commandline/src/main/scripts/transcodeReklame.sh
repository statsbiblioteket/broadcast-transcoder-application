#!/bin/bash

SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"

uuid=$1
timestamp=$2
machine=$3
collection=reklamer


java -Dlogback.configurationFile=$SCRIPT_PATH/../config/logback.xml  -cp "$CLASSPATH" dk.statsbiblioteket.broadcasttranscoder.ReklamefilmTranscoderApplication \
 --hibernate_configfile=$SCRIPT_PATH/../config/hibernate.cfg.xml\
 --infrastructure_configfile=$SCRIPT_PATH/../config/bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../config/bta.behaviour.properties \
 --programpid=$uuid \
 --timestamp=$timestamp

returncode=$?

## Consider placing progress, successes, failures outside deploy directory so they don't get nuked by deploy.

if [ $returncode -ne 0 ]; then
    failureFile= $SCRIPT_PATH/../$collection.failures
    lockfile "$failureFile.lock"
        echo "$uuid   $timestamp $machine" >> $failureFile
    rm -f "$failureFile.lock"
fi

exit $returncode