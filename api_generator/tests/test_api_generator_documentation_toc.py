import json
import sys

import pytest
import utils

try:
    from divkit.public.api_generator.api_generator import __main__ as api_generator_main
    from divkit.public.api_generator.api_generator.config import Config
    from divkit.public.api_generator.api_generator.generator import generate_api
    from divkit.public.api_generator.api_generator.generators.documentation.toc import (
        DocumentationTocError,
    )
except ModuleNotFoundError:
    from api_generator import __main__ as api_generator_main
    from api_generator.config import Config
    from api_generator.generator import generate_api
    from api_generator.generators.documentation.toc import DocumentationTocError

TEST_SCHEMA_PATH = utils.path_divkit_test_data("test_schema")

ENTITY_MEMBERS = [
    "entity_with_array",
    "entity_with_array_of_enums",
    "entity_with_array_of_expressions",
    "entity_with_array_of_nested_items",
    "entity_with_array_with_transform",
    "entity_with_complex_property",
    "entity_with_complex_property_with_default_value",
    "entity_with_entity_property",
    "entity_with_optional_complex_property",
    "entity_with_optional_property",
    "entity_with_optional_string_enum_property",
    "entity_with_property_with_default_value",
    "entity_with_raw_array",
    "entity_with_required_property",
    "entity_with_simple_properties",
    "entity_with_string_array_property",
    "entity_with_string_enum_property",
    "entity_with_string_enum_property_with_default_value",
    "entity_without_properties",
]

TOC = {
    "root": {
        "name": {"ru": "Справочник", "en": "Reference"},
        "expanded": True,
        "items": [
            {
                "name": {"ru": "Сущности", "en": "Entities"},
                "any_of": "entity",
                "items": ["entity_with_simple_properties"],
            },
            {
                "name": {"ru": "Перечисления (anyOf)", "en": "Enumerations (anyOf)"},
                "items": [
                    {
                        "page": "enum_with_default_type",
                        "any_of": "enum_with_default_type",
                    }
                ],
            },
            {
                "page": "entity_with_json_property",
                "name": {"ru": "JSON-свойство", "en": "JSON property"},
            },
        ],
    },
    "helpers": ["entity"],
}


def generate(tmp_path, toc):
    toc_path = tmp_path / "toc.json"
    toc_path.write_text(json.dumps(toc, ensure_ascii=False), encoding="utf-8")
    config_path = tmp_path / "config.json"
    config_path.write_text(json.dumps({"lang": "documentation", "toc": "toc.json"}))
    output = tmp_path / "generated"
    config = Config(
        generator_path=None,
        config_path=str(config_path),
        schema_path=TEST_SCHEMA_PATH,
        output_path=str(output),
    )
    generate_api(config)
    return output


def page(name, indent):
    prefix = " " * indent
    return [f"{prefix}- name: {name}", f"{prefix}  href: concepts/divs/2/{name}.md"]


def test_grouped_toc_places_any_of_members_after_explicit_items(tmp_path):
    output = generate(tmp_path, TOC)

    members = ["entity_with_simple_properties"] + [
        m for m in ENTITY_MEMBERS if m != "entity_with_simple_properties"
    ]
    expected = [
        "items:",
        "  - name: Справочник",
        "    expanded: true",
        "    items:",
        "      - name: Сущности",
        "        items:",
        *[line for member in members for line in page(member, 10)],
        "      - name: Перечисления (anyOf)",
        "        items:",
        *page("enum_with_default_type", 10),
        "            items:",
        *page("with_default", 14),
        *page("without_default", 14),
        "      - name: JSON-свойство",
        "        href: concepts/divs/2/entity_with_json_property.md",
        "  - name: entity",
        "    href: concepts/divs/2/entity.md",
        "    hidden: true",
    ]
    assert (output / "toc.ru.yaml").read_text(encoding="utf-8") == "\n".join(
        expected
    ) + "\n"
    assert not (output / "toc.yaml").exists()


def test_grouped_toc_uses_language_specific_group_names(tmp_path):
    output = generate(tmp_path, TOC)

    english = (output / "toc.en.yaml").read_text(encoding="utf-8")
    assert "  - name: Reference\n" in english
    assert "      - name: JSON property\n" in english
    assert "Справочник" not in english


def test_page_without_group_fails(tmp_path):
    toc = json.loads(json.dumps(TOC))
    toc["root"]["items"].pop()

    with pytest.raises(
        DocumentationTocError, match='"entity_with_json_property" is in no group'
    ):
        generate(tmp_path, toc)


def test_configured_name_without_schema_fails(tmp_path):
    toc = json.loads(json.dumps(TOC))
    toc["helpers"].append("entity_that_does_not_exist")

    with pytest.raises(
        DocumentationTocError,
        match='"entity_that_does_not_exist" has no generated page',
    ):
        generate(tmp_path, toc)


def test_page_placed_twice_fails(tmp_path):
    toc = json.loads(json.dumps(TOC))
    toc["helpers"].append("entity_with_json_property")

    with pytest.raises(
        DocumentationTocError, match='"entity_with_json_property" is already placed'
    ):
        generate(tmp_path, toc)


def test_any_of_of_a_plain_entity_fails(tmp_path):
    toc = json.loads(json.dumps(TOC))
    toc["root"]["items"][2]["any_of"] = "entity_with_json_property"

    with pytest.raises(
        DocumentationTocError, match='"entity_with_json_property" is not an enumeration'
    ):
        generate(tmp_path, toc)


def test_unknown_node_key_fails(tmp_path):
    toc = json.loads(json.dumps(TOC))
    toc["root"]["items"][0]["hidden"] = True

    with pytest.raises(DocumentationTocError, match="unknown keys \\['hidden'\\]"):
        generate(tmp_path, toc)


def test_cli_exits_with_error_message(tmp_path, monkeypatch, capsys):
    toc = json.loads(json.dumps(TOC))
    toc["helpers"] = []
    (tmp_path / "toc.json").write_text(
        json.dumps(toc, ensure_ascii=False), encoding="utf-8"
    )
    (tmp_path / "config.json").write_text(
        json.dumps({"lang": "documentation", "toc": "toc.json"})
    )
    monkeypatch.setattr(
        sys,
        "argv",
        [
            "api_generator",
            "-c",
            str(tmp_path / "config.json"),
            "-s",
            TEST_SCHEMA_PATH,
            "-o",
            str(tmp_path / "generated"),
        ],
    )

    with pytest.raises(SystemExit) as exit_info:
        api_generator_main.main()

    assert exit_info.value.code == 1
    assert 'page "entity" is in no group' in capsys.readouterr().err


def test_comment_key_is_allowed(tmp_path):
    toc = json.loads(json.dumps(TOC))
    toc["comment"] = "free text"

    output = generate(tmp_path, toc)

    assert (output / "toc.ru.yaml").exists()


def test_missing_toc_config_fails_with_a_message(tmp_path, monkeypatch, capsys):
    (tmp_path / "config.json").write_text(
        json.dumps({"lang": "documentation", "toc": "missing.json"})
    )
    monkeypatch.setattr(
        sys,
        "argv",
        [
            "api_generator",
            "-c",
            str(tmp_path / "config.json"),
            "-s",
            TEST_SCHEMA_PATH,
            "-o",
            str(tmp_path / "generated"),
        ],
    )

    with pytest.raises(SystemExit) as exit_info:
        api_generator_main.main()

    assert exit_info.value.code == 1
    assert "missing.json: cannot read the TOC config" in capsys.readouterr().err
