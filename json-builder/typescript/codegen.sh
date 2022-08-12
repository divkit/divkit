if [[ "$OSTYPE" == "darwin"* ]]; then
    ../../../internal/homeapigenerator-binaries/macosx/HomeAPIGenerator generator_config.json ../../schema src/generated
else
    ../../../internal/homeapigenerator-binaries/linux/HomeAPIGenerator generator_config.json ../../schema src/generated
fi
