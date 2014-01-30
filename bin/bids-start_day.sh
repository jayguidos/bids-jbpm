#!/bin/bash 

processId="$1"
version="$2"
bidsDay="$3"

. `dirname $0`/setup.rc

#
#  Main
#
runRestCmd andi '' startDay $processId $version $bidsDay
