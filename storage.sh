#!/bin/bash

if [[ "$1" == "--help"  || $# -lt 2 ]]; then
    echo -e "Usage:"
    echo -e "$0 [OPTIONS...]"
    echo
    echo "Operations can be:"
    echo -e "\t--base=<DIR>\t Saving directory of Storage Server."
    echo -e "\t--port=<PORT>\t Port of Storage Server."
    echo 
    echo -e "Examples:"
    echo -e "\t$0 --base=./storage --port=55555"
    echo
    exit 1
fi

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

cp=$(JARS=($SCRIPT_DIR/jar/DistributedFileSystem.jar); IFS=:; echo "${JARS[*]}")
libs="$SCRIPT_DIR/lib/native/linux-64"

mainClass="storageserver.StorageServerLauncher"

java -Djava.library.path=$libs -cp $cp $mainClass $@
