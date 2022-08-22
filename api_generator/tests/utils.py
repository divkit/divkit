import json
import os
import shutil
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


def clear_content_of_directory(directory: str) -> None:
    for filename in os.listdir(directory):
        file_path = os.path.join(directory, filename)
        try:
            if os.path.isfile(file_path) or os.path.islink(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path):
                shutil.rmtree(file_path)
        except Exception as e:
            print(f'Failed to delete {file_path}. Reason: {e}')


def update_references(source_path: str, destination_path: str):
    for filename in os.listdir(source_path):
        src_file_path = os.path.join(source_path, filename)
        destination_file_path = os.path.join(destination_path, filename)
        shutil.copy(src_file_path, destination_file_path)
