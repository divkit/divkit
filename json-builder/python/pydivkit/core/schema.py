from __future__ import annotations

import enum
from types import MappingProxyType
from typing import (
    Any,
    Dict,
    Mapping,
    Optional,
    Sequence,
    Type,
    Union,
    get_args,
    get_origin,
)

from .fields import Expr, _Field

TYPE_FIELD = "type"

# ExcludeFieldsType: str -> ExcludeFieldsType | bool
ExcludeFieldsType = Mapping[str, Any]
SchemaType = Dict[str, Any]


BUILTIN_TYPES_TO_SCHEMA: Mapping[type, Mapping[str, Any]] = MappingProxyType(
    {
        int: MappingProxyType({"type": "integer"}),
        float: MappingProxyType({"type": "number"}),
        bool: MappingProxyType(
            {
                "type": "integer",
                "enum": [0, 1],
                "format": "boolean",
            },
        ),
        str: MappingProxyType({"type": "string"}),
        bytes: MappingProxyType({"type": "string"}),
        Expr: MappingProxyType({"type": "string", "pattern": "^@{.*}$"}),
        Any: MappingProxyType({}),
    },
)


def _type_field_to_schema(field: _Field) -> SchemaType:
    return {
        "type": "string",
        "enum": [field.default],
    }


def _enum_to_schema(type_: Type[enum.Enum]) -> SchemaType:
    return {
        "type": "string",
        "enum": [enum_el.value for enum_el in type_],
    }


def _list_to_schema(
    type_: Any,
    definitions: Dict[str, SchemaType],
    exclude: ExcludeFieldsType,
) -> SchemaType:
    item_type, *_ = get_args(type_)
    return {
        "type": "array",
        "items": _field_to_schema(None, item_type, exclude, definitions),
    }


def _dict_to_schema() -> SchemaType:
    return {
        "type": "object",
        "additionalProperties": True,
    }


def _union_to_schema(
    field: Optional[_Field],
    type_: Any,
    exclude: ExcludeFieldsType,
    definitions: Dict[str, SchemaType],
) -> SchemaType:
    return {
        "anyOf": [
            _field_to_schema(field, arg_type, exclude, definitions)
            for arg_type in get_args(type_)
        ],
    }


def _add_field_extra_to_schema(
    field: _Field,
    schema: SchemaType,
) -> None:
    if field.description:
        schema["description"] = field.description
    if field.default:
        schema["default"] = field.default
    schema.update(**field.constraints)


def _field_to_schema(
    field: Optional[_Field],
    type_: Any,
    exclude: ExcludeFieldsType,
    definitions: Dict[str, SchemaType],
) -> SchemaType:
    from .entities import BaseEntity
    from .serialization import _unpack_optional_type

    type_ = _unpack_optional_type(type_)
    origin = get_origin(type_)

    schema: Optional[SchemaType] = None
    if field and field.name == TYPE_FIELD and field.default:
        schema = _type_field_to_schema(field)
    elif type_ in BUILTIN_TYPES_TO_SCHEMA:
        schema = {**BUILTIN_TYPES_TO_SCHEMA[type_]}
    elif isinstance(origin, type) and issubclass(origin, Sequence):
        schema = _list_to_schema(type_, definitions, exclude)
    elif isinstance(origin, type) and issubclass(origin, Mapping):
        schema = _dict_to_schema()
    elif origin is Union:
        schema = _union_to_schema(field, type_, exclude, definitions)
    elif issubclass(type_, BaseEntity):
        schema = type_.schema_as_ref(definitions, exclude)
    elif issubclass(type_, enum.Enum):
        schema = _enum_to_schema(type_)

    if schema is None:
        raise TypeError(f"Schema building error for unknown type {type_}")

    if field:
        _add_field_extra_to_schema(field, schema)

    return schema
