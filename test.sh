#!/bin/bash

echo "Stoping container"
docker stop -t 0 $(docker ps -aq)
docker rm -f test

mvn clean install -DskipTests && docker build . -t test:1.0 && docker run --cpus=1 -p 8080:8080 --restart=unless-stopped -d --name=test test:1.0

echo "Wait 10 sec to start and settle"



NUMBERS=10000000
sleep 10
echo "Lets test bad ep :)"
seq 1 40 | xargs -n1 -P10 sh -c "curl localhost:8080/crash?numbers=$NUMBERS"
sleep 5
echo "Result"
docker inspect test | grep RestartCount

sleep 10
echo "Lets test atLeastYouTried ep :)"
seq 1 40 | xargs -n1 -P10 sh -c "curl localhost:8080/atLeastYouTried?numbers=$NUMBERS"
sleep 5
echo "Result"
docker inspect test | grep RestartCount

sleep 10
echo "Lets test better ep :)"
seq 1 40 | xargs -n1 -P10 sh -c "curl localhost:8080/better?numbers=$NUMBERS"
sleep 5
echo "Result"
docker inspect test | grep RestartCount


sleep 10
echo "Lets test safe ep :)"
seq 1 40 | xargs -n1 -P10 sh -c "curl localhost:8080/safe?numbers=$NUMBERS"
sleep 5
echo "Result"
docker inspect test | grep RestartCount
