#!/usr/bin/env bash

set -e
set -x
SCRIPT_DIR=$(dirname $(readlink -f $BASH_SOURCE[0]))

cd $SCRIPT_DIR

develHost="bta@iapetus"

mvn clean install -Psbprojects-nexus -DskipTests

archive=$(basename broadcast-transcoder-webservice/target/broadcast-transcoder-webservice-*-zip-package.tar.gz)

scp broadcast-transcoder-webservice/target/$archive $develHost:.

ssh $develHost "~/tomcat/bin/shutdown.sh"
sleep 5

ssh $develHost "tar -xvzf $archive --overwrite --directory \$HOME --strip 1"
ssh $develHost "mkdir -p \$HOME/lockdir/bta \$HOME/lockdir/bta-dorq"

ssh $develHost "~/tomcat/bin/shutdown.sh"
sleep 5
ssh $develHost "~/tomcat/bin/startup.sh"

ssh $develHost "tail -f ~/tomcat/logs/catalina.$(date +%Y-%m-%d).log"

set +e
