#!/bin/bash

#The number of worker processes. If this is changed from a previous run then the
#cleanup loop needs to be executed by hand.
WORKERS=2

#These are external machines on which to run the transcoding process by ssh eg user1@encoder1 ....
#The special value "local" means "do the transcoding locally as the current user without ssh"
#
#It is a requirement that the installation directory for bta on the remote machine has the same absolute
#path as the local installation where the scripts run.
#
machines=( "local" )
# machines=( "bta@iapetus" )

#
# if progressFile does not exist, it will be created using the value in INITIAL_TIMESTAMP
#
## This is 2012-04-01
##INITIAL_TIMESTAMP=1333231200000

## This is 2012-12-16
INITIAL_TIMESTAMP=1355612400000

#
# Set debug=1 for debugging output from scripts, debug=0 for minimal output.
#
debug=1



#
# Nothing on or below this line should need to be changed for configuration purposes.
#

#
# This sets the SCRIPT_PATH to the directory where this script is found
#
SCRIPT_PATH=$(dirname $(readlink -f $0))
CLASSPATH="$SCRIPT_PATH/../lib/*"
logDir="$SCRIPT_PATH/.."
confDir="$SCRIPT_PATH/../config"


#
# collection can have values "Broadcast" or "Reklamefilm"
#
collection=$1
if [ "$collection" = "" ]; then
    collection="Broadcast"
fi

#
# if it exists, the progress file contains the last-modified timestamp of the last-transcoded program.
#
progressFile="$SCRIPT_PATH/../$collection.progress"

#
# File where failed transcodings are written in a format suitable for retranscoding with transcodeFile.sh
#
failureFile="$SCRIPT_PATH/../$collection.failures"


#
# if progressFile does not exist, it will be created using the value in INITIAL_TIMESTAMP
#
## This is 2012-04-01
##INITIAL_TIMESTAMP=1333231200000

## This is 2012-12-16
INITIAL_TIMESTAMP=1355612400000

if [ ! -e $progressFile ]; then
    echo $INITIAL_TIMESTAMP > $progressFile
fi


hibernate_log_config=" -Dcom.mchange.v2.log.MLog=com.mchange.v2.log.FallbackMLog  -Dcom.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL=WARNING "
