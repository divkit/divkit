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
REAL_SCHEMA_PATH = utils.path_divkit_test_data(os.path.join('..', 'schema'))
API_GENERATOR_PATH = utils.path_generator_tests(os.path.join('..', 'api_generator'))
OUTPUT_PATH = utils.path_generator_tests('output')


def test_schema_preprocessor():
    def assert_json_preprocessor_test(filename: str) -> None:
        path = utils.path_generator_tests(os.path.join('references', filename))
        if SHOULD_UPDATE_REFERENCES:
            utils.update_json_reference(filename=path, json_content=root_directory.as_json)
            assert False, 'Updated references. Don\'t forget to restore SHOULD_UPDATE_REFERENCES flag.'
        utils.assert_as_json_test(file_expected=path, content_actual=root_directory.as_json)
    test_config = Config(generator_path=API_GENERATOR_PATH,
                         config_path=utils.path_generator_tests(os.path.join('configs', 'swift_config.json')),
                         schema_path=TEST_SCHEMA_PATH,
                         output_path=OUTPUT_PATH)

    root_directory = resolve_structure(test_config)
    assert_json_preprocessor_test(filename='reference_resolve_structure.json')

    root_directory.merge_all_ofs(test_config.generation.lang)
    assert_json_preprocessor_test(filename='reference_merge_allOfs.json')

    root_directory.resolve_references(test_config.generation.lang)
    assert_json_preprocessor_test(filename='reference_resolve_references.json')

    root_directory.clean_unused_definitions()
    assert_json_preprocessor_test(filename='reference_clean_unused_definitions.json')


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


def test_type_script_generator():
    assert_test_generator(config_filename='typescript_config.json',
                          schema_path=TEST_SCHEMA_PATH,
                          references_folder_name='type_script')


def test_python_generator():
    assert_test_generator(config_filename='python_config.json',
                          schema_path=TEST_SCHEMA_PATH,
                          references_folder_name='python')


def assert_test_generator(config_filename: str, schema_path: str, references_folder_name: str):
    config = Config(generator_path=API_GENERATOR_PATH,
                    config_path=utils.path_generator_tests(os.path.join('configs', config_filename)),
                    schema_path=schema_path,
                    output_path=OUTPUT_PATH)
    if not os.path.exists(OUTPUT_PATH):
        os.makedirs(OUTPUT_PATH)
    generate_api(config)
    references_path = utils.path_generator_tests(os.path.join('references', references_folder_name))

    if SHOULD_UPDATE_REFERENCES:
        utils.clear_content_of_directory(directory=references_path)
        utils.update_references(source_path=OUTPUT_PATH, destination_path=references_path)
        assert False, 'Updated references. Don\'t forget to restore SHOULD_UPDATE_REFERENCES flag.'

    utils.compare_dirs(references_path, OUTPUT_PATH)
