#!/bin/bash

# Settings file for the java programs included in BTA

# This script expects SCRIPT_PATH to be defined, if it is not then notify
# and error out
if [ "x$SCRIPT_PATH" = "x" ]; then
    echo "$(basename $0): FATAL ERROR: \$SCRIPT_PATH is not set"
    exit 1
fi

# Set CLASSPATH for the JVM
CLASSPATH="$SCRIPT_PATH/../lib/*"
# Config is here
confDir="$SCRIPT_PATH/../config"

###############################################################################
# Nothing on or below this line should need to be changed for configuration purposes.

# collection can have values "Broadcast" or "Reklamefilm"
# if it is unset or empty then we cannot proceed
collection=${collection:-<undefined>}
case $collection in
    Broadcast|Reklamefilm)
	# A valid collection given
	;;
    *)
	echo "ERROR: $collection is not a valid collection name"
	exit 1
	;;
esac

# Log configuration for hibernate passed as parameters to the JVM
hibernate_log_config="-Dcom.mchange.v2.log.MLog=com.mchange.v2.log.FallbackMLog -Dcom.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL=WARNING"

# Enable the sb-btatools1 SCL to get vlc 1.1.x (also includes ffmpeg 0.10)
source scl_source enable sb-btatools1

# Source this to get a newer ffmpeg (not an SCL)
source /opt/ffmpeg40/enable