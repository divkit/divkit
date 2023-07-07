import json
import os
import shutil
from typing import Dict, Any, List
from deepdiff import DeepDiff

try:
    import yatest.common as yc

    PROJECT_TESTS_PATH = os.path.join('divkit', 'public', 'api_generator', 'tests')
    DIVKIT_TESTS_PATH = os.path.join('divkit', 'public', 'test_data')

    def path_generator_tests(relative_path: str) -> str:
        return yc.build_path(os.path.join(PROJECT_TESTS_PATH, relative_path))

    def path_divkit_test_data(relative_path: str) -> str:
        return yc.build_path(os.path.join(DIVKIT_TESTS_PATH, relative_path))

except ModuleNotFoundError:
    PROJECT_TESTS_PATH = os.path.join('tests')
    DIVKIT_TESTS_PATH = os.path.join('..', 'test_data')

    def path_generator_tests(relative_path: str) -> str:
        return os.path.join(PROJECT_TESTS_PATH, relative_path)

    def path_divkit_test_data(relative_path: str) -> str:
        return os.path.join(DIVKIT_TESTS_PATH, relative_path)


def assert_as_json_test(file_expected: str, content_actual: Dict) -> None:
    with open(file_expected, 'r') as expected:
        content_expected = json.loads(expected.read())
        diff = DeepDiff(content_expected, content_actual, ignore_order=False)
        assert len(diff) == 0


def update_reference(filename: str, content: Any):
    with open(filename, 'w') as updated_reference:
        updated_reference.write(str(content))


def update_json_reference(filename: str, json_content: Dict[str, Any]):
    update_reference(filename, json.dumps(json_content, indent=2, ensure_ascii=False))


def clear_content_of_directory(directory: str) -> None:
    for file in os.listdir(directory):
        file_path = os.path.join(directory, file)
        try:
            if os.path.isfile(file_path) or os.path.islink(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path):
                shutil.rmtree(file_path)
        except Exception as e:
            print(f'Failed to delete {file_path}. Reason: {e}')


def update_references(source_path: str, destination_path: str):
    for file in os.listdir(source_path):
        if os.path.isfile(os.path.join(source_path, file)):
            src_file_path, dst_file_path = __join_paths(source_path, destination_path, file)
            shutil.copy(src_file_path, dst_file_path)
        else:
            src_file_path, dst_file_path = __join_paths(source_path, destination_path, file)
            if not os.path.exists(dst_file_path):
                os.makedirs(dst_file_path)
            update_references(src_file_path, dst_file_path)


def compare_dirs(references_path: str, generated_path: str, ignore: List[str] = None):
    if ignore is None:
        ignore = []
    for file in os.listdir(references_path):
        if file in ignore:
            continue
        if os.path.isfile(os.path.join(references_path, file)):
            ref_file_path, gen_file_path = __join_paths(references_path, generated_path, file)
            compare_files(file, ref_file_path, gen_file_path)
        else:
            ref_dir_path, gen_dir_path = __join_paths(references_path, generated_path, file)
            compare_dirs(ref_dir_path, gen_dir_path)


def __join_paths(references_path: str, generated_path: str, new_path: str) -> (str, str):
    ref_path = os.path.join(references_path, new_path)
    gen_path = os.path.join(generated_path, new_path)
    return ref_path, gen_path


def compare_files(filename: str, ref_file_path: str, generated_file_path: str):
    with open(ref_file_path, 'r') as ref_file:
        with open(generated_file_path, 'r') as gen_file:
            ref_lines, gen_lines = ref_file.readlines(), gen_file.readlines()
            lines_count = min(len(ref_lines), len(gen_lines))
            for line_ind in range(lines_count):
                ref_line, gen_line = ref_lines[line_ind], gen_lines[line_ind]
                are_equal = ref_line == gen_line
                if not are_equal:
                    print(f'Lines are not equal in file {filename}. Line number: {line_ind + 1}')
                    print(f'Expected: [ {ref_line.strip()} ]')
                    print(f'Actual:   [ {gen_line.strip()} ]')
                assert are_equal
            lines_count_equal = len(ref_lines) == len(gen_lines)
            if not lines_count_equal:
                print(f'Different lines count in reference({len(ref_lines)} lines) ' +
                      f'and generated({len(gen_lines)} lines) file {filename}.')
            assert lines_count_equal
