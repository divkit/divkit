cd ../../api_generator/
pip3 install -r requirements.txt
python3 -m api_generator -c ../json-builder/typescript/generator_config.json -s ../schema -o ../json-builder/typescript/src/generated
