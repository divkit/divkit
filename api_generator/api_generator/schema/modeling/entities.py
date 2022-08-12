from __future__ import annotations

from abc import ABC, abstractmethod
from typing import List, Optional, Dict, Union, Tuple, cast
from dataclasses import dataclass
from enum import Enum, auto
import validators
import re

from ..utils import is_dict_with_keys_of_type, is_list_of_type
from .utils import (
    should_generate_swift_serialization,
    alias,
    fixing_reserved_typename
)

from ...config import Config, GeneratedLanguage, GenerationMode, Platform
from ..preprocessing.entities import ElementLocation
from ..preprocessing.errors import UnresolvedReferenceError
from .errors import GenericError, InvalidFieldRepresentationError

from . import builders


class Declarable(ABC):
    def __init__(self) -> None:
        self._parent: Optional[Declarable] = None

    @property
    def parent(self) -> Optional[Entity]:
        return self._parent

    @parent.setter
    def parent(self, parent: Entity) -> None:
        self._parent = parent

    @property
    @abstractmethod
    def name(self) -> str:
        pass

    @property
    @abstractmethod
    def resolved_name(self) -> str:
        pass

    @property
    @abstractmethod
    def original_name(self) -> str:
        pass

    @property
    @abstractmethod
    def include_in_documentation_toc(self) -> bool:
        pass

    @property
    @abstractmethod
    def as_json(self) -> Dict:
        pass

    @abstractmethod
    def resolve_dependencies(self, global_objects: List[Declarable]) -> None:
        pass

    @abstractmethod
    def check_dependencies_resolved(self, location: ElementLocation, stack: List[Declarable]) -> None:
        pass


def _super_entities(config: Config.GenerationConfig, dictionary: Dict[str, any]) -> Optional[str]:
    if config.lang in [GeneratedLanguage.SWIFT, GeneratedLanguage.DOCUMENTATION]:
        super_entities_key = 'swift_protocols'
    elif config.lang in [GeneratedLanguage.KOTLIN, GeneratedLanguage.KOTLIN_DSL]:
        super_entities_key = 'kotlin_interfaces'
    elif config.lang is GeneratedLanguage.TYPES_SCRIPT:
        super_entities_key = 'typescript_interfaces'
    elif config.lang is GeneratedLanguage.PYTHON:
        super_entities_key = 'python_classes'
    else:
        raise NotImplementedError

    super_entities: Optional[str] = dictionary.get(super_entities_key, dictionary.get(f'{super_entities_key}_local'))
    return super_entities


class Entity(Declarable):
    _implemented_protocol: Optional[Entity]
    _enclosing_enumeration: Optional[EntityEnumeration] = None

    def __init__(self,
                 name: str,
                 dictionary: Dict[str, any],
                 location: ElementLocation,
                 mode: GenerationMode,
                 config: Config.GenerationConfig):
        super().__init__()
        self._generate_serialization_flag: bool = should_generate_swift_serialization(config, dictionary)
        self._super_entities: Optional[str] = _super_entities(config, dictionary)
        self._resolved_name: str = alias(config.lang, dictionary) or fixing_reserved_typename(name, config.lang)
        self._name: str = self._resolved_name + mode.type.name_suffix()
        self._root_entity: bool = dictionary.get('root_entity', False)
        self._display_name: str = dictionary.get('display_name', name)
        self._description: str = dictionary.get('description', '')
        self._swift_super_protocol: Optional[str] = dictionary.get('swift_super_protocol')
        self._include_in_documentation_toc: bool = dictionary.get('include_in_documentation_toc', False)
        self._original_name: str = name
        self._generation_mode: GenerationMode = mode
        self._errors_collector_enabled: bool = not mode.is_template and name in config.errors_collectors

        input_properties: Dict[str, any] = dictionary.get('properties', dict())
        if not is_dict_with_keys_of_type(value=input_properties, key_type=str):
            raise GenericError(location=location + 'properties', text='Must have format [String : Any]')

        input_properties_with_dict_values: Dict[str, Dict[str, any]] = input_properties
        if not all(is_dict_with_keys_of_type(value, str) for value in input_properties_with_dict_values.values()):
            raise GenericError(location=location + 'properties', text='Must have format [String : [String : Any]]')
        properties, inner_types = builders.property_build(properties=input_properties_with_dict_values,
                                                          required=dictionary.get('required'),
                                                          location=location,
                                                          mode=mode,
                                                          config=config)

        self._properties: List[Property] = sorted(properties, key=lambda p: p.name)

        definitions: Optional[Dict[str, Dict[str, any]]] = dictionary.get('definitions')
        inner_types_from_definitions: List[Declarable] = []
        if definitions is not None:
            for name, d in definitions.items():
                _, defs = builders.type_property_build(dictionary=d,
                                                       outer_name=name,
                                                       location=location,
                                                       mode=mode,
                                                       config=config)
                inner_types_from_definitions.extend(defs)
        prot_names: Optional[Union[List[str], str]] = dictionary.get('protocol_name')
        if prot_names is not None:
            if not isinstance(prot_names, List):
                prot_names = [prot_names]
            if is_list_of_type(prot_names, str):
                self._protocol_names: List[str] = list(map(lambda n: n + mode.type.name_suffix(), prot_names))
            else:
                self._protocol_names: List[str] = []
        else:
            self._protocol_names: List[str] = []

        self._is_deprecated: bool = dictionary.get('deprecated', False)
        self._type_script_templatable: bool = dictionary.get('typescript_templatable', True)
        self._generate_swift_optional_args: bool = dictionary.get('generate_swift_optional_arguments', True)
        self._lang: GeneratedLanguage = config.lang
        self._inner_types: List[Declarable] = inner_types_from_definitions + inner_types
        for inner_type in self._inner_types:
            inner_type.parent = self
        self.__resolve_property_objects()

    def __str__(self):
        joined = '\n'.join(map(lambda x: f'\t{self._name} property: {x}', self._properties))
        return f'Entity "{self._name}":\n{joined}'

    def __repr__(self):
        return str(self)

    def __resolve_property_objects(self):
        for ind in range(len(self._properties)):
            self._properties[ind].property_type = self.__resolve_declaration(self._properties[ind].property_type)

    def __resolve_declaration(self, property_type: PropertyType) -> PropertyType:
        if isinstance(property_type, Object) and property_type.object is None:
            result: Optional[Declarable] = self.find_declaration(property_type.name)
            if result is not None:
                return Object(name=property_type.name, object=result, format=property_type.format)
            return self.parent.__resolve_declaration(property_type) if self.parent is not None else property_type
        elif isinstance(property_type, Array):
            return Array(property_type=self.__resolve_declaration(property_type.property_type),
                         min_items=property_type.min_items,
                         strict_parsing=property_type.strict_parsing)
        return property_type

    @property
    def name(self) -> str:
        return self._name

    @property
    def resolved_name(self) -> str:
        return self._resolved_name

    @property
    def parent(self) -> Optional[Entity]:
        return self._parent

    @parent.setter
    def parent(self, parent: Entity) -> None:
        self._parent = parent
        self.__resolve_property_objects()
        for inner_type in self._inner_types:
            inner_type.parent = self

    @property
    def original_name(self) -> str:
        return self._original_name

    @property
    def include_in_documentation_toc(self) -> bool:
        return self._include_in_documentation_toc

    @property
    def generate_as_protocol(self) -> bool:
        return self._name in self._protocol_names

    @property
    def implemented_protocol(self) -> Optional[Entity]:
        return self._implemented_protocol

    @property
    def enclosing_enumeration(self) -> Optional[EntityEnumeration]:
        return self._enclosing_enumeration

    def __resolve_declaration_with_objects(self,
                                           property_type: PropertyType,
                                           global_objects: List[Declarable]) -> PropertyType:
        if isinstance(property_type, Object) and property_type.object is None:
            result = next((d for d in global_objects if property_type.name in [d.name, d.original_name]), None)
            if result is not None:
                if isinstance(result, StringEnumeration):
                    return Object(name=property_type.name, object=result, format=property_type.format)
                else:
                    actual_name = property_type.name + self._generation_mode.type.name_suffix()
                    valid_obj = next(
                        d for d in global_objects if d.name == actual_name or d.original_name == property_type.name)
                    return Object(name=actual_name, object=valid_obj, format=property_type.format)
            return property_type
        elif isinstance(property_type, Array):
            return Array(
                property_type=self.__resolve_declaration_with_objects(property_type=property_type.property_type,
                                                                      global_objects=global_objects),
                min_items=property_type.min_items,
                strict_parsing=property_type.strict_parsing)
        return property_type

    def resolve_dependencies(self, global_objects: List[Declarable]) -> None:
        for i in range(len(self._properties)):
            self._properties[i].property_type = self.__resolve_declaration_with_objects(
                property_type=self._properties[i].property_type,
                global_objects=global_objects)
        for inner_type in self._inner_types:
            inner_type.resolve_dependencies(global_objects)

        self._implemented_protocol = next((cast(Entity, d) for d in global_objects if d.name in self._protocol_names),
                                          None)

        if self._lang is GeneratedLanguage.KOTLIN_DSL:
            new_enumeration = None
            for enumeration in global_objects:
                if isinstance(enumeration, EntityEnumeration):
                    valid_names = [self._name, self._resolved_name, self._original_name]
                    new_enumeration = next((ent for ent in enumeration.entities if ent[0] in valid_names), None)
                if new_enumeration is not None:
                    break
            self._enclosing_enumeration = new_enumeration

    def check_dependencies_resolved(self, location: ElementLocation, stack: List[Declarable]) -> None:
        if self in stack:
            return
        for p in self._properties:
            name: Optional[str] = None
            declarable: Optional[Declarable] = None
            if isinstance(p.property_type, Object):
                name = p.property_type.name
                declarable = p.property_type.object
            elif isinstance(p.property_type, Array) and isinstance(p.property_type.property_type, Object):
                name = p.property_type.property_type.name
                declarable = p.property_type.property_type.object
            if name is not None:
                if declarable is None:
                    raise UnresolvedReferenceError(location=location,
                                                   object_name=self._name,
                                                   field_name=p.dict_field,
                                                   unresolved_typename=name)
                if declarable is self:
                    return

                declarable.check_dependencies_resolved(location=location,
                                                       stack=stack + [self])

    @property
    def as_json(self) -> Dict:
        return {
            'type': 'entity',
            'name': self._name,
            'properties': list(map(lambda p: p.as_json, self._properties))
        }

    def find_declaration(self, name: str) -> Optional[Declarable]:
        if name in [self._name, self._original_name]:
            return self
        result = next(
            (d for d in self._inner_types if name in [d.name, d.resolved_name, d.original_name]),
            None)
        if result is not None:
            return result
        return self.parent.find_declaration(name) if self.parent is not None else None


class EntityEnumeration(Declarable):
    def __init__(self,
                 name: str,
                 original_name: str,
                 include_in_documentation_toc: bool,
                 root_entity: bool,
                 generate_serialization: bool,
                 generate_case_for_templates: bool,
                 entities: List[str],
                 mode: GenerationMode) -> None:
        super().__init__()
        self._resolved_name: str = name
        self._name: str = self._resolved_name + mode.type.name_suffix()
        self._original_name: str = original_name
        self._include_in_documentation_toc: bool = include_in_documentation_toc
        self._root_entity: bool = root_entity
        self._generate_serialization_flag: bool = generate_serialization
        self._generate_case_for_templates: bool = generate_case_for_templates
        self._resolved_entity_names: List[str] = entities
        self._entities: List[Tuple[str, Optional[Declarable]]] = list(map(
            lambda entity: (entity + mode.type.name_suffix(), None),
            entities
        ))
        self._mode = mode

    @property
    def name(self) -> str:
        return self._name

    @property
    def resolved_name(self) -> str:
        return self._resolved_name

    @property
    def original_name(self) -> str:
        return self._original_name

    @property
    def include_in_documentation_toc(self) -> bool:
        return self._include_in_documentation_toc

    @property
    def parent(self) -> Optional[Entity]:
        return self._parent

    @parent.setter
    def parent(self, parent: Entity) -> None:
        self._parent = parent
        new_entities: List[Tuple[str, Optional[Declarable]]] = []
        for name, obj in self._entities:
            new_obj = obj
            if new_obj is None and self.parent is not None:
                new_obj = self.parent.find_declaration(name)
            new_name = name
            if new_obj is not None:
                new_name = new_obj.name
            new_entities.append((new_name, new_obj))
        self._entities = new_entities

    @property
    def entities(self) -> List[Tuple[str, Optional[Declarable]]]:
        return self._entities

    def resolve_dependencies(self, global_objects: List[Declarable]) -> None:
        new_entities: List[Tuple[str, Optional[Declarable]]] = []
        for name, obj in self._entities:
            new_obj = obj or next((d for d in global_objects if name in [d.name, d.resolved_name, d.original_name]),
                                  None)
            new_name = name
            if new_obj is not None:
                new_name = new_obj.name
            new_entities.append((new_name, new_obj))
        self._entities = new_entities

    def check_dependencies_resolved(self, location: ElementLocation, stack: List[Declarable]) -> None:
        if self in stack:
            return
        for _, entity in self._entities:
            entity.check_dependencies_resolved(location=location + self._name,
                                               stack=stack + [self])

    @property
    def as_json(self) -> Dict:
        return {
            'type': 'entity_enumeration',
            'resolved_name': self._resolved_name,
            'entities': list(map(lambda x: {
                'name': x[0],
                'declarable': x[1].as_json if x[1] is not None else 'None'
            }, self._entities))
        }


class StringEnumeration(Declarable):
    def __init__(self,
                 name: str,
                 original_name: str,
                 cases: Union[List[str], List[Tuple[str, str]]],
                 include_documentation_toc: bool) -> None:
        super().__init__()
        self._name: str = name
        self._original_name: str = original_name
        if is_list_of_type(cases, str):
            self._cases: List[Tuple[str, str]] = list(map(lambda case: (case, case), cases))
        else:
            self._cases: List[Tuple[str, str]] = cases
        self._include_documentation_toc: bool = include_documentation_toc

    @property
    def name(self) -> str:
        return self._name

    @property
    def original_name(self) -> str:
        return self._original_name

    @property
    def resolved_name(self) -> str:
        return self._name

    @property
    def cases(self) -> List[Tuple[str, str]]:
        return self._cases

    @property
    def include_in_documentation_toc(self) -> bool:
        return self._include_documentation_toc

    def resolve_dependencies(self, global_objects: List[Declarable]) -> None:
        pass

    def check_dependencies_resolved(self, location: ElementLocation, stack: List[Declarable]) -> None:
        pass

    @property
    def as_json(self) -> Dict:
        return {
            'type': 'string_enumeration',
            'name': self._name,
            'cases': list(map(lambda case: case[1], self._cases))
        }


def default_value(lang: GeneratedLanguage,
                  property_type: PropertyType,
                  location: ElementLocation,
                  dictionary: Dict[str, any]) -> Optional[str]:
    generic_key = 'default_value'
    specific_prefix = ''
    if lang is GeneratedLanguage.KOTLIN:
        specific_prefix = 'android_'
    elif lang is GeneratedLanguage.KOTLIN_DSL:
        specific_prefix = 'kotlin_'
    elif lang in [GeneratedLanguage.SWIFT, GeneratedLanguage.DOCUMENTATION]:
        specific_prefix = 'ios_'
    elif lang is GeneratedLanguage.TYPES_SCRIPT:
        specific_prefix = 'typescript_'
    elif lang is GeneratedLanguage.PYTHON:
        specific_prefix = 'python_'
    specific_key = specific_prefix + generic_key
    result: Optional[str] = dictionary.get(specific_key, dictionary.get(generic_key))
    if isinstance(result, str):
        property_type.validate(value=result, location=location + specific_key)
        return result
    return None


class PropertyType(ABC):
    def support_expressions(self) -> bool:
        if isinstance(self, (Int, Double, Bool, BoolInt, String, Color, Url)):
            return True
        elif isinstance(self, (StaticString, StaticNative, Dictionary)):
            return False
        elif isinstance(self, Array):
            return self.property_type.support_expressions()
        elif isinstance(self, Object):
            return isinstance(self.object, StringEnumeration)

    def validate(self, value: str, location: ElementLocation) -> None:
        error = InvalidFieldRepresentationError(location, value)
        if isinstance(self, Url):
            if not validators.url(value):
                raise error
        elif isinstance(self, Color):
            color_matcher = re.compile(r'^#([0-9a-fA-F]{2}){3,4}$')
            if not color_matcher.fullmatch(value):
                raise error

    @property
    def as_json(self) -> Dict:
        if isinstance(self, (Int, Double, Bool, BoolInt, String, StaticString, StaticNative, Color, Url, Dictionary)):
            return {
                'value': str(type(self).__name__)
            }
        elif isinstance(self, Array):
            return {
                'type': 'array',
                'item_type': self.property_type.as_json
            }
        elif isinstance(self, Object):
            return {
                'type': 'object',
                'name': self.name,
                'obj': self.object.as_json if self.object is not None else 'None'
            }


@dataclass
class Int(PropertyType):
    constraint: Optional[str]


@dataclass
class Double(PropertyType):
    constraint: Optional[str]


@dataclass
class Bool(PropertyType):
    pass


@dataclass
class BoolInt(PropertyType):
    pass


@dataclass
class String(PropertyType):
    min_length: int
    formatted: bool
    regex: Optional[re.Pattern]
    enable_optimization: bool


@dataclass
class StaticString(PropertyType):
    value: str


@dataclass
class StaticNative(PropertyType):
    type: str
    native_value: str
    raw_value: str


@dataclass
class Color(PropertyType):
    pass


@dataclass
class Url(PropertyType):
    schemes: Optional[List[str]]


@dataclass
class Dictionary(PropertyType):
    pass


@dataclass
class Array(PropertyType):
    property_type: PropertyType
    min_items: int
    strict_parsing: bool


class ObjectFormat(Enum):
    DEFAULT = auto()
    JSON_STRING = auto()


@dataclass
class Object(PropertyType):
    name: str
    object: Optional[Declarable]
    format: ObjectFormat


@dataclass
class Property:
    name: str
    description: Optional[str]
    dict_field: str
    property_type: PropertyType
    optional: bool
    is_deprecated: bool
    mode: GenerationMode.Type
    default_value: Optional[str]
    platforms: Optional[List[Platform]]

    def __str__(self) -> str:
        return f'Property(name={self.name}, type={self.property_type})'

    def __repr__(self) -> str:
        return str(self)

    @property
    def as_json(self) -> Dict:
        return {
            'type': 'property',
            'name': self.name,
            'property_type': self.property_type.as_json
        }
