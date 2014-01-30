#!/bin/bash 

processId="$1"
deploymentId="$2"

. `dirname $0`/setup.rc

#
#  Main
#
runRestCmd andi '' startProcess $processId $deploymentId
