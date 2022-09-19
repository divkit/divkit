from types import MappingProxyType
from typing import Any, Dict, Type, Union, get_args, get_origin


def inject_types(type_: Any) -> None:
    if get_origin(type_) is not Union:
        return
    types_dict: Dict[str, Type[Any]] = {}
    for u_type in get_args(type_):  # TODO
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
        setattr(type_, "__types__", MappingProxyType(types_dict))
