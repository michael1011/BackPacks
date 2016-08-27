#!/bin/sh

rm spigot-1.10.2.jar

wget https://michael1011.at/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/spigot-1.10.2.jar

sh ./start.sh
