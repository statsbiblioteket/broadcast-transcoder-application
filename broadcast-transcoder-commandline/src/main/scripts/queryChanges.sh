#!/bin/bash

SCRIPT_PATH=$(pwd) #TODO

timestamp=$1

java dk.statsbiblioteket.broadcasttranscoder.fetcher.BtaDomsFetcher \
 --infrastructure_configfile=$SCRIPT_PATH/../bta.infrastructure.properties \
 --behavioural_configfile=$SCRIPT_PATH/../bta.behaviour.properties \
 --since=$timestamp

