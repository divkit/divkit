cd ../../api_generator/
python3 -m api_generator -c ../client/ios/DivKit/generator_config.json -s ../schema -o ../client/ios/DivKit/generated_sources
python3 -m api_generator -c ../client/ios/DivKit/shared_data_generator_config.json -s ../shared_data -o ../client/ios/DivKit/shared_data_generated_sources
