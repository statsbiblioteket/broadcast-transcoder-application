#!/usr/bin/env bash

SCRIPT_DIR=$(dirname $(readlink -f $BASH_SOURCE[0]))

set -e
set -x

dir=$(mktemp -d)

object="${1:-uuid:9b773c50-7e0e-4c42-ad18-e11ad9b47db2}"

sourceDoms="${sourceDoms:-http://naiad:7880/fedora/objects}"
targetDoms="${targetDoms:-http://alhena:7980/fedora/objects}"

echo "$object" > "$dir/objects"

#Auth through ~/.netrc
curl --silent --show-error --netrc "$sourceDoms/$object/objectXML" -o "$dir/$object.xml"

curl --silent --show-error --netrc "$sourceDoms/$object/relationships" | \
    sed -e "s/rdf://g" | \
    sed -e 's/xmlns="[^"]*"//g' | \
    xmllint --xpath "//hasFile/@resource" - | \
    sed -e 's|resource="info:fedora/|\n|g' | sed 's/"//g' | sed 's/ //g' | \
    tee -a "$dir/objects" | \
    xargs -r -i curl --silent --show-error --netrc "$sourceDoms/{}/objectXML" -o "$dir/{}.xml"

cat "$dir/objects" | grep uuid | while read line; do
    curl --silent --show-error --netrc -H "Content-Type: text/xml" "$targetDoms/objects/new" -X POST --data-binary "@$dir/$line.xml" -vvv
done

#Enqueue the new object in the bta queue database
#"${SCRIPT_DIR}/enqueueJob.sh" "$object"



#
#Info to port prod programs to devel
#http://naiad:7880/fedora/objects?pid=true&label=true&title=true&identifier=true&terms=&query=label~*teracom*&maxResults=20#
#
