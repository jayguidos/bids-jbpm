#!/bin/bash 

processId="$1"
deploymentId="$2"
#
#  Main
#
cd `dirname $0`/../bids-rest-client
mvn exec:java -Dexec.args="startProcess $processId $deploymentId" | grep -v "\[INFO\]"
