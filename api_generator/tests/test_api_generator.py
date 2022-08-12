import utils

try:
    # full package name for ya make
    from divkit.public.api_generator.api_generator.config import Config
    from divkit.public.api_generator.api_generator.schema.preprocessing.preprocessor import resolve_structure

except ModuleNotFoundError:
    from api_generator.config import Config
    from api_generator.schema.preprocessing.preprocessor import resolve_structure

# Set SHOULD_UPDATE_REFERENCES = True and run pytest to update reference files.
SHOULD_UPDATE_REFERENCES = False

test_config = Config(config_path=utils.path_generator_tests('configs/swift_config.json'),
                     schema_path=utils.path_divkit_test_data('test_schema'),
                     output_path=utils.path_generator_tests(''))


def test_schema_preprocessor():
    def assert_json_preprocessor_test(filename: str) -> None:
        path = utils.path_generator_tests(filename)
        if SHOULD_UPDATE_REFERENCES:
            utils.update_json_reference(filename=path, json_content=root_directory.as_json)
        utils.assert_as_json_test(file_expected=path, content_actual=root_directory.as_json)

    root_directory = resolve_structure(test_config)
    assert_json_preprocessor_test(filename='references/reference_resolve_structure.json')

    root_directory.merge_all_ofs(test_config.generation.lang)
    assert_json_preprocessor_test(filename='references/reference_merge_allOfs.json')

    root_directory.resolve_references(test_config.generation.lang)
    assert_json_preprocessor_test(filename='references/reference_resolve_references.json')

    root_directory.clean_unused_definitions()
    assert_json_preprocessor_test(filename='references/reference_clean_unused_definitions.json')
