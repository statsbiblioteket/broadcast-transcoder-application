# Broadcast Transcoder


Broadcast transcoder is a system, that transcodes radio-tv
programs from DOMS, based on the raw recordings stored in the Bitmagasin, into
files suitable to presentation in Mediestream.


## Enqueuing jobs

Firstly, run
    ./bin/enqueueJobs.sh Broadcast 0
This will query DOMS for all programs changed since timestamp 0, in the
Broadcast group. The Broadcast group is defined in bta.fetcher.Broadcast.properties

One can also run
     ./bin/enqueueJobs.sh Reklamefilm 0

This will do the same but for the Reklame film group, defined in
bta.fetcher.Reklamefilm.properties.

In any case, enqueueJobs.sh saves information in the database defined in
hibernate.cfg.xml, and marks each program as PENDING for transcoding.
The possible states are PENDING, COMPLETED, REJECTED and FAILED.

If a program already exists in the database, it is only marked as PENDING if the
timestamp of the change in DOMS is newer than the timestamp recorded in the
database.

Because of that, it should be safe to run enqueueJobs.sh repeatably, as the database will
not be updated if it already has the newest version.

## Transcoding jobs

Before starting a transcoding a queue file needs to be prepared.

To get all PENDING jobs from the database for a specific collection
    ./bin/queryChangesDoms.sh Broadcast

This will output one line for each PENDING job suitable as input for the
transcoder.
If there are no running transcoders using the intended queue file then simple
redirection can be used
    ./bin/queryChangesDoms.sh Broadcast > myqueue

If a transcoder is already actively using the queue file then it must be
updated using the queue.sh command to avoid corrupting the file
    OIFS=$IFS; IFS=$'\n'; \
    for job in $(./bin/queryChangesDoms.sh Broadcast); do \
    ./bin/queue.sh myqueue push "$job"; done; IFS=$OIFS

The transcoding is then started with
    ./bin/run_transcoder.sh -h localhost -n 2 -j myqueue

If run with no or wrong parameters it will output usage instructions.

As many instances of run_transcoder.sh can be run as needed, even on the same
host.
Both Broadcast and Reklamefilm jobs can be added to the same queue.

The number of concurrent jobs (-n) can be adjusted on the fly by sending a
SIGUSR1 (increase number) or SIGUSR2 (decrease number) signal to the process.
Sending a SIGINT (ctrl-c on the terminal) to the process will make it dump a
status to stdout:

    2013-02-25 12:51:46: requested number of concurrent jobs: 2
    2013-02-25 12:51:46: current running jobs: 2
    2013-02-25 12:51:46: processed jobs: 2
    2013-02-25 12:51:46: jobs left in queue: 17

Sending a SIGTERM or a SIGQUIT to the process will make it stop processing
the queue and wait for current running jobs to finish. Sending a second
SIGTERM/SIGQUIT will make the process forcibly kill off the worker processes
and exit.

## Handling old transcodings

If old transcodings already exists, the best way to handle them is to mark them as already transcoded.
As they are marked as transcoded, the system will not attempt to retranscode them until some meaningful
change happens in doms.
Generate a list from Fedora with the following query

select $object $timestamp
from <#ri>
where
$object <fedora-model:hasModel> <info:fedora/doms:ContentModel_Program>
and
$object <fedora-model:state> <fedora-model:Active>
and
$object <fedora-view:lastModifiedDate> $timestamp
and
$object <fedora-view:disseminates> $datastream
and
$datastream <fedora-view:disseminationType> <info:fedora/*/PROGRAM_STRUCTURE>

And store the contents (minus the header) in a file. Then invoke the
    ./markAsCompleted.sh listOfprograms
It will then create entries in the database for each program in that list.

After this enqueueJobs.sh can be run freely, as it will not update a record
if the doms timstamp is not newer than the database timestamp.

