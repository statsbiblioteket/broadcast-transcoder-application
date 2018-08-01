0.6.0
-----
Mogens Vestergaard Kjeldsen huskede mig på at DORQ var en ting, så nu har jeg fikset bta-ws også

Der er kommet et review til Thomas H. Lange:  <https://sbprojects.statsbiblioteket.dk/fisheye/cru/CR-MP-24>

hvor jeg har ændret bta-ws til at gøre netop det.

Jeg har ændret EN ting for  `msdrs@iapetus`, for at se at det virkede.

`dk.statsbiblioteket.mediestream.dorq.btawsroot=http://iapetus.statsbiblioteket.dk:9641/bta-dorq.ws/bta`
  
Og når Thomas har godkendt det, skal det bare i stage.

Så er det op til nogen andre at verificere at DORQ ikke har andre issues, men jeg kan i hvert fald se at de mp4 filer den laver i devel har undertekster. 



0.5.0
-----
* config/run_transcoder.conf and config/transcode-master.conf is now based on $SCRIPT_PATH, as this gives nicer paths without ..
* transcode-master.sh status and stop now actually works;  pgrep needed flag -a instead of -l

* Final deploy_on_devel script

* Updated UnistreamTrancoder to correctly handle programs that start less than 5 seconds into their first file.
* Double skip feature (ss before file -5 sec and ss +5 sec after transcode, to be sure we get a useful keyframe to start from).

* Use the stereo audio stream instead of the 5.1 channel, as this gives better transcoded sound

* Correctly merges multi-file mpegs

* Only find danish subtitles and skip subtitles for hearing impared
* Overlayed subtitles

* java 8 

* Padding that works with newer ffmpegs
* Use ffmpeg40
* setenv.sh sources /opt/rh/sb-btatools1/enable and /opt/ffmpeg40/enable to get both a vlc AND a ffmpeg

* Default config do not store the images as the larm user, when running as the bta user...

* Asked the ExternalJobRunner to shut up about it's internal state. I like to be able to read my logs
* Better file move for final media file, now logs the actual error
* BTA job query: A bit more logging
* Log the url of the doms server, when connecting. Makes it a bit easier to quickly see which Doms a BTA is configured for
* Use active program pid as thread name. This makes it easier to read the log files, when multiple threads/processes write to the same file
* Reklamefilm trancoding: Improved error logging

* Readme: made more readable

* synchronised config files with those from iapetus devel server


0.4.0
-----

* Added DORQ webservice method and config.
    * Note that you must have a separate instance of BTA webservice running for DORQ, as it uses a different behaivour.ws.properties file
* Leaked domsReadOnlyAdmin password.

* Fixed extractSnapshot.sh script parameters.

* Disable SIGINT in queue.sh
* Fix race condition in transcode script
* Rewrite transcode control scripts