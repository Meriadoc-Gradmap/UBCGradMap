#!/bin/bash
gradle clean run &

# Start the server
cd frontend
npm run dev

# Wait for any process to exit
wait -n
 
exit $?