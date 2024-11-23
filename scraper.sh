#!/bin/bash

pkill java

./gradlew clean build
./gradlew runScraperNoCache

./gradlew run

wait -n
 
exit $?