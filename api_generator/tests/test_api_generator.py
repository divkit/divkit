import utils
import os

try:
    # full package name for ya make
    from divkit.public.api_generator.api_generator.config import Config
    from divkit.public.api_generator.api_generator.schema.preprocessing.preprocessor import resolve_structure
    from divkit.public.api_generator.api_generator.generator import generate_api

except ModuleNotFoundError:
    from api_generator.config import Config
    from api_generator.schema.preprocessing.preprocessor import resolve_structure
    from api_generator.generator import generate_api

# Set SHOULD_UPDATE_REFERENCES = True and run pytest to update reference files.
SHOULD_UPDATE_REFERENCES = False

TEST_SCHEMA_PATH = utils.path_divkit_test_data('test_schema')
REAL_SCHEMA_PATH = utils.path_divkit_test_data('../schema')
OUTPUT_PATH = utils.path_generator_tests('output')


def test_schema_preprocessor():
    def assert_json_preprocessor_test(filename: str) -> None:
        path = utils.path_generator_tests(filename)
        if SHOULD_UPDATE_REFERENCES:
            utils.update_json_reference(filename=path, json_content=root_directory.as_json)
            assert False, 'Updated references. Don\'t forget to restore SHOULD_UPDATE_REFERENCES flag.'
        utils.assert_as_json_test(file_expected=path, content_actual=root_directory.as_json)

    test_config = Config(config_path=utils.path_generator_tests('configs/swift_config.json'),
                         schema_path=TEST_SCHEMA_PATH,
                         output_path=OUTPUT_PATH)

    root_directory = resolve_structure(test_config)
    assert_json_preprocessor_test(filename='references/reference_resolve_structure.json')

    root_directory.merge_all_ofs(test_config.generation.lang)
    assert_json_preprocessor_test(filename='references/reference_merge_allOfs.json')

    root_directory.resolve_references(test_config.generation.lang)
    assert_json_preprocessor_test(filename='references/reference_resolve_references.json')

    root_directory.clean_unused_definitions()
    assert_json_preprocessor_test(filename='references/reference_clean_unused_definitions.json')


def test_swift_generator():
    assert_test_generator(config_filename='swift_config.json',
                          schema_path=TEST_SCHEMA_PATH,
                          references_folder_name='swift')


def test_kotlin_generator():
    assert_test_generator(config_filename='kotlin_config.json',
                          schema_path=TEST_SCHEMA_PATH,
                          references_folder_name='kotlin')


def test_documentation_generator():
    assert_test_generator(config_filename='documentation_config.json',
                          schema_path=TEST_SCHEMA_PATH,
                          references_folder_name='documentation')


def assert_test_generator(config_filename: str, schema_path: str, references_folder_name: str):
    config = Config(config_path=utils.path_generator_tests(f'configs/{config_filename}'),
                    schema_path=schema_path,
                    output_path=OUTPUT_PATH)
    if not os.path.exists(OUTPUT_PATH):
        os.makedirs(OUTPUT_PATH)
    generate_api(config)
    references_path = utils.path_generator_tests(f'references/{references_folder_name}')

    if SHOULD_UPDATE_REFERENCES:
        utils.clear_content_of_directory(directory=references_path)
        utils.update_references(source_path=OUTPUT_PATH, destination_path=references_path)
        assert False, 'Updated references. Don\'t forget to restore SHOULD_UPDATE_REFERENCES flag.'

    __compare_dirs(references_path, OUTPUT_PATH)


def __compare_dirs(references_path: str, generated_path: str):
    for file in os.listdir(references_path):
        if os.path.isfile(os.path.join(references_path, file)):
            ref_file_path, gen_file_path = __join_paths(references_path, generated_path, file)
            __compare_files(file, ref_file_path, gen_file_path)
        else:
            ref_dir_path, gen_dir_path = __join_paths(references_path, generated_path, file)
            __compare_dirs(ref_dir_path, gen_dir_path)


def __join_paths(references_path: str, generated_path: str, new_path: str) -> (str, str):
    ref_path = os.path.join(references_path, new_path)
    gen_path = os.path.join(generated_path, new_path)
    return ref_path, gen_path


def __compare_files(filename: str, ref_file_path: str, generated_file_path: str):
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
