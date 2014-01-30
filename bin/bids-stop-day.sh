#!/bin/bash 

bidsDeploymentId="$1"

. `dirname $0`/setup.rc

#
#  Main
#
runRestCmd andi '' stopDay $bidsDeploymentId
