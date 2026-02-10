"""Tests for pydivkit.core.serialization module.

Covers dump(), _cast_value_type sub-functions, _make_exclude_fields,
_unpack_optional_type, and _merge_types.
"""

from __future__ import annotations

import enum
from typing import Any, Mapping, Optional, Sequence, Union

import pytest

from pydivkit.core import BaseEntity, Expr, Field
from pydivkit.core.serialization import (
    _cast_entity_or_coerce,
    _cast_mapping,
    _cast_sequence,
    _cast_union,
    _cast_value_type,
    _make_exclude_fields,
    _merge_types,
    _unpack_optional_type,
    dump,
)


# ---------------------------------------------------------------------------
# dump()
# ---------------------------------------------------------------------------
class TestDump:
    def test_string_passthrough(self) -> None:
        assert dump("hello") == "hello"

    def test_bytes_passthrough(self) -> None:
        assert dump(b"data") == b"data"

    def test_int_passthrough(self) -> None:
        assert dump(42) == 42

    def test_sequence_recurse(self) -> None:
        assert dump([1, 2, 3]) == [1, 2, 3]

    def test_mapping_recurse_values(self) -> None:
        """Regression test: mapping values are recursively dumped."""
        assert dump({"a": 1, "b": 2}) == {"a": 1, "b": 2}

    def test_mapping_with_expr_values(self) -> None:
        """Mapping values containing Expr are serialized."""
        expr = Expr("@{var}")
        result = dump({"key": expr})
        assert result == {"key": "@{var}"}

    def test_mapping_with_entity_values(self) -> None:
        """Mapping values containing BaseEntity are serialized."""

        class Inner(BaseEntity):
            val: int = Field(default=1)

        result = dump({"child": Inner(val=5)})
        assert result == {"child": {"val": 5}}

    def test_mapping_with_enum_values(self) -> None:
        """Mapping values containing enums are serialized."""

        class Color(enum.Enum):
            RED = "red"

        result = dump({"color": Color.RED})
        assert result == {"color": "red"}

    def test_expr_to_string(self) -> None:
        assert dump(Expr("@{x}")) == "@{x}"

    def test_entity_calls_dict(self) -> None:
        class E(BaseEntity):
            name: str = Field(default="hi")

        assert dump(E()) == {"name": "hi"}

    def test_enum_value(self) -> None:
        class Dir(enum.Enum):
            UP = "up"
            DOWN = "down"

        assert dump(Dir.UP) == "up"

    def test_nested_sequence_of_entities(self) -> None:
        class Item(BaseEntity):
            x: int = Field(default=0)

        result = dump([Item(x=1), Item(x=2)])
        assert result == [{"x": 1}, {"x": 2}]

    def test_deeply_nested_structure(self) -> None:
        """dump() handles deeply nested list-of-mapping combinations."""

        class Leaf(BaseEntity):
            v: int = Field(default=0)

        data = [{"items": [Leaf(v=1), Leaf(v=2)]}]
        result = dump(data)
        assert result == [{"items": [{"v": 1}, {"v": 2}]}]


# ---------------------------------------------------------------------------
# _cast_sequence
# ---------------------------------------------------------------------------
class TestCastSequence:
    def test_list_of_int(self) -> None:
        result = _cast_sequence([1, 2, 3], Sequence[int])
        assert result == [1, 2, 3]

    def test_list_with_coercion(self) -> None:
        result = _cast_sequence([1, 2], Sequence[float])
        assert result == [1.0, 2.0]
        assert all(isinstance(v, float) for v in result)

    def test_non_sequence_raises(self) -> None:
        with pytest.raises(ValueError, match="has wrong type"):
            _cast_sequence(42, Sequence[int])

    def test_empty_list(self) -> None:
        assert _cast_sequence([], Sequence[str]) == []


# ---------------------------------------------------------------------------
# _cast_mapping
# ---------------------------------------------------------------------------
class TestCastMapping:
    def test_simple_mapping(self) -> None:
        result = _cast_mapping({"a": 1}, Mapping[str, int])
        assert result == {"a": 1}

    def test_mapping_value_coercion(self) -> None:
        result = _cast_mapping({"a": 1}, Mapping[str, float])
        assert result == {"a": 1.0}
        assert isinstance(result["a"], float)

    def test_non_mapping_raises(self) -> None:
        with pytest.raises(ValueError, match="has wrong type"):
            _cast_mapping([1, 2], Mapping[str, int])


# ---------------------------------------------------------------------------
# _cast_union
# ---------------------------------------------------------------------------
class TestCastUnion:
    def test_none_in_optional(self) -> None:
        result = _cast_union(None, Optional[int])
        assert result is None

    def test_exact_type_match(self) -> None:
        result = _cast_union(42, Union[int, str])
        assert result == 42

    def test_fallback_coercion(self) -> None:
        result = _cast_union(42, Union[float, str])
        assert isinstance(result, float)
        assert result == 42.0

    def test_no_match_raises(self) -> None:
        with pytest.raises(ValueError, match="has wrong type"):
            _cast_union([], Union[int, float])

    def test_dict_dispatch_with_type_field(self) -> None:
        class Alpha(BaseEntity):
            type: str = Field(name="type", default="alpha")
            val: int = Field(default=0)

        class Beta(BaseEntity):
            type: str = Field(name="type", default="beta")
            val: int = Field(default=0)

        result = _cast_union({"type": "alpha", "val": 5}, Union[Alpha, Beta])
        assert isinstance(result, Alpha)
        assert result.val == 5

    def test_dict_without_type_field_raises(self) -> None:
        class Gamma(BaseEntity):
            type: str = Field(name="type", default="gamma")
            val: int = Field(default=0)

        class Delta(BaseEntity):
            type: str = Field(name="type", default="delta")
            val: int = Field(default=0)

        with pytest.raises(ValueError, match="does not have field"):
            _cast_union({"val": 1}, Union[Gamma, Delta])

    def test_dict_unknown_type_raises(self) -> None:
        class Eps(BaseEntity):
            type: str = Field(name="type", default="eps")
            val: int = Field(default=0)

        with pytest.raises(ValueError, match="does not contain"):
            _cast_union({"type": "unknown"}, Union[Eps, int])


# ---------------------------------------------------------------------------
# _cast_entity_or_coerce
# ---------------------------------------------------------------------------
class TestCastEntityOrCoerce:
    def test_isinstance_passthrough(self) -> None:
        class E(BaseEntity):
            val: int = Field(default=0)

        entity = E(val=1)
        assert _cast_entity_or_coerce(entity, E) is entity

    def test_wrong_entity_type_raises(self) -> None:
        class A(BaseEntity):
            val: int = Field(default=0)

        class B(BaseEntity):
            name: str = Field(default="x")

        with pytest.raises(ValueError, match="has wrong type"):
            _cast_entity_or_coerce(A(), B)

    def test_dict_to_entity(self) -> None:
        class E(BaseEntity):
            val: int = Field(default=0)

        result = _cast_entity_or_coerce({"val": 5}, E)
        assert isinstance(result, E)
        assert result.val == 5

    def test_type_coercion(self) -> None:
        result = _cast_entity_or_coerce(42, float)
        assert isinstance(result, float)
        assert result == 42.0

    def test_none_raises(self) -> None:
        with pytest.raises(ValueError, match="has wrong type"):
            _cast_entity_or_coerce(None, int)


# ---------------------------------------------------------------------------
# _cast_value_type (dispatcher)
# ---------------------------------------------------------------------------
class TestCastValueType:
    def test_field_value_raises(self) -> None:
        from pydivkit.core.fields import _Field

        with pytest.raises(ValueError, match="cannot be of type"):
            _cast_value_type(_Field(), int)

    def test_any_passthrough(self) -> None:
        obj = object()
        assert _cast_value_type(obj, Any) is obj

    def test_exact_type_fast_path(self) -> None:
        assert _cast_value_type(42, int) == 42

    def test_sequence_dispatch(self) -> None:
        result = _cast_value_type([1, 2], Sequence[int])
        assert result == [1, 2]

    def test_mapping_dispatch(self) -> None:
        result = _cast_value_type({"a": 1}, Mapping[str, int])
        assert result == {"a": 1}

    def test_union_dispatch(self) -> None:
        result = _cast_value_type(42, Union[int, str])
        assert result == 42

    def test_entity_coerce(self) -> None:
        result = _cast_value_type(42, float)
        assert isinstance(result, float)


# ---------------------------------------------------------------------------
# _make_exclude_fields
# ---------------------------------------------------------------------------
class TestMakeExcludeFields:
    def test_none_returns_empty(self) -> None:
        result = _make_exclude_fields(None)
        assert dict(result) == {}

    def test_empty_list_returns_empty(self) -> None:
        result = _make_exclude_fields([])
        assert dict(result) == {}

    def test_simple_fields(self) -> None:
        result = _make_exclude_fields(["a", "b"])
        assert dict(result) == {"a": True, "b": True}

    def test_nested_fields(self) -> None:
        result = _make_exclude_fields(["a.b"])
        assert "a" in result
        assert dict(result["a"]) == {"b": True}

    def test_mixed_fields(self) -> None:
        result = _make_exclude_fields(["a", "b.c"])
        assert result["a"] is True
        assert dict(result["b"]) == {"c": True}

    def test_empty_field_name_raises(self) -> None:
        with pytest.raises(ValueError, match="cannot be empty"):
            _make_exclude_fields([""])

    def test_deeply_nested(self) -> None:
        result = _make_exclude_fields(["a.b.c"])
        inner = result["a"]
        assert dict(inner["b"]) == {"c": True}


# ---------------------------------------------------------------------------
# _unpack_optional_type
# ---------------------------------------------------------------------------
class TestUnpackOptionalType:
    def test_non_union_unchanged(self) -> None:
        assert _unpack_optional_type(int) is int

    def test_optional_unwraps(self) -> None:
        result = _unpack_optional_type(Optional[int])
        assert result is int

    def test_union_without_none_unchanged(self) -> None:
        result = _unpack_optional_type(Union[int, str])
        assert result == Union[int, str]


# ---------------------------------------------------------------------------
# _merge_types
# ---------------------------------------------------------------------------
class TestMergeTypes:
    def test_same_types(self) -> None:
        assert _merge_types(int, int) is int

    def test_optional_merge(self) -> None:
        assert _merge_types(Optional[int], int) is int

    def test_incompatible_raises(self) -> None:
        with pytest.raises(TypeError, match="Incompatible"):
            _merge_types(int, str)
