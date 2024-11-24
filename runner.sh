#!/usr/bin/bash
./gradlew clean run &
nginx
wait -n
exit $?
