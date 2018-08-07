#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $(readlink -f $BASH_SOURCE[0]))

set -e
set -x

rm "$SCRIPT_DIR/objects"

object="${1:-uuid:9b773c50-7e0e-4c42-ad18-e11ad9b47db2}"

echo "$object" > "$SCRIPT_DIR/objects"

#Auth through ~/.netrc
curl --silent --show-error --netrc "http://naiad:7880/fedora/objects/$object/objectXML" -o "$object.xml"

curl --silent --show-error --netrc "http://naiad:7880/fedora/objects/$object/relationships" | \
    sed -e "s/rdf://g" | \
    sed -e 's/xmlns="[^"]*"//g' | \
    xmllint --xpath "//hasFile/@resource" - | \
    sed -e 's|resource="info:fedora/|\n|g' | sed 's/"//g' | sed 's/ //g' | \
    tee -a "$SCRIPT_DIR/objects" | \
    xargs -r -i curl --silent --show-error --netrc "http://naiad:7880/fedora/objects/{}/objectXML" -o "{}.xml"

cat "$SCRIPT_DIR/objects" | grep uuid |  \
while read line
do
    curl --silent --show-error --netrc -H "Content-Type: text/xml" -ufedoraAdmin:fedoraAdminPass "http://alhena:7980/fedora/objects/new" -X POST --data-binary "@$line.xml" -vvv
done


#Auth through ~/.pgpass
psql --username=bta --dbname='bta-devel' --host=iapetus --echo-queries --command="INSERT INTO public.broadcasttranscodingrecord (id, domslatesttimestamp, failuremessage, lasttranscodedtimestamp, transcodingstate, broadcastendtime, broadcaststarttime, channel, endoffset, startoffset, title, transcodingcommand, tvmeter, video) VALUES ('$object', 1000, null, null, 0, null, null, null, 0, 0, null, null, true, true);"



#
#Info to port prod programs to devel
#http://naiad:7880/fedora/objects?pid=true&label=true&title=true&identifier=true&terms=&query=label~*teracom*&maxResults=20#
#
