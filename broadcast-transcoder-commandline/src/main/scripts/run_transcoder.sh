#!/bin/bash
#
# Start a transcoder running
#

# Do not rely on some random key in a ssh agent
unset SSH_AUTH_SOCK

# What transcode command to call
transcode_cmd=$HOME/services/bin/dummy_worker.sh
# What queue command to use
queue_cmd=$HOME/services/bin/queue.sh
# Logs go here
logdir=$HOME/logs

# Source optional config file
configfile=$(readlink -f $(dirname $(readlink -f $0))/../config/run_transcoder.conf)
[ -r $configfile ] && . $configfile

# Keep track of how many jobs we've forked
jobcounter=0

func_date()
{
    date "+%Y-%m-%d %H:%M:%S"
}

# rotate_log(): rotate a logfile
# params: $1 = logfile to rotate, $2 = max logsize which determines rotation
# $2 is optional and defaults to 10M
# The number of log generations are fixed to 8
rotate_log()
{
    # Keep 8 log generations
    local numlogs=8

    local logfile=$1
    # Default is 10485760 bytes, 10M
    local maxlogsize=${2:-10485760}

    # Multiple instances could be running in parallel so we need to grab a lock
    # to avoid race conditions
    (
        flock 200
        # Should we rotate?
        if [ -r "$logfile" ] && [ "$(stat -c %s $logfile)" -ge $maxlogsize ]; then
            # Rotate log
            for x in $(seq $numlogs -1 1)
            do
                [ -r ${logfile}.$x ] && mv ${logfile}.$x ${logfile}.$((x+1))
            done
            [ -r ${logfile} ] && mv ${logfile} ${logfile}.1
        fi
    ) 200> ${logfile}.lck
}

func_stop()
{
    echo "$(func_date): pid $$ exiting, jobs processed from $jobfile: $jobcounter" >> $logfile
}

main()
{
    echo "$(func_date): pid $$ startup using $jobfile" >> $logfile
    # As long as we get a job from the queue, we keep running
    job="go"
    while [ -n "$job" ]
    do
        job="$($queue_cmd $jobfile pop)"
        set -- $job
        collection=$1
        uuid=$2
        timestamp=$3

        # Check that we didn't get a malformed job from the queue
        if [ -n "$collection" -a -n "$uuid" -a -n "$timestamp" ]; then
            ((jobcounter++))
	    # Run job in the foreground so the loop naturally waits for completion
            ssh -n $host $transcode_cmd $collection $uuid $timestamp >> $logfile 2>&1
	    # rotate log if necessary
	    rotate_log "$logfile"
        fi
    done
}

print_usage()
{
    echo "Usage: $(basename $0) -h <host> -j <jobfile>"
    echo
    echo "-h	Specify host for transcoding job"
    echo "-j	File to pick jobs from"
    echo
    echo "The format of the jobs returned from jobfile should be:"
    echo "<collection> <uuid> <timestamp>"
    echo
    echo "Settings will be sourced from this file if it exists:"
    echo "$configfile"
    echo
}

# Let's get this party started
if [ $# -gt 0 ]; then
    while getopts h:n:j: opt
    do
	case $opt in
	    h)
		host=$OPTARG
		;;
	    j)
		jobfile=$OPTARG
		;;
	    \?)
		print_usage
		exit 1
		;;
	esac
    done
    shift $((OPTIND - 1))
else
    print_usage
    exit 1
fi

# Check that we got the args we wanted
[ -z "$host" ] && print_usage && exit 2
[ -z "$jobfile" -o ! -r "$jobfile" ] && print_usage && exit 4

# Setup trap
trap 'func_stop' EXIT

# This process logs here
logfile=$logdir/${host}.job.log

# off we go!
main

# vim: set sw=4 sts=4 et ft=sh : #
