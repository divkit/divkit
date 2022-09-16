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
