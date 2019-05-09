#!/bin/sh
sbt clean assembly
rm -fr dist
mkdir -p dist/bin
mkdir -p dist/lib
cp dslink.json dist/
cp dslink-scala-histread dist/bin/
cp target/scala-2.12/dslink-scala-histread-assembly-0.1.0-SNAPSHOT.jar dist/lib/
