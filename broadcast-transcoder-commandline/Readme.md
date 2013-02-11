# Broadcast Transcoder



Broadcast transcoder is (surprise) a system, that transcoded radio-tv
programs from DOMS, based on the raw recordings stored in the Bitmagasin, into
files suitable to presentation in Mediestream.

How to use this system


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

For the first run, note that the field overwrite in bta.behaviour.properties, should be set to
 false. This will cause the system to mark all programs as completed, if the file already exists.

After this first run all the programs in doms should be marked as completed in the database. We can
now set the overwrite flag to true, and let the system run continuously.


