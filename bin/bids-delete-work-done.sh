#!/bin/bash 

deploymentId="$1"
workId="$2"

. `dirname $0`/setup.rc

#
#  Main
#
runRestCmd andi '' deleteWorkDone $deploymentId $workId
