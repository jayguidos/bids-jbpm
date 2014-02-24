#!/bin/bash

version="1.0.0-SNAPSHOT"

REMOTE_REPO_ID=cgybpm01-deployment
REMOTE_REPO_URL=http://cgybpm01:8081/nexus/content/groups/bids

mvn org.apache.maven.plugins:maven-dependency-plugin:2.8:get            \
    -Dartifact=com.bids.bpm:BidsDay:$version                            \
    -DremoteRepositories=$REMOTE_REPO_ID::default::$REMOTE_REPO_URL
