#!/bin/bash

collection=$1
progressFile=$2

SCRIPT_PATH=$(dirname $(readlink -f $0))

source ${SCRIPT_PATH}/setenv.sh ${collection}


workList=$(mktemp)
${SCRIPT_PATH}/queryChanges.sh $collection $progressFile > $workList

id=$(cat $progressFile)

mv $workList $SCRIPT_PATH/$id

parallel --sshlogin $machines --jobs 12 --colsep ' '  --retries 3 --resume --joblog $SCRIPT_PATH/$id.log --eta --progress $SCRIPT_PATH/transcodeFile.sh $collection {} :::: $SCRIPT_PATH/$id
returncode=$?

echo "$returncode files failed transcoding"

exit $returncode
