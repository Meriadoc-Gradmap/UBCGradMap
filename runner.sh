#!/bin/bash

# Start the gradle daemon
gradle run &

# Start the server
nginx

# Wait for any process to exit
wait -n

exit $?
