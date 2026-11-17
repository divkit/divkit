import pytest

try:
    from divkit.public.api_generator.api_generator.config import Config
except ModuleNotFoundError:
    from api_generator.config import Config


@pytest.mark.parametrize('key, attribute', [
    ('header', 'header'),
    ('serializersHeader', 'serializers_header'),
])
@pytest.mark.parametrize('header, expected', [
    (None, ''),
    ('', ''),
    ([], ''),
    ([''], ''),
    ('// Generated code.', '// Generated code.'),
    ('// Generated code.\n\npackage generated\n', '// Generated code.\n\npackage generated\n'),
    (['// Generated code.'], '// Generated code.'),
    (['// Generated code.', 'package generated'], '// Generated code.\npackage generated'),
    (['// Generated code.', '', 'package generated', ''], '// Generated code.\n\npackage generated\n'),
    (['', '    // Indented', '', ''], '\n    // Indented\n\n'),
])
def test_header_formats(key, attribute, header, expected):
    config = Config.GenerationConfig({'lang': 'kotlin', key: header})

    assert getattr(config, attribute) == expected


def test_headers_default_to_empty_strings():
    config = Config.GenerationConfig({'lang': 'kotlin'})

    assert config.header == ''
    assert config.serializers_header == ''
