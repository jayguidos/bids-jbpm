#!/bin/bash 

#
#  Main
#
cd `dirname $0`/../bids-rest-client
mvn exec:java -Dexec.args="deployments" | grep -v "\[INFO\]"
