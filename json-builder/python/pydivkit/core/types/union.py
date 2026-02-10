from types import MappingProxyType
from typing import Any, Dict, Optional, Type, Union, get_args, get_origin

# Module-level caches for Union type metadata.
# Python 3.14+ no longer allows setting attributes on typing.Union objects,
# so we store the injected data externally.  Union types (both typing.Union
# and types.UnionType on 3.10+) are hashable, so we key by the type object
# directly -- this also prevents GC-related id reuse issues.
_union_types_cache: Dict[Any, MappingProxyType[str, Type[Any]]] = {}
_union_injected: set[Any] = set()


def inject_types(type_: Any) -> None:
    if get_origin(type_) is not Union:
        return
    types_dict: Dict[str, Type[Any]] = {}
    for u_type in get_args(type_):
        fields = getattr(u_type, "__fields__", None)
        if fields is None:
            continue
        field = fields.get("type")
        if field is None:
            continue
        type_value = getattr(field, "default", None)
        if (type_value is None) or not isinstance(type_value, str):
            continue
        types_dict[type_value] = u_type
    if types_dict:
        _union_types_cache[type_] = MappingProxyType(types_dict)
    _union_injected.add(type_)


def get_union_types(type_: Any) -> Optional[MappingProxyType[str, Type[Any]]]:
    return _union_types_cache.get(type_)


def is_union_injected(type_: Any) -> bool:
    return type_ in _union_injected
