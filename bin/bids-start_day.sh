#!/bin/bash 

processId="$1"
version="$2"
bidsDay="$3"
#
#  Main
#
cd `dirname $0`/../bids-rest-client
mvn exec:java -Dexec.args="startDay $processId $version $bidsDay" | grep -v "\[INFO\]"
