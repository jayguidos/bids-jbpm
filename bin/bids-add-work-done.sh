#!/bin/bash 

deploymentId="$1"
workDoneName="$2"

. `dirname $0`/setup.rc

#
#  Main
#
runRestCmd andi '' addWorkDone $deploymentId $workDoneName
