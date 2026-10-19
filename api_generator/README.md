# DivKit API Generator

The script takes a JSON schema at the input and generates the code of objects described in the schema based on it and the config.

## Config format:

```json
{
	"lang": "[kotlin|kotlinDsl|swift|typescript|python|documentation]",
	"header": "// Header of every generated file. Generated code. Do not modify.\nimport some.lib\nimport other.lib\n\n"
}
```

## Commands

### Kotlin parser-only generation

The Kotlin generator supports opt-in standalone parsers for existing models:

```json
{
  "lang": "kotlin",
  "generateModels": false,
  "generateSerializers": true,
  "multiplatform": true,
  "errorsCollectors": ["div-data"]
}
```

- `generateModels` defaults to `true`. When disabled, only `*JsonParser` files
  and `JsonParserComponent` are emitted; models, templates, and enums are reused.
- `multiplatform` defaults to `false`. It generates parsers using
  `com.yandex.div.core.json.JsonObject` instead of `org.json.JSONObject` and
  serializes through `JsonObject.builder()`. It requires Kotlin serializers and
  `generateModels: false`.
- Existing model fields keep their original JSON types. Parser helpers supply
  `JSON_TO_ORG_OBJECT` and `JSON_TO_ORG_ARRAY` conversions at those boundaries.
- The configuration's `header` must select a separate parser package and import
  the existing models, serialization contracts, and matching parser helpers.

See `client/android/parser-json/parser-generator-config.json` for the complete
Android configuration. Other generator configurations retain their existing behavior.

### Build

Setup environment:
```shell
pip install -r dev_requirements.txt
```

Run generator:
```shell
python3 -m api_generator -c /config/file/name.json -s /path/to/schema -o /output/path [--checkhash] [--savehash]
```

```shell
./api_generator.sh /config/file/name.json /path/to/schema /output/path [--checkhash] [--savehash]
```
- `--checkhash` Check the hash files in the output directory before generating
- `--savehash` Save the hash files of the generator, config and schema to the output directory

Run tests:
```shell
python3 -m pytest
```

### With ya make

Build:
```shell
ya make
```

Run generator:
```shell
./api_generator_script -c /config/file/name.json -s /path/to/schema -o /output/path [--checkhash] [--savehash]
```

- `--checkhash` Check the hash files in the output directory before generating
- `--savehash` Save the hash files of the generator, config and schema to the output directory

Run tests:
```shell
ya make -t
```
