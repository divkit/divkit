cd ../../api_generator/
pip3 install -r requirements.txt
python3 -m api_generator -c ../client/ios/DivKit/generator_config.json -s ../schema -o ../client/ios/DivKit/generated_sources
