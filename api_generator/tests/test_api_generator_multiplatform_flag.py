import json
import pytest
import utils

try:
    from divkit.public.api_generator.api_generator.config import Config
    from divkit.public.api_generator.api_generator.generator import generate_api
except ModuleNotFoundError:
    from api_generator.config import Config
    from api_generator.generator import generate_api

TEST_SCHEMA_PATH = utils.path_divkit_test_data('test_schema')


def generate(tmp_path, **options):
    config_path = tmp_path / 'config.json'
    config_path.write_text(json.dumps(dict(
        lang='kotlin',
        header='package generated',
        generateSerializers=True,
        errorsCollectors=['entity_with_required_property'],
        **options,
    )))
    output = tmp_path / 'generated'
    config = Config(
        generator_path=None,
        config_path=str(config_path),
        schema_path=TEST_SCHEMA_PATH,
        output_path=str(output),
    )
    generate_api(config, check_hash_files=False, save_hash_files=False)
    return {path.name: path.read_text() for path in output.glob('*.kt')}


@pytest.fixture(scope='module')
def multiplatform_files(tmp_path_factory):
    return generate(tmp_path_factory.mktemp('multiplatform'), generateModels=False, multiplatform=True)


def assert_contains(files, name, *snippets):
    missing = [snippet for snippet in snippets if snippet not in files[name]]
    assert not missing, f'{name} does not contain {missing}'


def test_multiplatform_generates_only_parsers(multiplatform_files):
    assert 'JsonParserComponent.kt' in multiplatform_files
    assert 'EntityJsonParser.kt' in multiplatform_files
    not_parsers = [name for name in multiplatform_files
                   if not name.endswith('JsonParser.kt') and name != 'JsonParserComponent.kt']
    assert not_parsers == []


def test_multiplatform_parsers_use_json_object(multiplatform_files):
    assert_contains(
        multiplatform_files, 'EntityWithRequiredPropertyJsonParser.kt',
        'Parser<JsonObject, EntityWithRequiredProperty>',
        'TemplateParser<JsonObject, EntityWithRequiredPropertyTemplate>',
        'TemplateResolver<JsonObject, EntityWithRequiredPropertyTemplate, EntityWithRequiredProperty>',
    )
    assert_contains(multiplatform_files, 'EntityJsonParser.kt', 'Parser<JsonObject, Entity>')


def test_multiplatform_serializes_with_builder(multiplatform_files):
    # Whole lines: writes mutate the builder and are never assigned back to `data`.
    assert_contains(
        multiplatform_files, 'EntityWithRequiredPropertyJsonParser.kt',
        '\n            val data = JsonObject.builder()\n',
        '\n            JsonExpressionParser.writeExpression(context, data, "property", value.property)\n',
        '\n            JsonFieldParser.writeExpressionField(context, data, "property", value.property)\n',
        '\n            JsonPropertyParser.write(context, data, "type", EntityWithRequiredProperty.TYPE)\n',
        '\n            return data.build()\n',
    )


@pytest.mark.parametrize('name, transform', [
    ('EntityWithJsonPropertyJsonParser.kt', 'JSON_TO_ORG_OBJECT'),
    ('EntityWithRawArrayJsonParser.kt', 'JSON_TO_ORG_ARRAY'),
])
def test_multiplatform_converts_json_values_to_org_json(multiplatform_files, name, transform):
    assert_contains(multiplatform_files, name, transform)


def test_multiplatform_collects_errors(multiplatform_files):
    assert_contains(
        multiplatform_files, 'EntityWithRequiredPropertyJsonParser.kt',
        'val context = context.collectingErrors()',
        'parsingErrors = context.collectedErrors',
    )


def test_multiplatform_reads_values(multiplatform_files):
    assert_contains(
        multiplatform_files, 'EntityWithSimplePropertiesJsonParser.kt',
        'id = JsonPropertyParser.readOptional(context, data, "id", NUMBER_TO_INT)',
        'id = JsonFieldParser.readOptionalField(context, data, "id", allowOverride, parent?.id, NUMBER_TO_INT)',
        'string = JsonExpressionParser.readOptionalExpression(context, data, "string", TYPE_HELPER_STRING)',
    )


def test_multiplatform_reads_nested_entities(multiplatform_files):
    assert_contains(
        multiplatform_files, 'EntityWithArrayOfNestedItemsJsonParser.kt',
        'internal class EntityWithArrayOfNestedItemsItemJsonParser(',
        'items = JsonPropertyParser.readList(context, data, "items", '
        'component.entityWithArrayOfNestedItemsItemJsonEntityParser, ',
    )


def test_multiplatform_resolves_entity_type(multiplatform_files):
    assert_contains(
        multiplatform_files, 'EntityJsonParser.kt',
        'val type: String = JsonPropertyParser.readString(context, data, "type")',
        'val template = context.templates.getOrThrow(type, data) as? EntityTemplate',
    )


def test_default_generation_matches_explicit_legacy_mode(tmp_path):
    implicit_path = tmp_path / 'implicit'
    explicit_path = tmp_path / 'explicit'
    implicit_path.mkdir()
    explicit_path.mkdir()
    implicit = generate(implicit_path)
    explicit = generate(explicit_path, generateModels=True, multiplatform=False)
    assert implicit == explicit
    assert 'Entity.kt' in implicit
    assert 'EntityTemplate.kt' in implicit
    assert 'Parser<JSONObject, EntityWithRequiredProperty>' in implicit['EntityWithRequiredPropertyJsonParser.kt']
    assert 'val data = JSONObject()' in implicit['EntityWithRequiredPropertyJsonParser.kt']
    assert 'data = JsonPropertyParser.write' not in implicit['EntityWithRequiredPropertyJsonParser.kt']


def test_parser_only_org_json_mode(tmp_path):
    files = generate(tmp_path, generateModels=False)
    assert 'Entity.kt' not in files
    assert 'EntityTemplate.kt' not in files
    assert 'Parser<JSONObject, EntityWithRequiredProperty>' in files['EntityWithRequiredPropertyJsonParser.kt']


@pytest.mark.parametrize('options', [
    {'lang': 'swift', 'generateModels': False},
    {'lang': 'kotlin', 'generateModels': False},
    {'lang': 'kotlin', 'generateSerializers': True, 'multiplatform': True},
])
def test_invalid_parser_modes_are_rejected(options):
    with pytest.raises(ValueError):
        Config.GenerationConfig(options)
