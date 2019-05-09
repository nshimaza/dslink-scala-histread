#!/bin/sh
rm -fr target
sbt assembly
rm -fr dslink-java-histread
mkdir -p dslink-java-histread/bin
mkdir -p dslink-java-histread/lib
cp dslink.json dslink-java-histread/
cp dslink-scala-histread dslink-java-histread/bin/
cp target/scala-2.12/dslink-scala-histread-assembly-0.1.0-SNAPSHOT.jar dslink-java-histread/lib/
rm dslink-java-histread.zip
zip -r dslink-java-histread.zip dslink-java-histread
