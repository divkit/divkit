SCRIPT_DIR=`dirname ${0}`

cd $SCRIPT_DIR/../../api_generator/
python3 -m api_generator -c ../json-builder/kotlin/generator-divan-config.json -s ../schema -o ../json-builder/kotlin/src/generated/kotlin/divkit/dsl
