#!/bin/bash
mkdir update
mkdir update/server

cd server
cp -r . ../update
cd ..

tar -czvf update.tar.gz update
rm -fr update