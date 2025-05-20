#!/bin/zsh

echo
echo
echo "-------------------------------------------------------------------------"
PROJECT_ROOT=$(dirname $(pwd))
SDK_MODULE_ROOT=$(pwd)

cd $PROJECT_ROOT/sdk-core-v1-no-update
sh build.sh -v 1 -t 0 -p 1
sh build.sh -v 1 -t 1 -p 1

cd $PROJECT_ROOT/sdk-core-v2-update
sh build.sh -v 2 -t 0 -p 1
sh build.sh -v 2 -t 1 -p 1
