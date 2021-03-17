#!/usr/bin/env bash

SRC="src"
LIB="./lib/javafx-sdk-16/lib"

if [[ `find . -type d -name "build" | wc -l` -eq 0 ]] ; then
	mkdir build
fi

javac -classpath ${SRC} --module-path ${LIB} --add-modules javafx.controls,javafx.fxml  -d build ${SRC}/company/StoreManager.java
