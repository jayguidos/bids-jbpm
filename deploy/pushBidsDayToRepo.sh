#!/bin/bash

version="1.0.0-SNAPSHOT"
repoTarget="snapshot"

REMOTE_REPO_ID=cgybpm01-deployment
REMOTE_REPO_URL=http://cgybpm01:8081/nexus/content/repositories

#
# the deploy-file goal does not seem to like a file path from the local repo, so I will make a copy
#
deployDir="`mktemp -d -t bids-day-deploy.XXXXX`"
cp $HOME/.m2/repository/com/bids/bom/BidsDay/$version/BidsDay-$version.jar $deployDir
cp $HOME/.m2/repository/com/bids/bom/BidsDay/$version/BidsDay-$version.pom $deployDir

#
# Now deploy
#
mvn     deploy:deploy-file                  \
        -DrepositoryId=$REMOTE_REPO_ID      \
        -Durl=$REMOTE_REPO_URL/$repoTarget  \
        -Dfile=./BidsDay-$version.jar       \
        -DpomFile=./BidsDay-$version.pom

rm -rf $deployDir
