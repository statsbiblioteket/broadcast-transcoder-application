Broadcast Transcoder
======================

Broadcast transcoder is a system, that transcodes radio-tv
programs from DOMS, based on the raw recordings stored in the Bitmagasin, into
files suitable to presentation in Mediestream.


Enqueuing jobs
--------------

Firstly, run
    
    ./bin/enqueueJobs.sh Broadcast 0

This will query DOMS for all programs changed since timestamp 0, in the
Broadcast group. The Broadcast group is defined in `bta.fetcher.Broadcast.properties`

One can also run
     
     ./bin/enqueueJobs.sh Reklamefilm 0

This will do the same but for the Reklame film group, defined in `bta.fetcher.Reklamefilm.properties`

In any case, `enqueueJobs.sh` saves information in the database defined in
`hibernate.cfg.xml`, and marks each program as **PENDING** for transcoding.
The possible states are **PENDING**, **COMPLETED**, **REJECTED** and **FAILED**.

If a program already exists in the database, it is only marked as **PENDING** if the
timestamp of the change in DOMS is newer than the timestamp recorded in the
database.

Because of that, it should be safe to run `enqueueJobs.sh` repeatably, as the database will not be updated if it already has the newest version.

Transcoding jobs
----------------

Before starting a transcoding a queue file needs to be prepared.

To get all **PENDING** jobs from the database for a specific collection
    
    ./bin/queryChangesDoms.sh Broadcast

This will output one line for each **PENDING** job suitable as input for the
transcoder.
If there are no running transcoders using the intended queue file then simple
redirection can be used
    
    ./bin/queryChangesDoms.sh Broadcast > myqueue.txt

If a transcoder is already actively using the queue file then it must be
updated using the queue.sh command to avoid corrupting the file
 
```bash
    OIFS=$IFS;
    IFS=$'\n';
    for job in $(./bin/queryChangesDoms.sh Broadcast); do
        ./bin/queue.sh myqueue.txt push "$job";
    done;
    IFS=$OIFS
```

The transcoding is started with
 
    ./bin/transcode-master.sh -h localhost -n 2 -j myqueue.txt

If run with no or wrong parameters it will output usage instructions.

transcode-master.sh will start up the given number (`-n`) of transcoding jobs
(usually `run_transcoder.sh`) which will then run in the background.
As many instances of `run_transcoder.sh` can be started as needed, even on the
same host.
Both Broadcast and Reklamefilm jobs can be added to the same queue.

Running jobs are managed with `transcode-master.sh` but note that it currently
only distinguishes jobs based on the host assigned so jobs with different queues
are treated the same.

Handling old transcodings
-------------------------

If old transcodings already exists, the best way to handle them is to mark them as already transcoded.
As they are marked as transcoded, the system will not attempt to retranscode them until some meaningful change happens in doms.
Generate a list from Fedora (<http://fedoraHost:7880/fedora/risearch>)with the following ITQL query

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

And store the contents (minus the header) in a file (`listOfPrograms.txt`). Then invoke the
    
    ./markAsCompleted.sh `listOfprograms.txt`
    
It will then create entries in the database for each program in that list.

After this `enqueueJobs.sh` can be run freely, as it will not update a record
if the doms timstamp is not newer than the database timestamp.

