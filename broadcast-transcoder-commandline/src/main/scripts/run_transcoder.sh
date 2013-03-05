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

# 0 is running status, 1 is stopped queue processing, 2 is active termination
shutdown=0
# Save the pid of this process for later use
this_pid=$$
# This process logs here (see redirection before calling main())
logfile=$logdir/run_transcoder.$this_pid.log

func_date()
{
    date "+%Y-%m-%d %H:%M:%S"
}

func_numjobs_inc()
{
    ((numjobs++))
    echo "$(func_date): Increase concurrent jobs to $numjobs"
}

func_numjobs_dec()
{
    [ $numjobs -gt 1 ] && ((numjobs--))
    echo "$(func_date): Decrease concurrent jobs to $numjobs"
}

func_joblist()
{
    pgrep -f -P $this_pid "ssh[ ]-n[ ]$host[ ]$transcode_cmd[ ].*"
}

func_status()
{
    local d=$(func_date)
    echo "$d: process pid: $this_pid"
    echo "$d: requested number of concurrent jobs: $numjobs"
    echo "$d: current running jobs: $(func_joblist|wc -l)"
    echo "$d: processed jobs: $jobcounter"
    echo "$d: jobs left in queue: $(wc -l < $jobfile)"
}

func_stop()
{
    local pid
    # Only enter if we are not in state 2
    if [ $shutdown -ne 2 ]; then
	if [ $shutdown -eq 0 ]; then
	    shutdown=1
	    echo "$(func_date): Stopped queue processing, waiting for jobs to finish"
	    func_status
	else
	    shutdown=2
	    # We've already been called once, so this time kill off children
	    # Note that just killing ssh will leave the transcode process
	    # a zombie waiting to be reaped by init
	    #
	    # Further note: the java transcode process has the responsibility of updating the database
	    # when a transcoding has been successfully completed. So any processes killed here will eventually
	    # be requeued the next time we query the database.
	    #
	    #
	    echo "$(func_date): Terminating current jobs"
	    func_status
	    for pid in $(func_joblist)
	    do
		kill $pid
	    done
	fi
    fi
}

main()
{
    # Workers log here
    transcode_cmd_log=$logdir/${host}.job.log
    # The number of currently running jobs on $host
    current_jobs=0
    # Keep track of how many jobs we've forked
    jobcounter=0
    # As long as we get a job from the queue, we keep running
    job="go"
    while [ -n "$job" -a $shutdown -eq 0 ]
    do
        job="$($queue_cmd $jobfile pop)"
        set -- $job
        collection=$1
        uuid=$2
        timestamp=$3
        # Check that we didn't get a malformed job from the queue
        if [ -n "$collection" -a -n "$uuid" -a -n "$timestamp" ]; then
            ssh -n $host $transcode_cmd $collection $uuid $timestamp >> $transcode_cmd_log 2>&1 &
            ((current_jobs++))
            ((jobcounter++))
        fi
        while [ $current_jobs -ge $numjobs ]
        do
            # Update the running job count
            current_jobs=$(func_joblist|wc -l)
            # Check every second if there is a free slot
            sleep 1
        done
    done
    # Wait out running jobs
    # Exit status greater than 128 means wait was aborted by a trap
    while ! wait
    do
	    wait
    done
    func_status
    echo "$(func_date): Exiting"
}

print_usage()
{
    echo "Usage: $(basename $0) -h <host> -n <number of jobs> -j <jobfile>"
    echo
    echo "-h	Specify host for transcoding jobs"
    echo "-n	Number of concurrent transcoding jobs on host"
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
	    n)
		numjobs=$OPTARG
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
    shift `expr $OPTIND - 1`
else
    print_usage
    exit 1
fi

# Check that we got the args we wanted
[ -z "$host" ] && print_usage && exit 2
[ -z "$numjobs" ] && print_usage && exit 3
[ -z "$jobfile" -o ! -r "$jobfile" ] && print_usage && exit 4

# Still here...

# Setup traps
trap 'func_stop' SIGQUIT SIGTERM
trap 'func_status' SIGINT
trap 'func_numjobs_inc' SIGUSR1
trap 'func_numjobs_dec' SIGUSR2

# Save all output to the logfile aswell
exec > >(tee $logfile) 2>&1

# off we go!
main
