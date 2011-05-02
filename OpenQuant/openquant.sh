#!/bin/sh

DIRNAME=`dirname $0`

cd $DIRNAME

# set Java
if [ "x$JAVA" = "x" ]; then
    if [ -x "$TS_HOME/jre/bin/java" ]; then
        JAVA="$TS_HOME/jre/bin/java"
    elif [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

# Memory Options
JAVA_OPTS="-Xmx512m -Xrs"

java -cp "./lib/*:./scripts/:./config" $JAVA_OPTS org.openquant.backtest.command.OpenQuantCommand