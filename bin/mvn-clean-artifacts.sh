#!/bin/bash

cd `basename $0`
mvn dependency:purge-local-repository
