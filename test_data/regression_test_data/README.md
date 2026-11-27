# DivKit Test Data

JSON data for framework testing inside `DivKit Playground` apps. Theese test cases can be found in `DivKit Playground` -> `Testing` screen.

## How to add new test case

Cover the behavior with an automated test first and add a test case here only for what it cannot check.

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

`case_id` – test case id in the test management system.

`automated` – list of platforms where automated tests check everything the test case expects. Android `DivKit Playground` does not show test cases automated for `android`.

`unsupported_platforms` – platforms not listed in `platforms`, each with a reason: the test case does not apply there, e.g. `{"web": "divkit-asset support is not expected"}`, or another test replaced it, e.g. `{"android": "test is replaced by interactive_snapshot_test_data/div-container/patch_container.json"}`.

`automation_blocker` – platforms where the test case stays manual, each with one sentence on what an automated test cannot check, e.g. `{"ios": "The runner cannot check animation smoothness."}`. A platform missing from both `automated` and this field is not automated yet.

## Automated UI scenarios

Executable scenarios are stored in [`interactive_snapshot_test_data`](../interactive_snapshot_test_data). The iOS runner reads a self-contained card from `div_data` and displays it in `DivKitUITestsHostApp`.
