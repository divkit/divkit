"""Tests for pydivkit.core.schema module.

Covers _field_to_schema and its helper functions directly.
"""

from __future__ import annotations

import enum
from typing import Any, Mapping, Optional, Sequence, Union

import pytest

from pydivkit.core import BaseEntity, Expr, Field
from pydivkit.core.fields import _Field
from pydivkit.core.schema import (
    BUILTIN_TYPES_TO_SCHEMA,
    SchemaType,
    _add_field_extra_to_schema,
    _dict_to_schema,
    _enum_to_schema,
    _field_to_schema,
    _list_to_schema,
    _type_field_to_schema,
    _union_to_schema,
)


# ---------------------------------------------------------------------------
# _type_field_to_schema
# ---------------------------------------------------------------------------
class TestTypeFieldToSchema:
    def test_produces_enum_schema(self) -> None:
        field = _Field(name="type", default="my_component")
        result = _type_field_to_schema(field)
        assert result == {
            "type": "string",
            "enum": ["my_component"],
        }


# ---------------------------------------------------------------------------
# _enum_to_schema
# ---------------------------------------------------------------------------
class TestEnumToSchema:
    def test_multiple_values(self) -> None:
        class Direction(enum.Enum):
            LEFT = "left"
            RIGHT = "right"
            UP = "up"

        result = _enum_to_schema(Direction)
        assert result["type"] == "string"
        assert set(result["enum"]) == {"left", "right", "up"}

    def test_single_value(self) -> None:
        class Single(enum.Enum):
            ONLY = "only"

        result = _enum_to_schema(Single)
        assert result == {"type": "string", "enum": ["only"]}


# ---------------------------------------------------------------------------
# _list_to_schema
# ---------------------------------------------------------------------------
class TestListToSchema:
    def test_sequence_of_int(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _list_to_schema(Sequence[int], defs, {})
        assert result == {
            "type": "array",
            "items": {"type": "integer"},
        }

    def test_sequence_of_str(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _list_to_schema(Sequence[str], defs, {})
        assert result == {
            "type": "array",
            "items": {"type": "string"},
        }


# ---------------------------------------------------------------------------
# _dict_to_schema
# ---------------------------------------------------------------------------
class TestDictToSchema:
    def test_returns_object_schema(self) -> None:
        result = _dict_to_schema()
        assert result == {
            "type": "object",
            "additionalProperties": True,
        }


# ---------------------------------------------------------------------------
# _union_to_schema
# ---------------------------------------------------------------------------
class TestUnionToSchema:
    def test_anyof_generation(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _union_to_schema(None, Union[int, str], {}, defs)
        assert "anyOf" in result
        type_values = [a["type"] for a in result["anyOf"]]
        assert "integer" in type_values
        assert "string" in type_values

    def test_multi_type_union(self) -> None:
        """Union[int, str, float] produces anyOf with all arms."""
        defs: dict[str, SchemaType] = {}
        result = _union_to_schema(None, Union[int, str, float], {}, defs)
        assert "anyOf" in result
        assert len(result["anyOf"]) == 3


# ---------------------------------------------------------------------------
# _add_field_extra_to_schema
# ---------------------------------------------------------------------------
class TestAddFieldExtraToSchema:
    def test_description_added(self) -> None:
        field = _Field(description="A field")
        schema: SchemaType = {"type": "string"}
        _add_field_extra_to_schema(field, schema)
        assert schema["description"] == "A field"

    def test_default_added(self) -> None:
        field = _Field(default="val")
        schema: SchemaType = {"type": "string"}
        _add_field_extra_to_schema(field, schema)
        assert schema["default"] == "val"

    def test_constraints_added(self) -> None:
        field = _Field(constraints={"minimum": 0, "maximum": 100})
        schema: SchemaType = {"type": "integer"}
        _add_field_extra_to_schema(field, schema)
        assert schema["minimum"] == 0
        assert schema["maximum"] == 100

    def test_empty_field_no_extras(self) -> None:
        field = _Field()
        schema: SchemaType = {"type": "integer"}
        _add_field_extra_to_schema(field, schema)
        assert schema == {"type": "integer"}


# ---------------------------------------------------------------------------
# _field_to_schema (main dispatcher)
# ---------------------------------------------------------------------------
class TestFieldToSchema:
    def test_type_field_dispatch(self) -> None:
        field = _Field(name="type", default="comp")
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(field, str, {}, defs)
        assert result["type"] == "string"
        assert result["enum"] == ["comp"]

    def test_builtin_int(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, int, {}, defs)
        assert result == {"type": "integer"}

    def test_builtin_str(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, str, {}, defs)
        assert result == {"type": "string"}

    def test_builtin_float(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, float, {}, defs)
        assert result == {"type": "number"}

    def test_builtin_bool(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, bool, {}, defs)
        assert result["type"] == "integer"
        assert result["format"] == "boolean"

    def test_expr_type(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, Expr, {}, defs)
        assert result["type"] == "string"
        assert "pattern" in result

    def test_any_type(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, Any, {}, defs)
        assert result == {}

    def test_sequence_type(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, Sequence[int], {}, defs)
        assert result["type"] == "array"
        assert result["items"]["type"] == "integer"

    def test_mapping_type(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, Mapping[str, Any], {}, defs)
        assert result["type"] == "object"

    def test_union_type(self) -> None:
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, Union[int, str], {}, defs)
        assert "anyOf" in result

    def test_entity_type(self) -> None:
        class Nested(BaseEntity):
            x: int = Field(default=0)

        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, Nested, {}, defs)
        assert "$ref" in result
        assert "Nested" in result["$ref"]

    def test_enum_type(self) -> None:
        class Color(enum.Enum):
            RED = "red"
            GREEN = "green"

        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, Color, {}, defs)
        assert result["type"] == "string"
        assert set(result["enum"]) == {"red", "green"}

    def test_unknown_type_raises(self) -> None:
        defs: dict[str, SchemaType] = {}
        with pytest.raises(TypeError, match="unknown type"):
            _field_to_schema(None, object, {}, defs)

    def test_optional_unwraps(self) -> None:
        """Optional[int] unwraps to int schema."""
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(None, Optional[int], {}, defs)
        assert result == {"type": "integer"}

    def test_field_extras_applied(self) -> None:
        field = _Field(
            description="count",
            constraints={"minimum": 0},
        )
        defs: dict[str, SchemaType] = {}
        result = _field_to_schema(field, int, {}, defs)
        assert result["type"] == "integer"
        assert result["description"] == "count"
        assert result["minimum"] == 0


# ---------------------------------------------------------------------------
# BUILTIN_TYPES_TO_SCHEMA coverage
# ---------------------------------------------------------------------------
class TestBuiltinTypesToSchema:
    def test_all_expected_types_present(self) -> None:
        expected = {int, float, bool, str, bytes, Expr, Any}
        assert set(BUILTIN_TYPES_TO_SCHEMA.keys()) == expected

    def test_immutable(self) -> None:
        """BUILTIN_TYPES_TO_SCHEMA should be immutable."""
        with pytest.raises(TypeError):
            BUILTIN_TYPES_TO_SCHEMA[int] = {}  # type: ignore[index]
