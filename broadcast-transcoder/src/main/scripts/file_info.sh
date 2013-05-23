#!/bin/bash

#
# Given a program uuid as input, prints out the uuids of all corresponding file objects.
#
SCRIPT_PATH=$(dirname $(readlink -f $0))
source ${SCRIPT_PATH}/settings.sh
url=http://naiad:7880/fedora/objects/${1}/datastreams/RELS-EXT/content
wget -q --user=fedoraReadOnlyAdmin --password=${DOMS_PWD} -O   - ${url}|sed -rn 's!.*hasFile.*info:fedora/(.*)\".*!\1!p'

