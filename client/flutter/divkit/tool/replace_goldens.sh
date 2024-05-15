#!/bin/bash

INPUT_PATH=$1
GOLDENS_DIR="test/goldens/goldens"
FAILURES_DIR="test/goldens/failures"
TEMP_DIR="temp_extracted_to_replace"
TEST_FILE_POSTFIX="_testImage"
ZIP_FILE=false

if [ -z "$1" ]; then
   echo "Must pass path as argument"
   exit 1
fi

if [[ -d $INPUT_PATH ]]; then
   echo "Treating path as a directory: $INPUT_PATH"
elif [[ -f $INPUT_PATH ]]; then
   echo "Treating path as an archive file: $INPUT_PATH"
   ZIP_FILE=true
else
   echo "Error: $INPUT_PATH is neither archive file or directory"
   exit 1
fi

echo

if [[ $ZIP_FILE = true ]]; then
   echo "Exctracting..."
   echo
   WORK_FOLDER=$TEMP_DIR
   unzip -o "$INPUT_PATH" -d $WORK_FOLDER
   echo
else
   WORK_FOLDER=$FAILURES_DIR
fi

function testImageFiles() {
   # shellcheck disable=SC2046
   # shellcheck disable=SC2006
   # shellcheck disable=SC2005
   echo `find $WORK_FOLDER -name "*$TEST_FILE_POSTFIX*" -print`
}

FILES=$(testImageFiles)
FILES_ARRAY=("$FILES")

echo "Found ${#FILES_ARRAY[@]} test files"
echo

for FILE in $FILES; do
      # Remove substring to match golden file name
      GOLDEN_FILE_NAME=${FILE//$TEST_FILE_POSTFIX/}
      echo "Moving $GOLDEN_FILE_NAME"
      mv "$FILE" "$GOLDEN_FILE_NAME"
      mkdir -p $GOLDENS_DIR
      mv "$GOLDEN_FILE_NAME" $GOLDENS_DIR
   done

if [[ $ZIP_FILE = true ]]; then
   echo
   echo "Removing temp dir"
   rm -rf $TEMP_DIR
fi

echo
echo "Done!"
