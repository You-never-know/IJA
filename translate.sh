#!/usr/bin/env bash

SRC="src"

if [[ `find . -type d -name "build" | wc -l` -eq 0 ]] ; then
	mkdir build
fi

javac -classpath ${SRC} -d build ${SRC}/company/StoreManager.java
