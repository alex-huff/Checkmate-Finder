#!/bin/sh
cd `dirname $0`
./gradlew shadowJar && cp build/libs/checkmate-finder.jar .
