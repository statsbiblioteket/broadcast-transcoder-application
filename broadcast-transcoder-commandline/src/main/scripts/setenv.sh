#!/bin/bash

# The number of worker processes. If this is changed from a previous run then the
# cleanup loop needs to be executed by hand.
WORKERS=2

# These are external machines on which to run the transcoding process by ssh eg user1@encoder1 ....
# The special value "local" means "do the transcoding locally as the current user without ssh"
#
# It is a requirement that the installation directory for bta on the remote machine has the same absolute
# path as the local installation where the scripts run.
# machines=( "bta@iapetus" )
machines=( "local" )

# If progressFile does not exist, it will be created using the value in INITIAL_TIMESTAMP
#
# This is 2012-04-01
#INITIAL_TIMESTAMP=1333231200000
# This is 2012-12-16
INITIAL_TIMESTAMP=1355612400000

#
# Set debug=1 for debugging output from scripts, debug=0 for minimal output.
#
debug=0



# This script expects SCRIPT_PATH to be defined, if it is not then notify
# and error out
if [ "x$SCRIPT_PATH" = "x" ]; then
    echo "$(basename $0): FATAL ERROR: \$SCRIPT_PATH is not set"
    exit 1
fi

# Set CLASSPATH for the JVM
CLASSPATH="$SCRIPT_PATH/../lib/*"
# Logs go here
logDir="$HOME/logs"
# Config is here
confDir="$SCRIPT_PATH/../config"
# work-in-progress logs and other temporary files like .lock files go here
workDir=$HOME/var/work
# stateDir, this holds permanent state, such as the various progress files
stateDir=$HOME/var/state

###############################################################################
# Nothing on or below this line should need to be changed for configuration purposes.

# Make sure directories exist
for setenv_mkdir in $logDir $workDir $stateDir; do
    [ ! -d "$setenv_mkdir" ] && mkdir -p "$setenv_mkdir"
done

# collection can have values "Broadcast" or "Reklamefilm"
collection=$1
case $collection in
    Broadcast|Reklamefilm)
	# A valid collection given
	;;
    cleanup)
	# call came from cleanupUnfinished.sh, no collection is okay
	;;
    *)
	echo "ERROR: $collection is not a valid collection name"
	exit 1
	;;
esac

# If it exists, the progress file contains the last-modified timestamp of the last-transcoded program.
progressFile="$stateDir/$collection.progress"

# File where failed transcodings are written in a format suitable for retranscoding with transcodeFile.sh
failureFile="$stateDir/$collection.failures"

# Initialize progressfile if it does not exist
if [ ! -e $progressFile ]; then
    echo $INITIAL_TIMESTAMP > $progressFile
fi

# Log configuration for hibernate passed as parameters to the JVM
hibernate_log_config="-Dcom.mchange.v2.log.MLog=com.mchange.v2.log.FallbackMLog -Dcom.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL=WARNING"
