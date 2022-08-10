cd ../../api_generator/
pip3 install -r requirements.txt
python3 -m api_generator -c ../client/ios/TemplatesSupportTests/generator_config.json -s ../test_data/test_schema -o ../client/ios/TemplatesSupportTests/generated_sources
