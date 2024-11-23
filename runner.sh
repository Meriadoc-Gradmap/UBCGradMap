#!/bin/bash

# Start the gradle daemon
gradle run &

# Start the server
cd frontend
npm run dev

# Wait for any process to exit
wait -n
 
exit $?