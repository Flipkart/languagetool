#!/bin/bash -e
set -x
function die()
{
	echo "Error: $1" >&2
	exit 1
}

[ -z "$LOCAL_DIR" ] && die "No base dir specified"
[ -z "$TARGET" ] && die "No package target specified"
[ -z "$INSTALL_BASE" ] && die "No install base specified"
[ -z "$PACKAGE" ] && die "No package name specified"
[ ! -d "$LOCAL_DIR" ] && die "$LOCAL_DIR does not exist"


# Selecting target ENV

case "$TARGET" in
        local) ENV=local;;
        nm) ENV=nm;;
        stagech) ENV=stagech;;
        preprod) ENV=preprod;;
esac

[ -z "$ENV" ] && die "Invalid target: $TARGET"


#current pointer...
OLD_DIR=`pwd`

#Deb directory
DEB_DIR="$LOCAL_DIR/deb"

#JAVA SETTINGS
OS=`lsb_release -c | awk '{print $2}'`;


#moving to repo directory
cd $LOCAL_DIR

#Copying everything to DEB Dir
cp -r $LOCAL_DIR/package/ $DEB_DIR

export JAVA_HOME=/usr/lib/jvm/j2sdk1.8-oracle

cd fk-cs-languagetool-service

#Compling and packaging
mvn clean compile package

MVN_STATUS=$?
[ $MVN_STATUS -eq 0 ] || exit $MVN_STATUS


cd "$DEB_DIR"

## inside deb directory now

BASE_DIR="/usr/share/$PACKAGE"


cp ../fk-cs-languagetool-service/target/fk-cs-languagetool-service-*.jar ."$BASE_DIR/service"
cp ../fk-cs-languagetool-service/src/main/resources/config/$ENV/configuration.yaml .$BASE_DIR/service
find *
cd $OLD_DIR