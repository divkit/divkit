from __future__ import annotations

import enum
from collections import defaultdict
from contextlib import suppress
from types import MappingProxyType
from typing import (
    Any,
    Dict,
    List,
    Mapping,
    Optional,
    Sequence,
    Set,
    Type,
    Union,
    get_args,
    get_origin,
)

from .fields import Expr, _Field
from .types.union import get_union_types, inject_types, is_union_injected

TYPE_FIELD = "type"

# ExcludeFieldsType: str -> ExcludeFieldsType | bool
ExcludeFieldsType = Mapping[str, Any]


def _make_exclude_fields(
    exclude_fields: Optional[Sequence[str]] = None,
) -> ExcludeFieldsType:
    if not exclude_fields:
        return MappingProxyType({})
    exclude: Dict[str, Any] = {}
    exclude_nested: Dict[str, List[str]] = defaultdict(list)
    for exclude_field in exclude_fields:
        if not exclude_field:
            raise ValueError("Field name cannot be empty")
        field_name, *nested_fields = exclude_field.split(".")
        if nested_fields:
            exclude_nested[field_name].append(".".join(nested_fields))
        else:
            exclude[field_name] = True
    for field_name, nested_fields in exclude_nested.items():
        if field_name not in exclude:
            exclude[field_name] = _make_exclude_fields(nested_fields)
    return MappingProxyType(exclude)


def _cast_sequence(value: Any, type_: Any) -> Any:
    if not isinstance(value, Sequence):
        raise ValueError(
            f"Value {value} has wrong type: {type(value)}. Expected type is {type_}.",
        )
    element_type, *_ = get_args(type_)
    return [_cast_value_type(v, element_type) for v in value]


def _cast_mapping(value: Any, type_: Any) -> Any:
    if not isinstance(value, Mapping):
        raise ValueError(
            f"Value {value} has wrong type: {type(value)}. Expected type is {type_}.",
        )
    key_type, value_type = get_args(type_)
    return {
        _cast_value_type(k, key_type): _cast_value_type(v, value_type)
        for k, v in value.items()
    }


def _cast_union(value: Any, type_: Any) -> Any:
    args_type = get_args(type_)

    if value is None and type(None) in args_type:
        return None

    if not is_union_injected(type_):
        inject_types(type_)
    types = get_union_types(type_)
    if types and isinstance(value, dict):
        type_value = value.get(TYPE_FIELD)
        if not type_value:
            raise ValueError(
                f"Value {value} does not have field {TYPE_FIELD}.",
            )
        target_type = types.get(type_value)
        if not target_type:
            raise ValueError(
                f"Union {type_} does not contain type {type_value}.",
            )
        return target_type(**value)

    if type(value) in args_type:
        return value

    for u_type in args_type:
        with suppress(ValueError):
            return _cast_value_type(value, u_type)

    raise ValueError(
        f"Value {value} has wrong type: {type(value)}. Expected type is {type_}.",
    )


def _cast_entity_or_coerce(value: Any, type_: Any) -> Any:
    from .entities import BaseEntity

    if isinstance(value, type_):
        return value

    if isinstance(value, BaseEntity):
        raise ValueError(
            f"Value {value} has wrong type. Expected type is {type_}",
        )

    if issubclass(type_, BaseEntity) and isinstance(value, dict):
        return type_(**value)

    if value is not None:
        try:
            return type_(value)
        except Exception:
            pass

    raise ValueError(
        f"Value {value} has wrong type. Expected type is {type_}.",
    )


def _cast_value_type(value: Any, type_: Any) -> Any:
    if isinstance(value, _Field):
        raise ValueError("Value cannot be of type _Field")

    if type_ is Any:
        return value

    if type(value) is type_:
        return value

    origin_type = get_origin(type_)

    if isinstance(origin_type, type) and issubclass(origin_type, Sequence):
        return _cast_sequence(value, type_)

    if isinstance(origin_type, type) and issubclass(origin_type, Mapping):
        return _cast_mapping(value, type_)

    if origin_type is Union:
        return _cast_union(value, type_)

    return _cast_entity_or_coerce(value, type_)


def dump(obj: Any) -> Any:
    from .entities import BaseEntity

    if isinstance(obj, (str, bytes)):
        return obj
    if isinstance(obj, Sequence):
        return [dump(obj_item) for obj_item in obj]
    if isinstance(obj, Mapping):
        return {k: dump(v) for k, v in obj.items()}
    if isinstance(obj, Expr):
        return str(obj)
    if isinstance(obj, BaseEntity):
        return obj.dict()
    if isinstance(obj, enum.Enum):
        return obj.value
    return obj


def _update_related_templates(
    value: Any,
    related_templates: Set[Type[Any]],
) -> None:
    from .entities import BaseEntity

    if isinstance(value, BaseEntity):
        related_templates.update(value.related_templates())
    elif isinstance(value, list):
        for v_item in value:
            _update_related_templates(v_item, related_templates)


def _unpack_optional_type(type_: Any) -> Any:
    if get_origin(type_) is Union:
        inner_types = []
        for inner_type in get_args(type_):
            if get_origin(inner_type) or not isinstance(None, inner_type):
                inner_types.append(inner_type)
        return Union[tuple(inner_types)]
    return type_


def _merge_types(fst_type: Any, snd_type: Any) -> Any:
    fst_type = _unpack_optional_type(fst_type)
    snd_type = _unpack_optional_type(snd_type)
    if fst_type == snd_type:
        return fst_type
    raise TypeError(f"Incompatible types: {fst_type} and {snd_type}")
