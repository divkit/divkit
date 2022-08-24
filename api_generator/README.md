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
pip install -r requirements.txt
pip install -r dev_requirements.txt
```

Run generator:
```shell
python -m api_generator -c /config/file/name.json -s /path/to/schema -o /output/path
```

Run tests:
```shell
python -m pytest
```

### With ya make

Build:
```shell
ya make
```

Run generator:
```shell
./api_generator -c /config/file/name.json -s /path/to/schema -o /output/path
```

Run tests:
```shell
ya make -t
```

