import re
from typing import List, Tuple, Optional, Dict, Union, cast

from .utils import (
    fixing_reserved_typename,
    should_generate_swift_serialization,
    generate_cases_for_templates,
    alias,
    platforms
)
from ..utils import is_list_of_type, is_dict_with_keys_of_type, code_generation_disabled

from ...config import Config, GenerationMode, GeneratedLanguage
from ..preprocessing.entities import ElementLocation
from .entities import (
    Entity,
    EntityEnumeration,
    StringEnumeration,
    Declarable,
    PropertyType,
    Property,
    default_value as property_default_value,
    Int,
    Bool,
    BoolInt,
    Double,
    StaticString,
    Object,
    Array,
    ObjectFormat,
    Url,
    Color,
    String,
    Dictionary
)
from .errors import InvalidFieldRepresentationError, UnsupportedFormatTypeError


def __resolve_string_field(name: str,
                           mode: GenerationMode,
                           config: Config.GenerationConfig,
                           location: ElementLocation,
                           dictionary: Dict[str, any]) -> Tuple[PropertyType, List[Declarable]]:
    force_instance_field: bool = dictionary.get('force_instance_field', False)
    enum_cases: List[str] = dictionary.get('enum')
    if not force_instance_field and enum_cases is not None:
        if not is_list_of_type(enum_cases, str):
            raise TypeError
        force_enum_field: bool = dictionary.get('force_instance_field', False)
        if len(enum_cases) > 1 or force_enum_field:
            fixed_name = fixing_reserved_typename(name, config.lang)
            enumeration = StringEnumeration(name=fixed_name,
                                            original_name=name,
                                            cases=enum_cases,
                                            include_documentation_toc=dictionary.get('include_in_documentation_toc',
                                                                                     False))
            return Object(name=name, object=None, format=ObjectFormat.DEFAULT), [enumeration]
        else:
            if len(enum_cases) < 1:
                raise InvalidFieldRepresentationError(location=location + 'enum', value=enum_cases)
            return StaticString(value=enum_cases[0]), []
    else:
        format_value: Optional[Union[str, Dict[str, any]]] = dictionary.get('format')
        if format_value == 'uri':
            return Url(schemes=dictionary.get('schemes', [])), []
        elif format_value == 'color':
            return Color(), []
        elif is_dict_with_keys_of_type(format_value, str):
            property_type, inner_declarations = type_property_build(dictionary=format_value,
                                                                    outer_name=name,
                                                                    location=location + 'format',
                                                                    mode=mode,
                                                                    config=config)
            if not isinstance(property_type, Object):
                raise UnsupportedFormatTypeError
            assert property_type.format == ObjectFormat.DEFAULT
            return Object(name=property_type.name, object=property_type.object,
                          format=ObjectFormat.JSON_STRING), inner_declarations
        else:
            pattern = dictionary.get('pattern')
            regex = re.compile(pattern) if pattern is not None else None
            return String(min_length=dictionary.get('minLength', 0),
                          formatted=format_value == 'formatted_string',
                          regex=regex,
                          enable_optimization=dictionary.get('optimization', False)), []


def _entity_enumeration_build(entities: List[Dict[str, any]],
                              name: str,
                              original_name: str,
                              include_in_documentation_toc: bool,
                              root_entity: bool,
                              generate_serialization: bool,
                              generate_case_for_templates: bool,
                              location: ElementLocation,
                              mode: GenerationMode,
                              config: Config.GenerationConfig) -> List[Declarable]:
    resulting_declarations: List[Declarable] = []
    property_types: List[str] = []
    for index, entity in enumerate(entities):
        entity_location = location + str(index)
        preprocessor_typename: str = entity.get('$typename')
        if preprocessor_typename is not None:
            typename = preprocessor_typename
        else:
            defined_type = entity.get('type')
            if not defined_type.startswith('$defined_'):
                raise InvalidFieldRepresentationError(entity_location, entity)
            typename = defined_type.replace('$defined_', '')

        property_type, declarations = type_property_build(dictionary=entity,
                                                          outer_name=typename,
                                                          location=entity_location,
                                                          mode=mode,
                                                          config=config)

        if isinstance(property_type, StaticString):
            raise InvalidFieldRepresentationError(entity_location, entity)

        property_types.append(typename)
        resulting_declarations.extend(declarations)

    return [cast(Declarable, EntityEnumeration(name=name,
                                               original_name=original_name,
                                               include_in_documentation_toc=include_in_documentation_toc,
                                               root_entity=root_entity,
                                               generate_serialization=generate_serialization,
                                               generate_case_for_templates=generate_case_for_templates,
                                               entities=property_types,
                                               mode=mode))] + resulting_declarations


def entity_enumeration_build(entities: List[Dict[str, any]],
                             name: str,
                             original_name: str,
                             include_in_documentation_toc: bool,
                             root_entity: bool,
                             generate_serialization: bool,
                             generate_case_for_templates: bool,
                             location: ElementLocation,
                             config: Config.GenerationConfig) -> List[Declarable]:
    def make_result(mode: GenerationMode) -> List[Declarable]:
        return _entity_enumeration_build(entities=entities,
                                         name=name,
                                         original_name=original_name,
                                         include_in_documentation_toc=include_in_documentation_toc,
                                         root_entity=root_entity,
                                         generate_serialization=generate_serialization,
                                         generate_case_for_templates=generate_case_for_templates,
                                         location=location,
                                         mode=mode,
                                         config=config)

    normal_result: List[Declarable] = make_result(GenerationMode(GenerationMode.Type.NORMAL_WITH_TEMPLATES))
    template_result: List[Declarable] = make_result(GenerationMode(GenerationMode.Type.TEMPLATE))
    return normal_result + template_result


def entity_build(name: str,
                 dictionary: Dict[str, any],
                 location: ElementLocation,
                 config: Config.GenerationConfig) -> List[Entity]:
    def make_result(mode: GenerationMode) -> Entity:
        return Entity(name=name,
                      dictionary=dictionary,
                      location=location,
                      mode=mode,
                      config=config)

    normal: Entity = make_result(mode=GenerationMode(generation_type=GenerationMode.Type.NORMAL_WITH_TEMPLATES))
    if not normal.generate_as_protocol:
        if config.lang in [GeneratedLanguage.SWIFT, GeneratedLanguage.KOTLIN]:
            return [normal, make_result(mode=GenerationMode(generation_type=GenerationMode.Type.TEMPLATE))]
        raise NotImplementedError(f'Templates are not supported for {config.lang.value}')
    return [normal]


def type_property_build(dictionary: Dict[str, any],
                        outer_name: str,
                        location: ElementLocation,
                        mode: GenerationMode,
                        config: Config.GenerationConfig) -> Tuple[PropertyType, List[Declarable]]:
    name: str = alias(config.lang, dictionary) or outer_name
    any_of_entities: List[Dict[str, any]] = dictionary.get('anyOf')
    if is_list_of_type(any_of_entities, Dict) and all(is_dict_with_keys_of_type(d, str) for d in any_of_entities):
        name = fixing_reserved_typename(name, config.lang)
        entity_enum: List[Declarable] = _entity_enumeration_build(
            entities=any_of_entities,
            name=name,
            original_name=outer_name,
            include_in_documentation_toc=dictionary.get('include_in_documentation_toc', False),
            root_entity=dictionary.get('root_entity', False),
            generate_serialization=should_generate_swift_serialization(config, dictionary),
            generate_case_for_templates=generate_cases_for_templates(config.lang, dictionary),
            location=location + 'anyOf',
            mode=mode,
            config=config)
        return Object(name=name, object=None, format=ObjectFormat.DEFAULT), entity_enum

    type_value: str = dictionary.get('type')
    number_constraints: Optional[str] = dictionary.get('constraint')

    if type_value == 'integer':
        if dictionary.get('format') == 'boolean':
            return BoolInt(), []
        return Int(constraint=number_constraints), []
    elif type_value == 'number':
        return Double(constraint=number_constraints), []
    elif type_value == 'boolean':
        return Bool(), []
    elif type_value == 'string':
        return __resolve_string_field(name=name,
                                      mode=mode,
                                      config=config,
                                      location=location,
                                      dictionary=dictionary)
    elif type_value == 'array':
        items_types: Dict[str, any] = dictionary.get('items')
        single_name: str = name[:-1] if name.endswith('s') else name
        min_items: int = dictionary.get('minItems', 1)
        strict_parsing: bool = dictionary.get('strictParsing', False)
        property_type, declarations = type_property_build(dictionary=items_types,
                                                          outer_name=single_name,
                                                          location=location + 'items',
                                                          mode=mode,
                                                          config=config)
        return Array(property_type=property_type, min_items=min_items, strict_parsing=strict_parsing), declarations
    elif type_value == 'object':
        if dictionary.get('additionalProperties', False) and 'properties' not in dictionary:
            return Dictionary(), []
        entity: Entity = Entity(name=name,
                                dictionary=dictionary,
                                location=location,
                                mode=mode,
                                config=config)
        return Object(name=entity.original_name, object=None, format=ObjectFormat.DEFAULT), [entity]
    else:
        if not type_value.startswith('$defined_'):
            raise InvalidFieldRepresentationError(location=location + 'type', value=dictionary)
        return Object(name=type_value.replace('$defined_', ''), object=None, format=ObjectFormat.DEFAULT), []


def property_build(properties: Dict[str, Dict[str, any]],
                   required: Optional[str],
                   location: ElementLocation,
                   mode: GenerationMode,
                   config: Config.GenerationConfig) -> Tuple[List[Property], List[Declarable]]:
    properties_list: List[Property] = []
    inner_types_list: List[Declarable] = []
    for dict_field, dictionary in filter(lambda pair: not code_generation_disabled(config.lang, pair[1]),
                                         properties.items()):
        location_val = location + dict_field
        property_type, inner_types = type_property_build(dictionary=dictionary,
                                                         outer_name=dict_field,
                                                         location=location_val,
                                                         mode=mode,
                                                         config=config)
        default_value: str = property_default_value(lang=config.lang,
                                                    property_type=property_type,
                                                    location=location_val,
                                                    dictionary=dictionary)
        is_required: bool = dict_field in required if required is not None else False
        name = alias(lang=config.lang, dictionary=dictionary)
        properties_list.append(Property(name=name,
                                        description=dictionary.get('description'),
                                        dict_field=dict_field,
                                        property_type=property_type,
                                        optional=not is_required,
                                        is_deprecated=dictionary.get('deprecated', False),
                                        mode=mode.type,
                                        default_value=default_value,
                                        platforms=platforms(dictionary)))
        inner_types_list.extend(inner_types)

    return sorted(properties_list, key=lambda p: isinstance(p.property_type, StaticString)), inner_types_list
