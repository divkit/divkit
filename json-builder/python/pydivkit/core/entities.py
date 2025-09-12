from __future__ import annotations

import enum
import hashlib
import json
import uuid
import warnings
from collections import defaultdict
from functools import reduce
from types import MappingProxyType
from typing import (
    Any, Dict, FrozenSet, List, Mapping, Optional, Sequence, Set, Type, Union,
    get_args, get_origin, get_type_hints, Iterator, Tuple,
)

from .compat import classproperty
from .fields import Expr, _Field
from .types.union import inject_types


TYPE_FIELD = "type"

# ExcludeFieldsType: str -> ExcludeFieldsType | bool
ExcludeFieldsType = Mapping[str, Any]
SchemaType = Dict[str, Any]


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


def _cast_value_type(value: Any, type_: Any) -> Any:  # noqa
    if isinstance(value, _Field):
        raise ValueError("Value cannot be of type _Field")

    if type_ is Any:
        return value

    origin_type = get_origin(type_)

    if isinstance(origin_type, type) and isinstance(origin_type, (str, bytes)):
        return type_(value)

    if isinstance(origin_type, type) and issubclass(origin_type, Sequence):
        if not isinstance(value, Sequence):
            raise ValueError(
                f"Value {value} has wrong type. Expected type is {type_}.",
            )
        element_type, *_ = get_args(type_)
        return [_cast_value_type(v, element_type) for v in value]

    if isinstance(origin_type, type) and issubclass(origin_type, Mapping):
        if not isinstance(value, Mapping):
            raise ValueError(
                f"Value {value} has wrong type. Expected type is {type_}.",
            )

        key_type, value_type = get_args(type_)
        return {
            _cast_value_type(k, key_type): _cast_value_type(v, value_type)
            for k, v in value.items()
        }

    if origin_type is Union:
        if not getattr(type_, "__injected_types__", False):
            inject_types(type_)
            setattr(type_, "__injected_types__", True)
        types = getattr(type_, "__types__", None)
        if types and isinstance(value, dict):
            type_value = value.get(TYPE_FIELD)
            if type_value:
                target_type = types.get(type_value)
                if target_type:
                    return target_type(**value)
                raise ValueError(
                    f"Union {type_} does not contain type {type_value}.",
                )
            raise ValueError(
                f"Value {value} does not have field {TYPE_FIELD}.",
            )
        for u_type in get_args(type_):
            try:
                return _cast_value_type(value, u_type)
            except ValueError:
                pass
        raise ValueError(
            f"Value {value} has wrong type. Expected type is {type_}.",
        )

    if isinstance(value, type_):
        return value

    if isinstance(value, BaseEntity):
        if isinstance(value, type_):
            return value
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


def dump(obj: Any) -> Any:
    if isinstance(obj, (str, bytes)):
        return obj
    if isinstance(obj, Sequence):
        return [dump(obj_item) for obj_item in obj]
    if isinstance(obj, Mapping):
        return {k: v for k, v in obj.items()}
    if isinstance(obj, Expr):
        return str(obj)
    if isinstance(obj, BaseEntity):
        return obj.dict()
    if isinstance(obj, enum.Enum):
        return obj.value
    return obj


def _update_related_templates(
    value: Any,
    related_templates: Set[Type[BaseDiv]],
) -> None:
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


def _add_field_extra_to_schema(field: _Field, schema: SchemaType) -> None:
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


class BaseEntity:
    __fields__: Mapping[str, _Field]
    __field_names__: Mapping[uuid.UUID, str]
    __field_types__: Mapping[str, type]

    __subclasses__: Dict[str, Type[BaseEntity]] = {}
    __related_templates__: FrozenSet[Type[BaseDiv]] = frozenset({})
    __template_name__: Optional[str] = None

    def __init__(self, **kwargs: Any) -> None:
        if self.__fields__ and not self.__field_types__:
            class_name = self.__class__.__name__
            raise TypeError(
                f"Class {class_name} fields are not prepared yet, you may "
                f"need to call {class_name}.update_forward_refs().",
            )
        self._instance_related_templates: Set[Type[BaseDiv]] = set()
        for field_name, value in kwargs.items():
            if value is not None:
                setattr(self, field_name, value)
        for field_name, field in self.__fields__.items():
            if field_name not in kwargs:
                setattr(self, field_name, field.default)

    @classproperty
    @classmethod
    def template_name(cls) -> str:
        if cls.__template_name__ is not None:
            return cls.__template_name__
        return f"{cls.__module__}.{cls.__name__}"

    def __init_subclass__(cls, *args: Any, **kwargs: Any) -> None:
        super().__init_subclass__()
        if cls.template_name in cls.__subclasses__:
            warnings.warn(
                f"Template {cls.template_name!r} already defined in "
                f"{cls.__subclasses__[cls.template_name]!r} "
                f"and will be replaced to {cls!r}", RuntimeWarning,
            )
        cls.__subclasses__[cls.template_name] = cls
        cls.__fields__ = cls._extract_fields()
        cls.__field_names__ = cls._extract_field_names(cls.__fields__)
        cls._remove_fields_from_cls_attrs()
        cls.update_forward_refs()
        if cls.__init__ != BaseEntity.__init__:
            setattr(cls, "__init__", BaseEntity.__init__)

    def __setattr__(self, key: str, value: Any) -> None:
        if not key.startswith("_"):
            field_type = self.__field_types__.get(key)
            if field_type is None:
                raise KeyError(
                    f"'{self.__class__.__name__}' object "
                    f"has no attribute '{key}'",
                )
            if key == TYPE_FIELD:
                self._validate_type_value(value)
            if not (isinstance(value, _Field) and value.ref_to):
                value = _cast_value_type(value, field_type)
            _update_related_templates(value, self._instance_related_templates)
        super().__setattr__(key, value)

    @classmethod
    def _extract_field_names(
        cls,
        fields: Mapping[str, _Field],
    ) -> Mapping[uuid.UUID, str]:
        return MappingProxyType(
            {field.uid: field_name for field_name, field in fields.items()},
        )

    @classmethod
    def _remove_fields_from_cls_attrs(cls) -> None:
        to_remove = []
        for attr_name, attr_value in cls.__dict__.items():
            if isinstance(attr_value, _Field):
                to_remove.append(attr_name)
        for attr_name in to_remove:
            delattr(cls, attr_name)

    def _validate_type_value(self, value: Any) -> None:
        field = self.__fields__[TYPE_FIELD]
        if field.default and field.default != value:
            raise ValueError(
                f"Property '{TYPE_FIELD}' has wrong value: '{value}'. "
                f"Expected is '{field.default}'.",
            )

    @classmethod
    def update_forward_refs(cls) -> None:
        try:
            cls.__field_types__ = cls._extract_field_types(cls.__fields__)
        except (NameError, AttributeError):
            cls.__field_types__ = MappingProxyType({})

    @classmethod
    def _extract_fields(cls) -> Mapping[str, _Field]:
        fields: Dict[str, _Field] = {}
        for base in cls.__bases__:
            base_fields = getattr(base, "__fields__", None)
            if issubclass(base, BaseEntity) and base_fields:
                fields.update(base_fields)
        for key, value in cls.__dict__.items():
            if isinstance(value, _Field):
                field_ref = value.ref_to
                if field_ref:
                    value = fields[key]
                    if value.is_ref:
                        raise ValueError("Ref cannot point to another ref")
                    if value.default is not None:
                        raise ValueError(
                            f"Cannot create a ref for field {key} because "
                            f"it has a default value",
                        )
                    value.ref_to = field_ref
                if not value.name:
                    value.name = key
                fields[key] = value
        return MappingProxyType(fields)

    @classmethod
    def _extract_field_types(
        cls,
        fields: Mapping[str, _Field],
    ) -> Mapping[str, type]:
        cls_hints = get_type_hints(cls, localns={cls.__name__: cls})
        cls._validate_field_types_of_bases(cls_hints)
        cls._validate_field_types(fields, cls_hints)

        field_types = {}
        for key in fields.keys():
            field_types[key] = cls_hints[key]
        for base in cls.__bases__:
            base_field_types = getattr(base, "__field_types__", None)
            if issubclass(base, BaseEntity) and base_field_types:
                field_types.update(base_field_types)

        for field_name, field_type in field_types.items():
            field = cls.__fields__[field_name]
            if field.ref_to:
                field_types[field_name] = Optional[field_type]

        return MappingProxyType(field_types)

    @classmethod
    def _validate_field_types(
        cls,
        fields: Mapping[str, _Field],
        cls_hints: Mapping[str, Any],
    ) -> None:
        for key, field in fields.items():
            hint = cls_hints.get(key)
            if not hint:
                raise ValueError(
                    f"Type hint is missed for {cls.__name__}.{key}",
                )
            if field.default is not None:
                _cast_value_type(field.default, hint)

    @classmethod
    def _validate_field_types_of_bases(
        cls,
        cls_hints: Mapping[str, Any],
    ) -> None:
        for base in cls.__bases__:
            base_hints = get_type_hints(base)
            for key, cls_type_hint in cls_hints.items():
                if not isinstance(getattr(base, key, None), _Field):
                    continue
                base_type_hint = base_hints.get(key)
                if base_type_hint and (cls_type_hint is not base_type_hint):
                    raise ValueError(
                        f"Type hint mismatch. _Field {cls.__name__}.{key} "
                        "should match type hint of a parent class "
                        f"{base.__name__}.{key}\n"
                        f"Expected: {base_type_hint}. "
                        f"Actual: {cls_type_hint}",
                    )

    @classmethod
    def _merge_ref_types(
        cls,
        *ref_types_seq: Mapping[uuid.UUID, Any],
    ) -> Mapping[uuid.UUID, Any]:
        all_refs: Dict[uuid.UUID, List[Any]] = defaultdict(list)
        for ref_types in ref_types_seq:
            for ref_uid, ref_type in ref_types.items():
                all_refs[ref_uid].append(ref_type)
        return MappingProxyType(
            {
                ref_uid: reduce(_merge_types, ref_types)
                for ref_uid, ref_types in all_refs.items()
            },
        )

    @classmethod
    def _extract_ref_types_from_obj(
        cls,
        obj: Any,
    ) -> Mapping[uuid.UUID, Any]:
        if isinstance(obj, BaseEntity):
            return obj._extract_all_ref_types()
        elif isinstance(obj, list):
            ref_types_seq = []
            for item in obj:
                if isinstance(item, BaseEntity):
                    ref_types_seq.append(item._extract_all_ref_types())
            return cls._merge_ref_types(*ref_types_seq)
        return MappingProxyType({})

    def _extract_ref_types_from_values(self) -> Mapping[uuid.UUID, Any]:
        ref_types: Dict[uuid.UUID, Any] = {}
        for field_name, field_type in self.__field_types__.items():
            field_value = getattr(self, field_name, None)
            if isinstance(field_value, _Field) and field_value.ref_to:
                field = self.__fields__[field_name]
                field_value.ref_to.apply_constraints(field.constraints)
                ref_uid = field_value.ref_to.uid
                if ref_uid in ref_types:
                    ref_types[ref_uid] = _merge_types(
                        ref_types[ref_uid],
                        field_type,
                    )
                else:
                    ref_types[ref_uid] = field_type
        return MappingProxyType(ref_types)

    def _extract_all_ref_types(self) -> Mapping[uuid.UUID, Any]:
        ref_types_seq = [self._extract_ref_types_from_values()]
        for field_name in self.__fields__:
            field_value = getattr(self, field_name, None)
            ref_types_seq.append(self._extract_ref_types_from_obj(field_value))
        return self._merge_ref_types(*ref_types_seq)

    def related_templates(self) -> Set[Type[BaseDiv]]:
        return {*self.__related_templates__, *self._instance_related_templates}

    def dict(self) -> Dict[str, Any]:
        result: Dict[str, Any] = {}
        for field_name, field in self.__fields__.items():
            field_value = getattr(self, field_name, field.default)
            if field_value is not None:
                if isinstance(field_value, _Field) and field_value.ref_to:
                    result[
                        f"${field.field_name}"
                    ] = field_value.ref_to.field_name
                else:
                    result[field.field_name] = dump(field_value)
        return result

    @classmethod
    def _can_add_field_to_schema(
        cls,
        field_name: str,
        field: _Field,
    ) -> bool:
        return not field.ref_to

    @classmethod
    def _build_schema(
        cls,
        definitions: Dict[str, SchemaType],
        exclude: ExcludeFieldsType,
    ) -> SchemaType:
        properties: Dict[str, Any] = {}
        required: List[str] = []
        for field_name, field in cls.__fields__.items():
            if (
                not cls._can_add_field_to_schema(field_name, field)
                or exclude.get(field_name) is True
            ):
                continue
            field_type = cls.__field_types__[field_name]
            properties[field_name] = _field_to_schema(
                field=field,
                type_=field_type,
                exclude=exclude.get(field_name, {}),
                definitions=definitions,
            )
            if field_type != Optional[field_type]:
                required.append(field_name)
        schema: SchemaType = {"type": "object"}
        if properties:
            schema["properties"] = properties
        if required:
            schema["required"] = required
        return schema

    @classmethod
    def schema_as_ref(
        cls,
        definitions: Dict[str, SchemaType],
        exclude: ExcludeFieldsType,
    ) -> SchemaType:
        schema_name = cls.__name__
        if exclude:
            serialized_exclude = json.dumps(exclude, sort_keys=True)
            exclude_hash = hashlib.md5(serialized_exclude.encode()).hexdigest()
            schema_name += f"_{exclude_hash}"
        if schema_name not in definitions:
            definitions[schema_name] = {}  # to stop infinity recursion
            schema = cls._build_schema(definitions, exclude)
            definitions[schema_name] = schema
        return {"$ref": f"#/definitions/{schema_name}"}


class BaseDiv(BaseEntity):
    __tpl_values__: Mapping[str, Any]
    __refs__: Mapping[str, Set[Optional[str]]]
    __local_referred_fields__: FrozenSet[str]

    __base_type__: Optional[str] = None
    __template__: Optional[Dict[str, Any]] = None

    def __init_subclass__(cls, *args: Any, **kwargs: Any) -> None:
        cls.__base_type__ = cls._get_base_type()
        cls.__local_referred_fields__ = cls._extract_local_referred_fields()
        cls.__template__ = None
        cls._inject_type_field()
        super().__init_subclass__(*args, **kwargs)

    @classmethod
    def _extract_local_referred_fields(cls) -> FrozenSet[str]:
        return frozenset(
            {
                field_name
                for field_name, field in cls.__dict__.items()
                if isinstance(field, _Field) and field.ref_to
            },
        )

    def __setattr__(self, key: str, value: Any) -> None:
        if (value is None) and (key in self.__tpl_values__):
            return
        super().__setattr__(key, value)

    @classmethod
    def update_forward_refs(cls) -> None:
        super().update_forward_refs()
        cls.__tpl_values__ = cls._extract_tpl_values(cls.__field_types__)
        cls.__field_types__ = cls._make_fields_optional_from_tpl_values(
            cls.__field_types__,
            cls.__tpl_values__,
        )
        cls.__related_templates__ = cls._extract_related_templates(
            cls.__tpl_values__,
        )
        cls._validate_ref_types()

    @classmethod
    def _make_fields_optional_from_tpl_values(
        cls,
        field_types: Mapping[str, type],
        tpl_values: Mapping[str, Any],
    ) -> Mapping[str, type]:
        new_field_types: Dict[str, Any] = {**field_types}
        for field_name in tpl_values:
            new_field_types[field_name] = Optional[new_field_types[field_name]]
        return MappingProxyType(new_field_types)

    @classmethod
    def _extract_related_templates(
        cls,
        tpl_values: Mapping[str, Any],
    ) -> FrozenSet[Type[BaseDiv]]:
        related_templates: Set[Type[BaseDiv]] = set()
        if cls.__base_type__:
            related_templates.add(cls)
            for base_cls in cls.__bases__:
                if issubclass(base_cls, BaseEntity):
                    related_templates.update(base_cls.__related_templates__)
        for tpl_value in tpl_values.values():
            _update_related_templates(tpl_value, related_templates)
        return frozenset(related_templates)

    @classmethod
    def _extract_tpl_values(
        cls,
        field_types: Mapping[str, Any],
    ) -> Mapping[str, Any]:
        tpl_values: Dict[str, Any] = {}
        for name, field_type in field_types.items():
            if not hasattr(cls, name):
                continue
            value = getattr(cls, name)
            if value is not None:
                tpl_values[name] = _cast_value_type(value, field_type)
            delattr(cls, name)
        return MappingProxyType(tpl_values)

    @classmethod
    def _extract_ref_types_from_tpl_values(cls) -> Mapping[uuid.UUID, Any]:
        return cls._merge_ref_types(
            *(
                cls._extract_ref_types_from_obj(tpl_value)
                for tpl_value in cls.__tpl_values__.values()
            ),
        )

    @classmethod
    def _extract_ref_types_from_fields(cls) -> Mapping[uuid.UUID, Any]:
        ref_types: Dict[uuid.UUID, Any] = {}
        for field_name, field in cls.__fields__.items():
            if not field.ref_to:
                continue
            field.ref_to.apply_constraints(field.constraints)
            ref_uid = field.ref_to.uid
            field_type = cls.__field_types__[field_name]
            if ref_uid in ref_types:
                ref_types[ref_uid] = _merge_types(
                    ref_types[ref_uid],
                    field_type,
                )
            else:
                ref_types[ref_uid] = field_type
        return MappingProxyType(ref_types)

    @staticmethod
    def _make_union(type: Type[Any]) -> Set[Type[Any]]:
        if get_origin(type) is Union:
            return set(get_args(type))
        return {type}

    @classmethod
    def _validate_subclass(
        cls,
        ref_type: Type[Any],
        expected_field_type: Type[Any],
    ) -> bool:
        def _check_origins() -> Iterator[Tuple[Tuple[Any, ...], Tuple[Any, ...]]]:
            for ref in cls._make_union(ref_type):
                for expect in cls._make_union(expected_field_type):
                    if get_origin(expect) == get_origin(ref):
                        yield get_args(ref), get_args(expect)
            return None

        for origins in _check_origins():
            if origins is None:
                return False

            expect, ref = origins

            if get_args(expect) == get_args(ref):
                return True

            if not any(
                issubclass(exp, get_args(ref))
                for exp in get_args(expect)
                if get_origin(exp) is not Union
            ):
                continue

            return True
        return False

    @classmethod
    def _validate_ref_types(cls) -> None:
        ref_types = cls._merge_ref_types(
            cls._extract_ref_types_from_fields(),
            cls._extract_ref_types_from_tpl_values(),
        )
        for ref_uid, ref_type in ref_types.items():
            field_name = cls.__field_names__[ref_uid]
            expected_field_type = cls.__field_types__[field_name]
            if (
                ref_type != expected_field_type
                and ref_type != Optional[expected_field_type]
                and ref_type != Union[expected_field_type, Expr]
                and ref_type != Union[expected_field_type, Expr, None]
                and not cls._validate_subclass(ref_type, expected_field_type)
            ):
                raise TypeError(
                    f"Type of attribute '{field_name}' does "
                    f"not match ref type {expected_field_type} != {ref_type}",
                )

    @classmethod
    def _get_base_type(cls) -> Optional[str]:
        if len(cls.__bases__) > 1:
            raise TypeError(
                "Types conflict: base class cannot be uniquely identified",
            )
        base_cls, *_ = cls.__bases__
        if not issubclass(base_cls, BaseDiv):
            raise TypeError(
                "Types conflict: class must be derived from the BaseDiv class",
            )
        type_field = base_cls.__fields__.get(TYPE_FIELD)
        if (base_cls is BaseDiv) or not (type_field and type_field.default):
            return None
        return type_field.default

    @classmethod
    def _inject_type_field(cls) -> None:
        if cls.__base_type__:
            type_value = cls.template_name
            setattr(cls, TYPE_FIELD, _Field(default=type_value))

    @classmethod
    def template(cls) -> Dict[str, Any]:
        if not cls.__base_type__:
            raise TypeError(f"Component {cls.__name__} is not a template")
        if cls.__template__ is None:
            cls.__template__ = cls._build_template()
        return cls.__template__

    @classmethod
    def _build_template(cls) -> Dict[str, Any]:
        template = {TYPE_FIELD: cls.__base_type__}
        for field_name, tpl_field_value in cls.__tpl_values__.items():
            field = cls.__fields__[field_name]
            template[field.field_name] = dump(tpl_field_value)
        for field_name, field in cls.__fields__.items():
            if field.ref_to and field_name in cls.__local_referred_fields__:
                template[f"${field.field_name}"] = field.ref_to.field_name
        return template

    @classmethod
    def _can_add_field_to_schema(
        cls,
        field_name: str,
        field: _Field,
    ) -> bool:
        return (
            super()._can_add_field_to_schema(field_name, field)
            and field_name not in cls.__tpl_values__
        )

    @classmethod
    def schema(
        cls,
        exclude_fields: Optional[Sequence[str]] = None,
    ) -> SchemaType:
        definitions: Dict[str, SchemaType] = {}
        exclude = _make_exclude_fields(exclude_fields)
        schema = cls._build_schema(definitions, exclude)
        schema["definitions"] = definitions
        return schema
