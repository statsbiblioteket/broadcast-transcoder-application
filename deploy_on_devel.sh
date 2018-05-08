#!/usr/bin/env bash

set -e
set -x
SCRIPT_DIR=$(dirname $(readlink -f $BASH_SOURCE[0]))

cd $SCRIPT_DIR

develHost="bta@iapetus"

mvn clean install -Psbprojects-nexus -DskipTests

archive=$(basename broadcast-transcoder-commandline/target/broadcast-transcoder-commandline-*-zip-package.tar.gz)

scp broadcast-transcoder-commandline/target/$archive $develHost:.

#TODO perhaps delete existing install first?
ssh $develHost "tar -xvzf $archive --directory /home/bta/bta --strip 1"

#Initialisation
ssh $develHost "psql -d bta-devel -c 'delete from broadcasttranscodingrecord;'"

ssh $develHost 'rm streamingContent/*.mp3'
ssh $develHost 'rm streamingContent/*.flv'

ssh $develHost 'rm logs/*'
