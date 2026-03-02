from __future__ import annotations

from collections.abc import Mapping, Sequence
from types import MappingProxyType
from typing import Any, get_args, get_origin
from weakref import WeakKeyDictionary
import uuid

from divkit_rs._native import PyDivEntity

from .core.compat import classproperty
from .core.fields import REF_MARKER_PREFIX, Expr, _Field


def _is_ref_marker(value: Any) -> bool:
    return isinstance(value, str) and value.startswith(REF_MARKER_PREFIX)


def _extract_ref_uid(value: str) -> str:
    return value[len(REF_MARKER_PREFIX) :]


def _replace_ref_markers(value: Any, uid_to_name: Mapping[str, str]) -> Any:
    if isinstance(value, list):
        return [_replace_ref_markers(v, uid_to_name) for v in value]
    if isinstance(value, dict):
        result: dict[str, Any] = {}
        for key, item in value.items():
            if _is_ref_marker(item):
                ref_uid = _extract_ref_uid(item)
                result[f"${key}"] = uid_to_name.get(ref_uid, ref_uid)
            else:
                result[key] = _replace_ref_markers(item, uid_to_name)
        return result
    return value


def _normalize_constructor_value(value: Any) -> Any:
    if isinstance(value, tuple):
        return [_normalize_constructor_value(v) for v in value]
    if isinstance(value, list):
        return [_normalize_constructor_value(v) for v in value]
    if isinstance(value, dict):
        return {k: _normalize_constructor_value(v) for k, v in value.items()}
    return value


def _iter_entity_classes(root: type[PyDivEntity]) -> list[type[PyDivEntity]]:
    out: list[type[PyDivEntity]] = []
    queue = [root]
    visited: set[type[PyDivEntity]] = set()
    while queue:
        cls = queue.pop()
        if cls in visited:
            continue
        visited.add(cls)
        out.append(cls)
        queue.extend(cls.__subclasses__())
    return out


def _install_constructor_compat() -> None:
    for cls in _iter_entity_classes(PyDivEntity):
        if cls is PyDivEntity:
            continue
        if getattr(cls, "__dk_constructor_compat__", False):
            continue
        original_new = cls.__new__

        def _wrapped_new(entity_cls, *args, __orig_new=original_new, **kwargs):
            normalized_kwargs = {
                key: _normalize_constructor_value(value)
                for key, value in kwargs.items()
            }
            instance = __orig_new(entity_cls, *args, **kwargs)
            if normalized_kwargs:
                _CONSTRUCTOR_VALUES[instance] = normalized_kwargs
            return instance

        cls.__new__ = staticmethod(_wrapped_new)
        cls.__dk_constructor_compat__ = True


def _dump(value: Any) -> Any:
    if isinstance(value, (str, bytes)):
        return value
    if isinstance(value, Expr):
        return str(value)
    if isinstance(value, Sequence):
        return [_dump(v) for v in value]
    if isinstance(value, Mapping):
        return {k: _dump(v) for k, v in value.items()}
    if isinstance(value, PyDivEntity):
        # pydivkit keeps separator delimiter style subclasses with class defaults
        # as empty objects in template declarations.
        cls = type(value)
        raw_dumped = _ORIG_DICT(value)
        if (
            cls.__name__ != "DivSeparatorDelimiterStyle"
            and any(base.__name__ == "DivSeparatorDelimiterStyle" for base in cls.__mro__[1:])
            and isinstance(raw_dumped, dict)
            and set(raw_dumped).issubset({"color"})
        ):
            return {}
        return value.dict()
    return value


def _collect_template_names_from_json(value: Any, out: set[str]) -> None:
    if isinstance(value, dict):
        type_name = value.get("type")
        if isinstance(type_name, str):
            out.add(type_name)
        for item in value.values():
            _collect_template_names_from_json(item, out)
        return
    if isinstance(value, list):
        for item in value:
            _collect_template_names_from_json(item, out)


def _collect_related_templates_from_value(value: Any, out: set[type[PyDivEntity]]) -> None:
    if isinstance(value, type) and issubclass(value, PyDivEntity):
        out.add(value)
        return
    if isinstance(value, PyDivEntity):
        out.update(value.related_templates())
        return
    if isinstance(value, Sequence) and not isinstance(value, (str, bytes)):
        for item in value:
            _collect_related_templates_from_value(item, out)
        return
    if isinstance(value, dict):
        type_name = value.get("type")
        if isinstance(type_name, str):
            template_cls = _TEMPLATE_REGISTRY.get(type_name)
            if template_cls is not None:
                out.add(template_cls)
        for item in value.values():
            _collect_related_templates_from_value(item, out)


def _annotation_to_schema(annotation: Any) -> dict[str, Any]:
    if annotation is Any:
        return {}

    origin = get_origin(annotation)
    args = get_args(annotation)

    if origin is not None:
        if origin in (list, tuple, set, frozenset, Sequence):
            item_schema = _annotation_to_schema(args[0]) if args else {}
            result: dict[str, Any] = {"type": "array"}
            if item_schema:
                result["items"] = item_schema
            return result

        if origin in (dict, Mapping):
            return {"type": "object"}

        # Optional[T] / T | None
        if type(None) in args:
            non_none_args = [arg for arg in args if arg is not type(None)]
            if len(non_none_args) == 1:
                base = _annotation_to_schema(non_none_args[0])
                if not base:
                    return {}
                base = dict(base)
                current_type = base.get("type")
                if isinstance(current_type, str):
                    base["type"] = [current_type, "null"]
                elif isinstance(current_type, list):
                    if "null" not in current_type:
                        base["type"] = [*current_type, "null"]
                else:
                    base["type"] = ["null"]
                return base
            return {}

    if annotation is str:
        return {"type": "string"}
    if annotation is bool:
        return {"type": "boolean"}
    if annotation is int:
        return {"type": "integer"}
    if annotation is float:
        return {"type": "number"}

    return {}


def _field_is_excluded(field_name: str, exclude_fields: list[str] | None) -> bool:
    if not exclude_fields:
        return False
    return any(
        exclude_path == field_name or exclude_path.startswith(f"{field_name}.")
        for exclude_path in exclude_fields
    )


def _apply_template_schema_fields(
    cls: type[PyDivEntity],
    schema: dict[str, Any],
    exclude_fields: list[str] | None,
) -> dict[str, Any]:
    declared_fields: Mapping[str, _Field] = getattr(cls, "__dk_fields__", {})
    if not declared_fields:
        return schema

    properties = dict(schema.get("properties", {}))
    required = list(schema.get("required", []))
    annotations = dict(getattr(cls, "__annotations__", {}))

    for field_name, field in declared_fields.items():
        if _field_is_excluded(field_name, exclude_fields):
            properties.pop(field_name, None)
            if field_name in required:
                required.remove(field_name)
            continue

        field_schema = _annotation_to_schema(annotations.get(field_name, Any))
        if field.description is not None:
            field_schema["description"] = field.description
        if field.default is not None:
            field_schema["default"] = _dump(field.default)
        if field.constraints:
            field_schema.update(field.constraints)

        properties[field_name] = field_schema
        if field.default is None and not field.is_ref:
            if field_name not in required:
                required.append(field_name)
        elif field_name in required:
            required.remove(field_name)

    schema["properties"] = properties
    if required:
        schema["required"] = required
    else:
        schema.pop("required", None)
    return schema


class _DualMethod:
    def __init__(self, fn):
        self._fn = fn

    def __get__(self, obj, owner):
        target = owner if obj is None else obj

        def _bound(*args, **kwargs):
            return self._fn(target, *args, **kwargs)

        return _bound


_ORIG_DICT = PyDivEntity.dict
_ORIG_INIT = PyDivEntity.__init__
_ORIG_SCHEMA = PyDivEntity.schema
_ORIG_RELATED_TEMPLATES = getattr(PyDivEntity, "related_templates", lambda self: [])
_ORIG_INIT_SUBCLASS = PyDivEntity.__init_subclass__

_TEMPLATE_REGISTRY: dict[str, type[PyDivEntity]] = {}
_CONSTRUCTOR_VALUES: "WeakKeyDictionary[PyDivEntity, dict[str, Any]]" = WeakKeyDictionary()
_CORNERS_RADIUS_KEY_ALIASES = {
    "top_left": "top-left",
    "top_right": "top-right",
    "bottom_left": "bottom-left",
    "bottom_right": "bottom-right",
    "$top_left": "$top-left",
    "$top_right": "$top-right",
    "$bottom_left": "$bottom-left",
    "$bottom_right": "$bottom-right",
}
_PYDIVKIT_FLOAT_KEYS = {"alpha", "ratio", "weight", "letter_spacing"}


def _template_name_for_class(cls: type[PyDivEntity]) -> str:
    template_name = getattr(cls, "__template_name__", None)
    if template_name is not None:
        return template_name
    return f"{cls.__module__}.{cls.__name__}"


def _legacy_field_uid(cls: type[PyDivEntity], field_name: str) -> uuid.UUID:
    return uuid.uuid5(
        uuid.NAMESPACE_URL,
        f"{cls.__module__}.{cls.__qualname__}:{field_name}",
    )


def _legacy_field_names_for_class(cls: type[PyDivEntity]) -> Mapping[uuid.UUID, str]:
    names: dict[uuid.UUID, str] = {}

    for field_name in getattr(cls, "_field_names", []) or []:
        names[_legacy_field_uid(cls, field_name)] = field_name

    for field_name, field in getattr(cls, "__dk_fields__", {}).items():
        uid = getattr(field, "uid", None)
        if not isinstance(uid, uuid.UUID):
            uid = _legacy_field_uid(cls, field_name)
        names[uid] = field_name

    return MappingProxyType(names)


def _install_field_names_compat() -> None:
    for cls in _iter_entity_classes(PyDivEntity):
        cls.__field_names__ = _legacy_field_names_for_class(cls)


def _collect_parent_template_classes(
    cls: type[PyDivEntity],
    out: set[type[PyDivEntity]],
) -> None:
    current_base_type = getattr(cls, "__dk_base_type__", None)
    visited: set[str] = set()
    while isinstance(current_base_type, str) and current_base_type not in visited:
        visited.add(current_base_type)
        base_template_cls = _TEMPLATE_REGISTRY.get(current_base_type)
        if base_template_cls is None:
            break
        out.add(base_template_cls)
        current_base_type = getattr(base_template_cls, "__dk_base_type__", None)


def _compat_init_subclass(cls: type[PyDivEntity], **kwargs: Any) -> None:
    try:
        _ORIG_INIT_SUBCLASS(**kwargs)
    except TypeError:
        try:
            _ORIG_INIT_SUBCLASS(cls, **kwargs)
        except Exception:
            pass

    annotations = dict(getattr(cls, "__annotations__", {}))
    declared_fields: dict[str, _Field] = {}
    for name in annotations:
        value = cls.__dict__.get(name)
        if isinstance(value, _Field):
            if value.name is None:
                value.name = name
            declared_fields[name] = value
            delattr(cls, name)

    cls.__dk_fields__ = MappingProxyType(declared_fields)
    cls.__field_names__ = _legacy_field_names_for_class(cls)
    cls.__dk_tpl_values__ = MappingProxyType({})
    cls.__dk_template__ = None
    cls.__dk_is_template__ = False
    cls.__dk_base_type__ = getattr(cls, "_type_name", None)

    if not cls.__dk_base_type__:
        return

    native_field_names = set(getattr(cls, "_field_names", []))
    inherited_dk_field_names: set[str] = set()
    for base in cls.__mro__[1:]:
        inherited_dk_field_names.update(getattr(base, "__dk_fields__", {}).keys())

    tpl_values: dict[str, Any] = {}
    for name, value in list(cls.__dict__.items()):
        if name in native_field_names or name in inherited_dk_field_names:
            tpl_values[name] = value
            delattr(cls, name)

    cls.__dk_tpl_values__ = MappingProxyType(tpl_values)
    cls.__dk_is_template__ = True
    template_name = _template_name_for_class(cls)
    _TEMPLATE_REGISTRY[template_name] = cls

    # Keep template type in nested native serialization.
    # The original base type is preserved in __dk_base_type__ and used by template().
    cls._type_name = template_name


def _compat_entity_init(self: PyDivEntity, **kwargs: Any) -> None:
    # Generated native entities define their own __init__, so this initializer
    # primarily serves lightweight user subclasses of BaseDiv/BaseEntity.
    try:
        _ORIG_INIT(self)
    except TypeError:
        pass

    for field_name, value in kwargs.items():
        if value is not None:
            setattr(self, field_name, value)

    for field_name, field in getattr(type(self), "__dk_fields__", {}).items():
        if field_name not in kwargs:
            setattr(self, field_name, field.default)


def _compat_related_templates(self: PyDivEntity) -> set[type[PyDivEntity]]:
    cls = type(self)
    related: set[type[PyDivEntity]] = set()

    if getattr(cls, "__dk_is_template__", False):
        related.add(cls)
        _collect_parent_template_classes(cls, related)
        for value in getattr(cls, "__dk_tpl_values__", {}).values():
            _collect_related_templates_from_value(value, related)

    native_related = _ORIG_RELATED_TEMPLATES(self)
    if native_related:
        related.update(native_related)

    data = _compat_dict(self)
    template_names: set[str] = set()
    _collect_template_names_from_json(data, template_names)
    for template_name in template_names:
        template_cls = _TEMPLATE_REGISTRY.get(template_name)
        if template_cls is not None:
            related.add(template_cls)
            _collect_parent_template_classes(template_cls, related)

    return related


def _compat_dict(self: PyDivEntity) -> dict[str, Any]:
    result = _ORIG_DICT(self)
    cls = type(self)
    constructor_values = _CONSTRUCTOR_VALUES.get(self, {})
    for field_name in getattr(cls, "_field_names", []):
        if field_name not in result:
            continue
        current = result[field_name]
        try:
            raw_value = getattr(self, field_name)
        except Exception:
            continue
        constructor_value = constructor_values.get(field_name)
        if (
            isinstance(raw_value, (str, bytes))
            and isinstance(constructor_value, (Sequence, Mapping, PyDivEntity))
            and not isinstance(constructor_value, (str, bytes))
        ):
            # Rust serializer stringifies tuple-like inputs in some fields.
            # If runtime value is a string repr, reuse normalized constructor input.
            raw_value = constructor_value
        if isinstance(raw_value, (str, bytes)):
            continue
        if isinstance(raw_value, (Sequence, Mapping, PyDivEntity)):
            dumped_raw = _dump(raw_value)
            if dumped_raw != current:
                result[field_name] = dumped_raw
    if getattr(cls, "__dk_is_template__", False):
        result["type"] = _template_name_for_class(cls)
    return result


def _compat_schema(target: Any, exclude_fields: list[str] | None = None) -> dict[str, Any]:
    if isinstance(target, type):
        cls = target
        kwargs = {name: None for name in getattr(cls, "_required_fields", [])}
        instance = cls(**kwargs)
        schema = _ORIG_SCHEMA(instance, exclude_fields)
        return _apply_template_schema_fields(cls, schema, exclude_fields)
    schema = _ORIG_SCHEMA(target, exclude_fields)
    return _apply_template_schema_fields(type(target), schema, exclude_fields)


def _compat_template(cls: type[PyDivEntity]) -> dict[str, Any]:
    if not getattr(cls, "__dk_is_template__", False):
        raise TypeError(f"Component {cls.__name__} is not a template")
    cached = getattr(cls, "__dk_template__", None)
    if cached is not None:
        return cached

    template: dict[str, Any] = {"type": getattr(cls, "__dk_base_type__", None)}
    for field_name, field_value in getattr(cls, "__dk_tpl_values__", {}).items():
        dumped = _dump(field_value)
        if dumped is not None:
            template[field_name] = dumped

    uid_to_name = {
        str(field.uid): field.field_name
        for field in getattr(cls, "__dk_fields__", {}).values()
        if field.name is not None
    }
    template = _replace_ref_markers(template, uid_to_name)
    cls.__dk_template__ = template
    return template


class _CompatDivData:
    def __init__(self, log_id: str, divs: tuple[PyDivEntity, ...]) -> None:
        self.log_id = log_id
        self.divs = divs

    def dict(self) -> dict[str, Any]:
        return {
            "log_id": self.log_id,
            "states": [
                {"state_id": index, "div": div.dict()}
                for index, div in enumerate(self.divs)
            ],
        }

    def build(self) -> dict[str, Any]:
        return self.dict()


def _compat_make_card(log_id: str, *divs: PyDivEntity) -> _CompatDivData:
    return _CompatDivData(log_id=log_id, divs=divs)


def _compat_make_div(div: PyDivEntity) -> dict[str, Any]:
    templates = set(div.related_templates())
    queue = list(templates)
    while queue:
        template_cls = queue.pop()
        template_body = template_cls.template()
        template_names: set[str] = set()
        _collect_template_names_from_json(template_body, template_names)
        for template_name in template_names:
            nested_template_cls = _TEMPLATE_REGISTRY.get(template_name)
            if nested_template_cls is None:
                continue
            if nested_template_cls not in templates:
                templates.add(nested_template_cls)
                queue.append(nested_template_cls)
            parent_templates: set[type[PyDivEntity]] = set()
            _collect_parent_template_classes(nested_template_cls, parent_templates)
            for parent_template_cls in parent_templates:
                if parent_template_cls not in templates:
                    templates.add(parent_template_cls)
                    queue.append(parent_template_cls)

    result = {
        "templates": {
            template.template_name: template.template()
            for template in templates
        },
        "card": _compat_make_card("card", div).dict(),
    }
    return _normalize_pydivkit_json(result)


def _normalize_pydivkit_json(value: Any, parent_key: str | None = None) -> Any:
    if isinstance(value, list):
        return [_normalize_pydivkit_json(item, parent_key=parent_key) for item in value]
    if isinstance(value, dict):
        result: dict[str, Any] = {}
        for key, item in value.items():
            normalized_key = _CORNERS_RADIUS_KEY_ALIASES.get(key, key)
            normalized_value = _normalize_pydivkit_json(item, parent_key=normalized_key)
            if (
                normalized_key in _PYDIVKIT_FLOAT_KEYS
                and isinstance(normalized_value, int)
                and not isinstance(normalized_value, bool)
            ):
                normalized_value = float(normalized_value)
            if (
                normalized_key == "value"
                and parent_key in {"x", "y"}
                and isinstance(normalized_value, int)
                and not isinstance(normalized_value, bool)
            ):
                normalized_value = float(normalized_value)
            if (
                normalized_key == "width"
                and parent_key == "stroke"
                and isinstance(normalized_value, int)
                and not isinstance(normalized_value, bool)
            ):
                normalized_value = float(normalized_value)
            if normalized_key == "color" and isinstance(normalized_value, int):
                normalized_value = str(normalized_value)
            result[normalized_key] = normalized_value
        return result
    return value


def install_pydivkit_compat() -> None:
    _install_constructor_compat()
    _install_field_names_compat()
    PyDivEntity.__init__ = _compat_entity_init
    PyDivEntity.__init_subclass__ = classmethod(_compat_init_subclass)
    PyDivEntity.dict = _compat_dict
    PyDivEntity.related_templates = _compat_related_templates
    PyDivEntity.schema = _DualMethod(_compat_schema)
    PyDivEntity.update_forward_refs = classmethod(lambda cls: None)
    PyDivEntity.template_name = classproperty(lambda cls: _template_name_for_class(cls))
    PyDivEntity.template = classmethod(_compat_template)


BaseEntity = PyDivEntity
BaseDiv = PyDivEntity
make_div = _compat_make_div
make_card = _compat_make_card
