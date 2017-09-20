#!/bin/bash

PAC=fk-cs-languagetool-service

BASE_PATH="/usr/share/$PAC/service"
LOG_DIR="/var/log/flipkart/erp/$PAC"

cd $BASE_PATH
ulimit -n 50000
exec setuidgid fk-cs-languagetool-service /usr/lib/jvm/jdk-8-oracle-x64/jre/bin/java -Xms3g -Xmx5g -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -Xloggc:$LOG_DIR/gc.out -XX:+UseG1GC -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=6102 -Dcom.sun.management.jmxremote.authenticate=false -jar $BASE_PATH/fk-cs-languagetool-service*.jar server $BASE_PATH/configuration.yaml 2>&1

