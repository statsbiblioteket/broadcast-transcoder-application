# Broadcast Transcoder

Broadcast Transcoder Application (BTA) is a suite of java applications, packaged with a scripting framework which
enables Radio & TV Broadcasts and Reklamefilm (represented as DOMS objects) to be mass-transcoded concurrently on a
single machine and (experimentally) distributed over multiple machines. The framework transcodes objects in
chronological order according to their last-modified timestamp in DOMS. The framework maintains its state in a
hibernate-based object-store database. Objects are transcoded in chronological order, according to their most recent
DOMS timestamps, and marked as complete in the database when completed. Hence the transcoding framework can always be
stopped and restarted safely; it begins again from the oldest object in the database which is still awaiting transcoding.


