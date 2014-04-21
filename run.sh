#!/bin/bash
set -eu
cd $(dirname $0)

if [ ! -f out/jemotionrec-distrib.jar ]
then
    ant
fi

java -jar out/jemotionrec-distrib.jar $@
