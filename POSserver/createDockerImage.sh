#!/bin/bash

cd ../server
docker rmi KCpos:latest
docker build -t KCpos .
docker save KCpos | gzip > ../deploy.tar.gz


