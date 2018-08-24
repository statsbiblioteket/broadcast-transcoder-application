How to make a stage test
========================

Relevant hosts
---------------

* devel: bta@iapetus
* stage: bta/develro@stage10
* prod: bta/develro@phoebe


Test general transcoding
------------------------

First, ensure that a sufficient number of programs have already been transcoded with the existing version. 

This can be done with
```bash
psql -d bta-stage -c 'update broadcasttranscodingrecord set transcodingstate=0 ;'
~/bta/bin/enqueueJobs.sh Broadcast 0
~/bta/bin/queryChangesDoms.sh Broadcast > ~/queue$(date +"%y%m%d").txt
~/bta/bin/transcode-master.sh start -h localhost -n 8 -j ~/queue.txt -v
```

Then archive all the files in `~/bta/data` as we will need these to compare with later on.


Go to `~/bta/data/streamingContent` and list the files

```bash
find -type f | rev | cut -d'/' -f1 | rev | cut -d'.' -f1 > transcoded_files

mv *.mp? previousTranscodings/
mv *.flv previousTranscodings/

cat ~/transcoded_files | cut -d'.' -f1 | xargs -r -i psql -d bta -c "update broadcasttranscodingrecord set transcodingstate=0 where id='uuid:{}' ;"
```

And restart the bta transcoder.

This should cause it to retranscode all the moved programs.

Then, pick some and compare manually.

One way to do this would be to upload pairs to the shared file storage `smb://halley/data/temp`

Please also upload pairs of `~bta/data/previews/` and `~bta/data/thumbnails/`


Test transcoding of specific (problematic) program
--------------------------------------------------

If we need to test transcoding of specific (problematic) programs from mediestream, do as follows

1. Get the doms id from the bug-reporter. This is the uuid from the URL.
From the url <http://www2.statsbiblioteket.dk/mediestream/tv/record/doms_radioTVCollection%3Auuid%3A8a0f2e2f-ab7f-4706-8184-001fe46a87c8/query/hest>, the uuid would be `8a0f2e2f-ab7f-4706-8184-001fe46a87c8`

2. Copy the existing transcoded files, thumbnails and previews to `smb://halley/data/temp` (`V:\temp` in windows)

3. Clear the transcoding queue `psql -d bta -c 'truncate table broadcasttranscodingrecord;'`

4. Use the script `portObjectsFromProdToDevel.sh uuid:8a0f2e2f-ab7f-4706-8184-001fe46a87c8`. This script uses `.netrc` for auth against the two DOMSses, and the vars `sourceDoms` and  `targetDoms` for the doms URLs. See the script for details.

5. Enqueue the new program for transcoding 
```bash
object="uuid:8a0f2e2f-ab7f-4706-8184-001fe46a87c8"
database="bta-devel"
databaseHost="iapetus"

#Auth through ~/.pgpass
psql --username=bta --dbname="$database" --host="$databaseHost" --echo-queries --command="INSERT INTO public.broadcasttranscodingrecord (id, domslatesttimestamp, failuremessage, lasttranscodedtimestamp, transcodingstate, broadcastendtime, broadcaststarttime, channel, endoffset, startoffset, title, transcodingcommand, tvmeter, video) VALUES ('$object', 1000, null, null, 0, null, null, null, 0, 0, null, null, true, true);"
```

6. Empty the log files by (re)moving the files `~bta/logs/bta-transcodeFile.error.log` and `~bta/logs/bta-transcodeFile.debug.log`

7. Run the transcoder system to get the enqueued program trancoded.

8. Ensure that no errors occurred, by checking that `~bta/logs/bta-transcodeFile.error.log` is still empty 

9. Copy the new transcoded files, previews and thumbnails to `smb://halley/data/temp`

10. Report back to the bugreporter and wait for him to compare the files.
