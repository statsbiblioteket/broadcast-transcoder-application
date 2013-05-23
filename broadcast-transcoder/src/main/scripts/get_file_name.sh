#!/bin/bash

#
# takes a file uuid as parameter and finds the corresponding file name
#
SCRIPT_PATH=$(dirname $(readlink -f $0))
source ${SCRIPT_PATH}/settings.sh
url=http://naiad:7880/fedora/objects/${1}/?format=xml
wget -q --user=fedoraReadOnlyAdmin --password=${DOMS_PWD} -O   - ${url} |sed -rn 's!.*<objLabel>(.*)</objLabel>.*!\1\n!p'
