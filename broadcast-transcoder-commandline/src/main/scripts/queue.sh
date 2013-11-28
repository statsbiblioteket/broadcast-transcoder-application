#!/bin/bash
#
# Very simple filebased push/pop queue
#

# No interruptions please
trap '' INT

qfile=$1
cmd=$2
input=$3
lockfile=${qfile}.lck

print_usage()
{
    echo "Usage: $(basename $0) <queuefile> push <item>|pop|clear"
    echo
    echo "push   will append items to the queue file"
    echo "pop    will get the first item from the queue file"
    echo "clear  will empty the queuefile (use carefully!)"
    echo
}

get_lock()
{
    lockfile $lockfile
}

release_lock()
{
    rm -f $lockfile
}

if [ $# -lt 2 ]; then
    print_usage
    exit 1
fi

# Queue file does not exist, or is unreadable?
if [ ! -r $qfile ]; then
    if ! touch $qfile; then
	echo "FATAL ERROR: Unable to accces queuefile: $qfile"
	exit 1
    fi
fi

case $cmd in
    push)
	get_lock
	[ -n "$input" ] && echo "$input" >> $qfile
	release_lock
	;;
    pop)
	get_lock
	head -n 1 $qfile
	sed -i '1d' $qfile
	release_lock
	;;
    clear)
	get_lock
	: > $qfile
	release_lock
	;;
    *)
	echo "Unknown command: $cmd"
	exit 1
	;;
esac
