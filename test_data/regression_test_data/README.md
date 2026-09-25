# DivKit Test Data

JSON data for framework testing inside `DivKit Playground` apps. Theese test cases can be found in `DivKit Playground` -> `Testing` screen.

## How to add new test case

1. Add JSON data file into this folder.
2. Add test case information into `index.json` file.
3. Rebuild `DivKit Playground` app and check new test case.

## index.json format

`title` – test case title.

`steps` – actions required to complete the test case.

`expected_results` – what is expected to happen after performing actions from `steps` section.

`tags` – tags that are used to filter scenarios.

`priority` – test case priority. Possible values: `blocker`, `critical`, `normal`, `minor`. Default value is `normal`.

`file` – relative path to JSON data.

`platforms` – list of platforms where the test case is available. Possible values: `android`, `ios`, `web`.

## Automated UI scenarios

Executable scenarios are stored in the `automated` subdirectory. The iOS runner reads a self-contained card from `div_data` and displays it in `DivKitUITestsHostApp`.

The common scenario fields are:

- `description` – a human-readable scenario description.
- `div_data` – full card JSON (`templates` and `card`), required by the iOS runner.
- `case_id` – a positive identifier for runners that load cards from `index.json`; not used by iOS.
- `platforms` – platforms that execute the scenario. The iOS runner ignores scenarios without `ios`.
- `steps` – ordered UI operations.
