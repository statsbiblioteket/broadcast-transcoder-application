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
ssh $develHost "mkdir -p /home/bta/bta && tar -xvzf $archive --directory /home/bta/bta --strip 1"

$SCRIPT_DIR/restart_devel.sh
