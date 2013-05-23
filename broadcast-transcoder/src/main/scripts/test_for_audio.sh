#!/bin/bash

#
# Checks all .flv files under a directory and prints out names of those files missing audio.
#

check_for_audio() {
  ffprobe "${1}" 2>&1 |grep -q 'Stream.*Audio'
  return $?
}

export -f check_for_audio

find -L ${1} -name '*.flv' -exec bash -c 'if check_for_audio "{}" ; then true ; else echo $(basename "{}") ; fi' \;
