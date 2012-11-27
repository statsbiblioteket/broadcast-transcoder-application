#!/bin/bash

SCRIPT_PATH=$(pwd) #TODO

programPid=$1
timestamp=$2

java BroadcastTranscoderApplication \
 --hibernate_configfile=$SCRIPT_PATH/../bta.$(hostname).hibernate.cfg.xml\
 --infrastructure_configfile=$SCRIPT_PATH/../bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../bta.behaviour.properties \
 --programpid=$programPid \
 --timestamp=$timestamp

exit $?