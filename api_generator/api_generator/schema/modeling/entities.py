from __future__ import annotations

from abc import ABC, abstractmethod
from copy import deepcopy
from typing import List, Optional, Dict, Union, Tuple, cast, Any, Set
from dataclasses import dataclass
from enum import Enum, auto
import re

from ..utils import is_dict_with_keys_of_type, is_list_of_type
from .utils import (
    alias,
    alias_for,

    fixing_reserved_typename
)
from ...utils import capitalize_camel_case

from ...config import Config, GeneratedLanguage, GenerationMode, Platform, TEMPLATE_SUFFIX
from ..preprocessing.entities import ElementLocation
from ..preprocessing.errors import UnresolvedReferenceError
from .errors import GenericError, InvalidFieldRepresentationError

from . import builders


class DescriptionLanguage(str, Enum):
    EN = 'en'
    RU = 'ru'


def _build_generator_properties(
        dictionary: Dict[str, Any],
        location: ElementLocation,
        config: Config.GenerationConfig,
        mode: GenerationMode,
        properties_list: List[Property],
) -> Optional[GeneratorProperties]:
    generator_properties: Dict[str, Any] = dictionary.get("codegen", None)
    generator_properties_location = location + "codegen"
    if generator_properties is None:
        return None

    if not isinstance(generator_properties, Dict):
        raise GenericError(
            location=generator_properties_location,
            text='Must have format {String : Any}'
        )

    lang: GeneratedLanguage = config.lang
    specific_properties: Dict[str, Any] = generator_properties.get(lang.value, None)
    if specific_properties is None:
        return GeneralGeneratorProperties(
            general_properties=generator_properties,
            lang=lang,
            mode=mode
        )
    if not isinstance(specific_properties, Dict):
        raise GenericError(
            location=generator_properties_location + lang.value,
            text='Must have format {String : Any}'
        )

    if lang is GeneratedLanguage.DIVAN:
        return DivanGeneratorProperties(
            general_properties=generator_properties,
            location=generator_properties_location,
            lang=lang,
            mode=mode,
            specific_properties=specific_properties,
            properties_list=properties_list,
        )
    generator_properties_classes = {
        GeneratedLanguage.SWIFT: SwiftGeneratorProperties,
        GeneratedLanguage.TYPE_SCRIPT: TypeScriptGeneratorProperties,
        GeneratedLanguage.KOTLIN_DSL: KotlinDSLGeneratorProperties,
        GeneratedLanguage.DOCUMENTATION: DocumentationGeneratorProperties,
    }
    specific_properties_class = generator_properties_classes.get(lang, None)
    if specific_properties_class is None:
        raise NotImplementedError

    return specific_properties_class(
        general_properties=generator_properties,
        lang=lang,
        mode=mode,
        specific_properties=specific_properties,
    )


def _build_documentation_generator_properties(
        dictionary: Dict[str, Any],
        location: ElementLocation,
        mode: GenerationMode,
) -> Optional[DocumentationGeneratorProperties]:
    generator_properties: Dict[str, Any] = dictionary.get("codegen", None)
    generator_properties_location = location + "codegen"
    if generator_properties is None:
        return None

    if not isinstance(generator_properties, Dict):
        raise GenericError(
            location=generator_properties_location,
            text='Must have format {String : Any}'
        )
    lang_value = GeneratedLanguage.DOCUMENTATION.value
    specific_properties: Dict[str, Any] = generator_properties.get(lang_value, None)
    if specific_properties is None:
        return None
    if not isinstance(specific_properties, Dict):
        raise GenericError(
            location=generator_properties_location + lang_value,
            text='Must have format {String : Any}'
        )
    return DocumentationGeneratorProperties(
        general_properties=generator_properties,
        lang=GeneratedLanguage.DOCUMENTATION,
        mode=mode,
        specific_properties=specific_properties,
    )


def _get_property_by_name(name: str, properties: List[Property], location: ElementLocation) -> Property:
    found_property = None
    for property in properties:
        if property.name == name:
            found_property = property
            break
    if found_property is None:
        raise GenericError(location, f"Object does not contains \"{name}\" property")
    return deepcopy(found_property)


class GeneratorProperties(ABC):
    def __init__(
            self,
            general_properties: Dict[str, Any],
            lang: GeneratedLanguage,
            mode: GenerationMode,
    ):
        self.general_properties = general_properties
        protocol_name = general_properties.get('protocol_name')
        self.protocol_names: List[str] = []
        if protocol_name is not None:
            if not isinstance(protocol_name, List):
                protocol_name = [protocol_name]
            if is_list_of_type(protocol_name, str):
                self.protocol_names: List[str] = list(map(lambda n: n + mode.name_suffix, protocol_name))
        self.alias = alias_for(lang, general_properties)


def description_doc(description_translations: Dict[str, str], lang: DescriptionLanguage, description: str) -> str:
    try:
        return description_translations[lang.value]
    except KeyError:
        return description


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
    def resolved_declaration_prefix(self) -> str:
        if self.parent is not None:
            return f'{self.parent.resolved_declaration_prefix}{capitalize_camel_case(self.parent.resolved_name)}.'
        return ''

    @property
    def resolved_prefixed_declaration(self) -> str:
        return self.resolved_declaration_prefix + capitalize_camel_case(self.resolved_name)

    @property
    def prefixed_declaration(self) -> str:
        return self.declaration_prefix + capitalize_camel_case(self.name)

    @property
    def declaration_prefix(self) -> str:
        if self.parent is not None:
            return f'{self.parent.declaration_prefix}{capitalize_camel_case(self.parent.name)}.'
        return ''

    @property
    def template_declaration_prefix(self) -> str:
        if self.parent is not None:
            name = capitalize_camel_case(self.parent.resolved_name + TEMPLATE_SUFFIX)
            return f'{self.parent.template_declaration_prefix}{name}.'
        return ''

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
    def as_json(self) -> Dict:
        pass

    @abstractmethod
    def resolve_dependencies(self, global_objects: List[Declarable]) -> None:
        pass

    @abstractmethod
    def check_dependencies_resolved(self, location: ElementLocation, stack: List[Declarable]) -> None:
        pass


def _super_entities(config: Config.GenerationConfig, dictionary: Dict[str, any]) -> Optional[str]:
    super_entities_dict = {
        GeneratedLanguage.DART: 'dart_interfaces',
        GeneratedLanguage.DIVAN: 'kotlin_interfaces',
        GeneratedLanguage.DOCUMENTATION: 'swift_protocols',
        GeneratedLanguage.KOTLIN: 'kotlin_interfaces',
        GeneratedLanguage.KOTLIN_DSL: 'kotlin_interfaces',
        GeneratedLanguage.PYTHON: 'python_classes',
        GeneratedLanguage.SWIFT: 'swift_protocols',
        GeneratedLanguage.TYPE_SCRIPT: 'typescript_interfaces',
    }
    super_entities_key: str = super_entities_dict[config.lang]
    if super_entities_key is None:
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
        self._super_entities: Optional[str] = _super_entities(config, dictionary)
        self._resolved_name: str = alias(config.lang, dictionary) or fixing_reserved_typename(name, config.lang)
        self._name: str = self._resolved_name + mode.name_suffix
        self._root_entity: bool = dictionary.get('root_entity', False)
        self._description: str = dictionary.get('description', '')
        self._description_object: Dict[str, str] = dictionary.get('description_translations', {})
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
                self._protocol_names: List[str] = list(map(lambda n: n + mode.name_suffix, prot_names))
            else:
                self._protocol_names: List[str] = []
        else:
            self._protocol_names: List[str] = []

        self._is_deprecated: bool = dictionary.get('deprecated', False)
        self._type_script_templatable: bool = dictionary.get('typescript_templatable', True)
        self._lang: GeneratedLanguage = config.lang
        self._inner_types: List[Declarable] = inner_types_from_definitions + inner_types
        for inner_type in self._inner_types:
            inner_type.parent = self
        self.__resolve_property_objects()

        self.generator_properties: Optional[GeneratorProperties] = _build_generator_properties(
            dictionary,
            location,
            config,
            mode,
            self._properties
        )

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

    @property
    def super_entities(self) -> Optional[str]:
        return self._super_entities

    @property
    def is_deprecated(self) -> bool:
        return self._is_deprecated

    def protocol_plus_super_entities(self, with_impl_protocol=True) -> Optional[str]:
        protocols = []
        if with_impl_protocol and self._implemented_protocol is not None:
            protocols.append(capitalize_camel_case(self._implemented_protocol.name))
        protocol_name = self.generation_mode.protocol_name(self._lang, self.resolved_prefixed_declaration)
        if protocol_name is not None:
            protocols.append(protocol_name)
        if self.super_entities is not None:
            protocols.append(self.super_entities)
        if not protocols:
            return None
        return ', '.join(protocols)

    @property
    def properties(self) -> List[Property]:
        return self._properties

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
        if not isinstance(self.generator_properties, DocumentationGeneratorProperties):
            return False
        return cast(DocumentationGeneratorProperties, self.generator_properties).include_in_toc

    @property
    def generate_as_protocol(self) -> bool:
        return self._name in self._protocol_names

    @property
    def implemented_protocol(self) -> Optional[Entity]:
        return self._implemented_protocol

    @property
    def enclosing_enumerations(self) -> List[EntityEnumeration]:
        return self._enclosing_enumerations

    @property
    def inner_types(self) -> List[Declarable]:
        return self._inner_types

    @property
    def generation_mode(self) -> GenerationMode:
        return self._generation_mode

    @property
    def instance_properties(self) -> List[Property]:
        return list(filter(lambda p: not isinstance(p.property_type, StaticString), self._properties))

    @property
    def all_properties_are_optional_except_default_values(self) -> bool:
        all_properties_are_optional = all(p.optional for p in self.properties)
        if not all_properties_are_optional:
            return False
        return any(p.default_value is not None for p in self.properties)

    @property
    def static_type(self) -> Optional[str]:
        prop = next((p for p in self._properties if p.name == 'type' and isinstance(p.property_type, StaticString)), None)
        if prop is not None:
            return cast(StaticString, prop.property_type).value
        return None

    def description_doc(self, lang: DescriptionLanguage = DescriptionLanguage.EN) -> str:
        return description_doc(self._description_object, lang, self._description)

    def __resolve_declaration_with_objects(self,
                                           property_type: PropertyType,
                                           global_objects: List[Declarable]) -> PropertyType:
        if isinstance(property_type, Object) and property_type.object is None:
            result = next((d for d in global_objects if property_type.name in [d.name, d.original_name]), None)
            if result is not None:
                if isinstance(result, StringEnumeration):
                    return Object(name=property_type.name, object=result, format=property_type.format)
                else:
                    actual_name = property_type.name + self._generation_mode.name_suffix
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

        if self._lang in [GeneratedLanguage.KOTLIN_DSL, GeneratedLanguage.DIVAN]:
            valid_names = (self._name, self._resolved_name, self._original_name)
            self._enclosing_enumerations = [
                enumeration for enumeration in global_objects
                if isinstance(enumeration, EntityEnumeration)
                and any(entities[0] in valid_names for entities in enumeration.entities)
            ]

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
    def root_entity(self) -> bool:
        return self._root_entity

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
                 generate_case_for_templates: bool,
                 entities: List[str],
                 mode: GenerationMode) -> None:
        super().__init__()
        self._resolved_name: str = name
        self._name: str = self._resolved_name + mode.name_suffix
        self._original_name: str = original_name
        self._include_in_documentation_toc: bool = include_in_documentation_toc
        self._root_entity: bool = root_entity
        self._generate_case_for_templates: bool = generate_case_for_templates
        self._resolved_entity_names: List[str] = entities
        self._entities: List[Tuple[str, Optional[Declarable]]] = list(map(
            lambda entity: (entity + mode.name_suffix, None),
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

    @property
    def mode(self) -> GenerationMode:
        return self._mode

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

    @property
    def entity_names(self) -> List[str]:
        return list(map(lambda x: x[0], self._entities))

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
            if entity is not None:
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

    def common_interface(self, lang: GeneratedLanguage) -> Optional[str]:
        common_interface = self._common_interface_without_serializable
        if lang is GeneratedLanguage.SWIFT:
            if common_interface is not None:
                common_interface = f' & {common_interface}'
            else:
                common_interface = ''
            return f'Serializable{common_interface}'
        return common_interface

    @property
    def _common_interface_without_serializable(self) -> Optional[str]:
        if self.entities:
            interface: Optional[Entity] = cast(
                Optional[Entity],
                next(
                    (cast(Entity, e[1]).implemented_protocol for e in self.entities if isinstance(e[1], Entity)),
                    None
                )
            )
            if interface is not None:
                for entity in self.entities:
                    other_interface = entity[1]
                    if isinstance(other_interface, Entity) and other_interface.implemented_protocol is not interface:
                        return None
                return capitalize_camel_case(interface.name)
        return None

    @property
    def generate_case_for_templates(self) -> bool:
        return self._generate_case_for_templates


class StringEnumeration(Declarable):
    def __init__(self,
                 name: str,
                 original_name: str,
                 cases: Union[List[str], List[Tuple[str, str]]],
                 description: str,
                 description_object: Dict[str, str],
                 include_in_documentation_toc: bool) -> None:
        super().__init__()
        self._name: str = name
        self._original_name: str = original_name
        self._description = description
        self._description_object: Dict[str, str] = description_object
        if is_list_of_type(cases, str):
            self._cases: List[Tuple[str, str]] = list(map(lambda case: (case, case), cases))
        else:
            self._cases: List[Tuple[str, str]] = cases
        self._include_in_documentation_toc: bool = include_in_documentation_toc

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

    def description_doc(self, lang: DescriptionLanguage = DescriptionLanguage.EN) -> str:
        return description_doc(self._description_object, lang, self._description)

    @property
    def include_in_documentation_toc(self) -> bool:
        return self._include_in_documentation_toc

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
    elif lang is GeneratedLanguage.TYPE_SCRIPT:
        specific_prefix = 'typescript_'
    elif lang is GeneratedLanguage.PYTHON:
        specific_prefix = 'python_'
    elif lang is GeneratedLanguage.DART:
        specific_prefix = 'dart_'
    specific_key = specific_prefix + generic_key
    result: Optional[str] = dictionary.get(specific_key, dictionary.get(generic_key))
    if isinstance(result, str):
        property_type.validate(value=result, location=location + specific_key)
        return result
    return None


class PropertyType(ABC):
    @property
    def supports_expressions(self) -> bool:
        if isinstance(self, (Int, Double, Bool, BoolInt, String, Color, Url)):
            return True
        elif isinstance(self, (Dictionary, StaticString)):
            return False
        elif isinstance(self, Array):
            if isinstance(self.property_type, Object) and \
                    isinstance(self.property_type.object, StringEnumeration):
                return False
            return self.property_type.supports_expressions
        elif isinstance(self, Object):
            return isinstance(self.object, StringEnumeration)

    def validate(self, value: str, location: ElementLocation) -> None:
        error = InvalidFieldRepresentationError(location, value)
        if isinstance(self, Url):
            regex = re.compile(
                r'^[A-Z0-9][A-Z0-9-]+[A-Z0-9]://'  # any letter and digit with '-'
                r'(?:(?:[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?\.?)+(?:[A-Z]{2,6}\.?|[A-Z0-9-]{2,}\.?)|'  # domain...
                r'localhost|'  # localhost...
                r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})'  # ...or ip
                r'(?::\d+)?'  # optional port
                r'(?:/?|[/?]\S+)$', re.IGNORECASE)
            if re.match(regex, value) is None:
                raise error
        elif isinstance(self, Color):
            color_matcher = re.compile(r'^#([0-9a-fA-F]{2}){3,4}$')
            if not color_matcher.fullmatch(value):
                raise error

    @property
    def can_be_templated(self) -> bool:
        if isinstance(self, Array):
            return self.property_type.can_be_templated
        elif isinstance(self, Object):
            return not isinstance(self.object, StringEnumeration)
        else:
            return False

    @property
    def as_json(self) -> Dict:
        if isinstance(self, (Int, Double, Bool, BoolInt, String, StaticString, Color, Url, Dictionary)):
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
    long_type: Optional[bool]


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
    description_translations: Dict[str, str]
    dict_field: str
    property_type: PropertyType
    optional: bool
    is_deprecated: bool
    mode: GenerationMode
    supports_expressions_flag: bool
    default_value: Optional[str]
    platforms: Optional[List[Platform]]

    def __str__(self) -> str:
        return f'Property(name={self.name}, type={self.property_type})'

    def __repr__(self) -> str:
        return str(self)

    @property
    def supports_expressions(self) -> bool:
        if self.property_type.supports_expressions:
            return self.supports_expressions_flag
        return False

    @property
    def as_json(self) -> Dict:
        return {
            'type': 'property',
            'name': self.name,
            'property_type': self.property_type.as_json
        }

    def description_doc(self, lang: DescriptionLanguage = DescriptionLanguage.EN) -> str:
        return description_doc(self.description_translations, lang, self.description)


class GeneralGeneratorProperties(GeneratorProperties):
    pass


class TypeScriptGeneratorProperties(GeneratorProperties):
    def __init__(
            self,
            general_properties: Dict[str, Any],
            lang: GeneratedLanguage,
            mode: GenerationMode,
            specific_properties: Dict[str, Any],
    ):
        super().__init__(general_properties, lang, mode)
        self.templatable: bool = specific_properties.get('templatable', True)


class SwiftGeneratorProperties(GeneratorProperties):
    def __init__(
            self,
            general_properties: Dict[str, Any],
            lang: GeneratedLanguage,
            mode: GenerationMode,
            specific_properties: Dict[str, Any],
    ):
        super().__init__(general_properties, lang, mode)
        self.generate_optional_args: bool = specific_properties.get('generate_optional_arguments', True)
        self.super_protocol: Optional[str] = specific_properties.get('super_protocol')


class DocumentationGeneratorProperties(GeneratorProperties):
    def __init__(
            self,
            general_properties: Dict[str, Any],
            lang: GeneratedLanguage,
            mode: GenerationMode,
            specific_properties: Dict[str, Any],
    ):
        super().__init__(general_properties, lang, mode)
        self.include_in_toc: bool = specific_properties.get('include_in_toc', False)


class KotlinDSLGeneratorProperties(GeneratorProperties):
    def __init__(
            self,
            general_properties: Dict[str, Any],
            lang: GeneratedLanguage,
            mode: GenerationMode,
            specific_properties: Dict[str, Any],
    ):
        super().__init__(general_properties, lang, mode)
        self.root_entity: bool = specific_properties.get('root_entity', False)


class DivanGeneratorProperties(GeneratorProperties):
    def __init__(
            self,
            general_properties: Dict[str, Any],
            location: ElementLocation,
            lang: GeneratedLanguage,
            mode: GenerationMode,
            specific_properties: Dict[str, Any],
            properties_list: List[Property],
    ):
        super().__init__(general_properties, lang, mode)

        self.forced_properties_order = specific_properties.get("forced_properties_order", [])
        if not is_list_of_type(self.forced_properties_order, str):
            raise GenericError(
                location=location + lang.value + 'forced_properties_order',
                text='Must have format [String]'
            )
        self.__resolve_forced_properties_order(location + lang.value + 'forced_properties_order', properties_list)

        factories_dict: Dict[str, any] = specific_properties.get('factories')
        self.factories: List[DivanFactory] = []
        if factories_dict is not None:
            self.__resolve_factories(location + lang.value + 'factories', factories_dict, properties_list)

        self.plus_operator_declaration = specific_properties.get("plus_operator", True)

        self.alias_factory = specific_properties.get("alias_factory", None)

        self.required_properties = specific_properties.get("required_properties_at_factory", False)

    def __resolve_forced_properties_order(self, location: ElementLocation, properties_list: List[Property]):
        found_properties: Set[str] = set()
        for property in properties_list:
            if property.name in self.forced_properties_order:
                found_properties.add(property.name)

        if len(self.forced_properties_order) != len(found_properties):
            not_contains_props = ', '.join(filter(lambda prop: prop not in found_properties, self.forced_properties_order))
            raise GenericError(location, f"Object does not contains properties: {not_contains_props}")

    def __resolve_factories(
            self,
            location: ElementLocation,
            factories_dict: Dict[str, any],
            properties_list: List[Property]
    ):
        for factory_method_name in factories_dict:
            factory = factories_dict[factory_method_name]
            divan_factory = DivanFactory(factory_method_name=factory_method_name)
            if "vararg_property" in factory:
                vararg_property = _get_property_by_name(
                    name=factory["vararg_property"],
                    properties=properties_list,
                    location=location + factory_method_name + "vararg_property",
                )
                if not isinstance(vararg_property.property_type, Array):
                    raise GenericError(location + factory_method_name + "vararg_property", "Expected array type")
                divan_factory.vararg_property = vararg_property
            if "inlines" in factory:
                divan_factory.inlines = dict()
                for inline_property_name, inline_value in factory["inlines"].items():
                    inline_property = _get_property_by_name(
                        name=inline_property_name,
                        properties=properties_list,
                        location=location + factory_method_name + "inlines",
                    )
                    divan_factory.inlines[inline_property.name] = inline_value
            self.factories.append(divan_factory)


@dataclass
class DivanFactory:
    factory_method_name: str
    vararg_property: Optional[Property] = None
    inlines: Optional[Dict[str, str]] = None
