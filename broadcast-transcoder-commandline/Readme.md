# Broadcast Transcoder


Broadcast transcoder is (surprise) a system, that transcoded radio-tv
programs from DOMS, based on the raw recordings stored in the Bitmagasin, into
files suitable to presentation in Mediestream.


## Enqueuing jobs

Firstly, run
    ./bin/enqueueJobs.sh Broadcast 0
This will query DOMS for all programs changed since timestamp 0, in the
Broadcast group. The Broadcast group is defined in bta.fetcher.Broadcast.properties

One can also run
     ./bin/enqueueJobs.sh Reklamefilm 0
Guess what that does

In any case, enqueueJobs.sh saved information in the database defined in
hibernate.cfg.xml, and marks each program as PENDING for transcoding.
The possible states are PENDING, COMPLETED, REJECTED and FAILED.

If a program already exists in the database, it is only marked as PENDING if the
timestamp of the change in DOMS is newer than the timestamp recorded in the
database.

Because of that, it should be safe to run enqueueJobs.sh repeatably, as the database will
not be updated if it already has the newest version.

## Transcoding jobs

The transcodings are then run with
    ./bin/transcodeChanges.sh Broadcast
This will get all PENDING jobs from the database, and start the transcoding.
The transcoding will run on all machines defined in setenv.sh, and use the defined
number of workers in total. Example:
4 workers and 2 machines will cause the transcoder to transcode 4 programs simultaneously, allocating
the work to 2 machines in a round robin fashion. So each machine will handle 2 transcodings.

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

