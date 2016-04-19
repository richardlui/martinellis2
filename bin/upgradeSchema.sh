#!/bin/bash

CP=$( echo `dirname $0`/../target/martinellis-rest-1.0.0-SNAPSHOT/WEB-INF/lib/*.jar . | sed 's/ /:/g')
CP=$CP:../target/martinellis-rest-1.0.0-SNAPSHOT/WEB-INF/classes

# Find Java
if [ "$JAVA_HOME" = "" ] ; then
    JAVA="java -server"
else
    JAVA="$JAVA_HOME/bin/java -server"
fi

# Set Java options
if [ "$JAVA_OPTIONS" = "" ] ; then
    JAVA_OPTIONS="-Xms32m -Xmx512m"
fi

# Launch the application
$JAVA $JAVA_OPTIONS -cp $CP com.martinellis.rest.sample.UpgradeSchema $@ $EXTRA

# Return the program's exit code
exit $?
