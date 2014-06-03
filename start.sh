#!/bin/bash
#
# The Nutch command script
#
# Environment Variableœs
#
#   NUTCH_JAVA_HOME The java implementation to use.  Overrides JAVA_HOME.
#
#   NUTCH_HEAPSIZE  The maximum amount of heap to use, in MB.
#                   Default is 1000.
#   JAVA_OPTS     jvm run options
#   NUTCH_OPTS      Extra Java runtime options.
#
# resolve links - $0 may be a softlink

if [ -e nohup.out ]; then
	echo "backup file nohup.out"
	mv nohup.out nohup.out.`date +%Y-%m-%d_%H-%M-%S`
fi

PROJECT_HOME=`pwd`

CLASSPATH=$PROJECT_HOME/classes
CLASSPATH=${CLASSPATH}:$PROJECT_HOME/lib
#CLASSPATH=${CLASSPATH}:$JAVA_HOME/lib/tools.jar

# add libs to CLASSPATH
for f in $PROJECT_HOME/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

# figure out which class to run
CLASS='com.handee.Context'

# run it
# JAVA_OPTS="-server -XX:PermSize=64m -XX:MaxPermSize=128m -verbose:gc -XX:+PrintGCDetails -Xms128m -Xmx256m -Xloggc:logs/gc.log"
JAVA_OPTS="-server -Djava.net.preferIPv4Stack=true -Xms512m -Xmx512m -XX:PermSize=64M -XX:MaxPermSize=128M -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Xnoclassgc"
JAVA_OPTS=${JAVA_OPTS}:" -verbose:gc -XX:+UseParNewGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCApplicationStoppedTime -Xloggc:logs/gc.log"

# log config
if [ "$LOG_DIR" = "" ]; then
  LOG_DIR="$PROJECT_HOME/logs"
fi
if [ "$LOG_FILE" = "" ]; then
  LOG_FILE='run.log'
fi

LOG_OPTS="-Dlog.dir=$LOG_DIR -Dlog.file=$LOG_FILE"

num=0
while [ $num -lt 1 ]
do
if [ -f "error" ];then
num=1
else
java $JAVA_OPTS $LOG_OPTS  -cp "$CLASSPATH" $CLASS "$@"
#"$JAVA" $JAVA_OPTS $LOG_OPTS  -cp "$CLASSPATH" $CLASS "$@"
#nohup "$JAVA" $JAVA_OPTS $LOG_OPTS  -cp "$CLASSPATH" $CLASS &
num=`expr $num + 1`
sleep 10
fi
done
