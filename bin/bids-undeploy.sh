#!/bin/bash 

bidsDeploymentId="$1"
#
#  Main
#
cd `dirname $0`/../bids-rest-client
mvn exec:java -Dexec.args="undeploy $bidsDeploymentId" | grep -v "\[INFO\]"
