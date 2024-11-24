#!/bin/bash
./gradlew clean run &

# Start the server
nginx

# Wait for any process to exit
wait -n
exit $?
