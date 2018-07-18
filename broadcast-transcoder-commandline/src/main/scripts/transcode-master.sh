#!/bin/bash
#
# This is a master script to control the transcode jobs
# It will fork, count and stop jobs if asked to

# What transcode command to call
transcode_cmd=$HOME/services/bin/run_transcoder.sh

# Source optional config file
SCRIPT_PATH=$(dirname $(readlink -f $BASH_SOURCE[0]))
configfile=${SCRIPT_PATH}/../config/transcode-master.conf
[ -r $configfile ] && . $configfile

# Check if we are running with a tty
tty -s
notty=$?

# Show processlist with status?
verbose=0

# Informational messages?
quiet=0

## Simple global scope hashmap for bash < 4 using the alias builtin
# http://stackoverflow.com/questions/688849/associative-arrays-in-shell-scripts
# Usage: map_put map_name key value
map_put()
{
    alias "${1}$2"="$3"
}
# Usage: map_get map_name key
map_get()
{
    # If alias does not exist we return an empty string
    alias "${1}$2" 2>/dev/null | awk -F "'" '{ print $2; }';
}
# Usage: map_keys map_name 
map_keys()
{
    alias -p | grep $1 | cut -d'=' -f1 | awk -F"$1" '{print $2; }'
}
##

# Raw pgrep output of running jobs
func_joblist()
{
    pgrep -f -l -u bta "$transcode_cmd[ ]-h[ ].*[ ]-j[ ].*"
}

# Find all running transcode jobs and construct a hashmap
# of the information with host as key
func_jobsummary()
{
    local numjobs pid bash transcode_cmd dashh host dashj queuefile
    while read pid bash transcode_cmd dashh host dashj queuefile
    do
	numjobs=$(map_get jobs $host)
	[ -z "$numjobs" ] && numjobs=0	# Initialize counter if we have not seen this host before
	((numjobs++))
	map_put jobs $host $numjobs
    done < <(func_joblist)
}

# Output job status
# Note that it uses the $host global
func_status()
{
    local h pids
    # Tally current jobs
    func_jobsummary
    if [ -n "$host" ]; then
        pids=$(func_joblist | awk "/$host/"'{ print $1 }')
        echo "$host is currently running $(map_get jobs $host) jobs ($(echo $pids))"
        [ $verbose -eq 1 ] && func_joblist
    else
	for h in $(map_keys jobs)
	do
            pids=$(func_joblist | awk "/$h/"'{ print $1 }')
            echo "$h is currently running $(map_get jobs $h) jobs ($(echo $pids))"
	done
        [ $verbose -eq 1 ] && func_joblist
    fi
}

# Output a . with a small delay after
print_dot()
{
    if [ $notty -eq 0 -a $quiet -eq 0 ]; then
	echo -n "."
	sleep 0.1
    fi
}

print_usage()
{
    echo "Usage: $(basename $0) start|stop|status [-h <host> -n <number of jobs> -j <jobfile> -v -q]"
    echo
    echo "start   Start new jobs, requires -h, -n and -j"
    echo "stop    Stop jobs, optionally takes -h"
    echo "status  Summarize currently running jobs, optionally takes -h"
    echo
    echo "-h  Specify host for transcoding jobs"
    echo "-n  Number of concurrent transcoding jobs on host"
    echo "-j  File to pick jobs from"
    echo "-v  Give verbose status"
    echo "-q  No informational messages from start|stop"
    echo
    echo "Settings will be sourced from this file if it exists:"
    echo "$configfile"
    echo
}

# Determine mode before we start getopt processing
case $1 in
    start|stop|status)
	mode=$1
	;;
    *)
	print_usage
	exit 1
	;;
esac
# Remove first arg before going to getopt processing
shift
# Process the rest of the arguments with getopt
if [ $# -gt 0 ]; then
    while getopts h:n:j:vq opt
    do
	case $opt in
	    h)
		host=$OPTARG
		;;
	    n)
		numjobs=$OPTARG
		;;
	    j)
		jobfile=$OPTARG
		;;
            v)
                verbose=1
                ;;
            q)
                quiet=1
                ;;
	    \?)
		print_usage
		exit 1
		;;
	esac
    done
    shift $((OPTIND - 1))
fi

# Check that we got the args we wanted
case $mode in
    start)
	[ -z "$host" ] && print_usage && exit 2
	[ -z "$numjobs" ] && print_usage && exit 3
	[ -z "$jobfile" -o ! -r "$jobfile" ] && print_usage && exit 4
	# Tally current jobs
	func_jobsummary
	currentjobs=$(map_get jobs $host)
	[ -z "$currentjobs" ] && currentjobs=0
	if [ $currentjobs -lt $numjobs ]; then
	    startjobs=$((numjobs - currentjobs))
	else
	    [ $quiet -eq 0 ] && echo "$currentjobs jobs already running on $host, not starting any more"
	    exit 5
	fi
	[ $quiet -eq 0 ] && echo "Starting $startjobs jobs on $host"
	for ((x=1; x <= $startjobs; x++))
	do
	    print_dot
	    $transcode_cmd -h $host -j $jobfile &
	done
	[ $notty -eq 0 -a $quiet -eq 0 ] && echo
	;;
    stop)
	# Missing, implement support for -n to reduce number of jobs
	pids=$(func_joblist | awk "/$host/"'{ print $1 }')
	func_status
	[ $quiet -eq 0 ] && echo "Shutting them down"
	for pid in $pids
	do
	    print_dot
	    kill $pid
	done
	[ $notty -eq 0 -a $quiet -eq 0 ] && echo
	;;
    status)
	func_status
	;;
esac

# vim: set sw=4 sts=4 et ft=sh : #
