import json
from typing import Dict, Any
from deepdiff import DeepDiff

try:
    import yatest.common as yc

    PROJECT_TESTS_PATH = 'divkit/public/api_generator/tests/'
    DIVKIT_TESTS_PATH = 'divkit/public/test_data/'

    def path_generator_tests(relative_path: str) -> str:
        return yc.build_path(f'{PROJECT_TESTS_PATH}{relative_path}')

    def path_divkit_test_data(relative_path: str) -> str:
        return yc.build_path(f'{DIVKIT_TESTS_PATH}{relative_path}')

except ModuleNotFoundError:
    PROJECT_TESTS_PATH = 'tests/'
    DIVKIT_TESTS_PATH = '../test_data/'

    def path_generator_tests(relative_path: str) -> str:
        return f'{PROJECT_TESTS_PATH}{relative_path}'

    def path_divkit_test_data(relative_path: str) -> str:
        return f'{DIVKIT_TESTS_PATH}{relative_path}'


def assert_as_json_test(file_expected: str, content_actual: Dict) -> None:
    with open(file_expected, 'r') as expected:
        content_expected = json.loads(expected.read())
        diff = DeepDiff(content_expected, content_actual, ignore_order=True)
        assert len(diff) == 0


def update_reference(filename: str, content: Any):
    with open(filename, 'w') as updated_reference:
        updated_reference.write(str(content))


def update_json_reference(filename: str, json_content: Dict[str, Any]):
    update_reference(filename, json.dumps(json_content, indent=2, ensure_ascii=False))
