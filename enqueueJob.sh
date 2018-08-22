#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $(readlink -f $BASH_SOURCE[0]))

object="${1:-uuid:9b773c50-7e0e-4c42-ad18-e11ad9b47db2}"


#Auth through ~/.pgpass
psql --username=bta --dbname='bta-devel' --host=iapetus --echo-queries --command="INSERT INTO public.broadcasttranscodingrecord (id, domslatesttimestamp, failuremessage, lasttranscodedtimestamp, transcodingstate, broadcastendtime, broadcaststarttime, channel, endoffset, startoffset, title, transcodingcommand, tvmeter, video) VALUES ('$object', 1000, null, null, 0, null, null, null, 0, 0, null, null, true, true);"

