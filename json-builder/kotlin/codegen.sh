SCRIPT_DIR=`dirname ${0}`

cd $SCRIPT_DIR/../../api_generator/
python3 -m api_generator -c ../json-builder/kotlin/divan-json-builder/generator-config.json -s ../schema -o ../json-builder/kotlin/divan-json-builder/src/generated/kotlin/divkit/dsl
