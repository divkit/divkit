#!/bin/bash

INPUT_PATH=$1
TEMP_DIR="$(dirname $INPUT_PATH)/temp_extracted_to_replace"
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
   WORK_FOLDER=$INPUT_PATH
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
      # Here convert string like "/Users/username/Downloads/test/goldens/failures/testName_testImage.png"
      # To string like "test/goldens/failures/testName.png"
      # And then move it to this path

      # Remove "_test" substring to match golden file name
      GOLDEN_FILE_NAME=${FILE//$TEST_FILE_POSTFIX/}
      # Remove start path, but keep the "test" part, if we have it
      TO_KEEP="test/*"
      TO_REMOVE_PREFIX="$WORK_FOLDER/"
      TO_REMOVE_PREFIX=${TO_REMOVE_PREFIX//$TO_KEEP/}
      GOLDENS_DIR=${GOLDEN_FILE_NAME//$TO_REMOVE_PREFIX/}
      # Change postfix to "goldens"
      TO_REMOVE_POSTFIX="failures/*"
      TO_ADD_POSTFIX="goldens/"
      GOLDENS_DIR=${GOLDENS_DIR//$TO_REMOVE_POSTFIX/$TO_ADD_POSTFIX}

      echo "Moving $GOLDEN_FILE_NAME"
      cp "$FILE" "$GOLDEN_FILE_NAME"
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
