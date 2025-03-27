#!/bin/bash

SCRIPT_DIR=`dirname ${0}`
ARCHIVE_NAME="build"

while getopts ":n:" option; do
  case $option in
    n)
      ARCHIVE_NAME="$OPTARG"
    ;;

    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
    ;;

    :)
      echo "Option -$OPTARG requires an argument" >&2
      exit 1
    ;;
  esac

  if [[ $OPTARG == -* ]]; then
        echo "Option $option has invalid argument: $OPTARG" >&2
        exit 1
  fi
done

cd $SCRIPT_DIR
zip -rDX $ARCHIVE_NAME.zip * -x build.sh -x *.zip -x build -x .idea
