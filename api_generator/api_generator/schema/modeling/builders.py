import re
from typing import List, Tuple, Optional, Dict, Union, cast

from .utils import (
    fixing_reserved_typename,
    generate_cases_for_templates,
    alias,
    platforms
)
from ..utils import is_list_of_type, is_dict_with_keys_of_type, code_generation_disabled

from ...config import Config, GenerationMode, GeneratedLanguage, TEMPLATE_SUFFIX
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
    Double,
    StaticString,
    Object,
    Array,
    ObjectFormat,
    Url,
    Color,
    String,
    Dictionary,
    BoolInt,
    RawArray,
    _build_documentation_generator_properties
)
from .errors import InvalidFieldRepresentationError, UnsupportedFormatTypeError, GenericError


def __generate_templates(config: Config.GenerationConfig) -> bool:
    return config.lang in [GeneratedLanguage.SWIFT, GeneratedLanguage.KOTLIN] and config.generate_templates


def __resolve_string_field(name: str,
                           mode: GenerationMode,
                           config: Config.GenerationConfig,
                           location: ElementLocation,
                           dictionary: Dict[str, any]) -> Tuple[PropertyType, List[Declarable]]:
    enum_cases: List[str] = dictionary.get('enum')
    if enum_cases is not None:
        if not is_list_of_type(enum_cases, str):
            raise TypeError
        if len(enum_cases) > 1:
            fixed_name = fixing_reserved_typename(name, config.lang)
            documentation_properties = _build_documentation_generator_properties(dictionary=dictionary,
                                                                                 location=location,
                                                                                 mode=mode)
            include_in_documentation_toc = False
            if documentation_properties is not None:
                include_in_documentation_toc = documentation_properties.include_in_toc

            enumeration = StringEnumeration(name=fixed_name,
                                            original_name=name,
                                            cases=enum_cases,
                                            description=dictionary.get('description', ''),
                                            description_object=dictionary.get('description_translations', {}),
                                            include_in_documentation_toc=include_in_documentation_toc)
            return Object(name=name, object=None, format=ObjectFormat.DEFAULT), [enumeration]
        else:
            if len(enum_cases) < 1:
                raise InvalidFieldRepresentationError(location=location + 'enum', value=enum_cases)
            return StaticString(value=enum_cases[0]), []
    else:
        format_value: Optional[Union[str, Dict[str, any]]] = dictionary.get('format')
        if format_value == 'uri':
            return Url(schemes=dictionary.get('schemes')), []
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
                          enable_optimization=dictionary.get('client_optimized', False)), []


def _entity_enumeration_build(entities: List[Dict[str, any]],
                              generated_entities: List[Declarable],
                              name: str,
                              original_name: str,
                              include_in_documentation_toc: bool,
                              root_entity: bool,
                              generate_case_for_templates: bool,
                              location: ElementLocation,
                              mode: GenerationMode,
                              config: Config.GenerationConfig) -> List[Declarable]:
    resulting_declarations: List[Declarable] = []
    property_types: List[str] = []
    default_entity_declarations: List[str] = []
    default_entity_declaration: str = ''
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

        if mode.is_template:
            default_name = f'{typename}{TEMPLATE_SUFFIX}'
        else:
            default_name = typename

        decl = next((d for d in generated_entities + declarations if d.name == default_name), None)
        use_as_default = decl is not None and decl.type_is_optional

        if isinstance(property_type, StaticString):
            raise InvalidFieldRepresentationError(entity_location, entity)
        elif isinstance(property_type, Object):
            if use_as_default:
                default_entity_declarations.append(property_type.name)
            property_types.append(property_type.name)
        else:
            if use_as_default:
                default_entity_declarations.append(typename)
            property_types.append(typename)
        resulting_declarations.extend(declarations)

    if len(default_entity_declarations) > 1:
        raise GenericError(entity_location, f'Defined multiple default type for anyOf : {default_entity_declarations}')
    elif len(default_entity_declarations) == 1:
        default_entity_declaration = default_entity_declarations[0]

    return [cast(Declarable, EntityEnumeration(name=name,
                                               original_name=original_name,
                                               include_in_documentation_toc=include_in_documentation_toc,
                                               root_entity=root_entity,
                                               generate_case_for_templates=generate_case_for_templates,
                                               entities=property_types,
                                               default_entity_declaration=default_entity_declaration,
                                               mode=mode))] + resulting_declarations


def entity_enumeration_build(entities: List[Dict[str, any]],
                             generated_entities: List[Declarable],
                             name: str,
                             original_name: str,
                             include_in_documentation_toc: bool,
                             root_entity: bool,
                             generate_case_for_templates: bool,
                             location: ElementLocation,
                             config: Config.GenerationConfig) -> List[Declarable]:
    def make_result(mode: GenerationMode) -> List[Declarable]:
        return _entity_enumeration_build(entities=entities,
                                         generated_entities=generated_entities,
                                         name=name,
                                         original_name=original_name,
                                         include_in_documentation_toc=include_in_documentation_toc,
                                         root_entity=root_entity,
                                         generate_case_for_templates=generate_case_for_templates,
                                         location=location,
                                         mode=mode,
                                         config=config)

    normal_result: List[Declarable] = make_result(GenerationMode(GenerationMode.NORMAL_WITH_TEMPLATES))
    template_result: List[Declarable] = []
    if __generate_templates(config):
        template_result += make_result(GenerationMode(GenerationMode.TEMPLATE))
    return normal_result + template_result


def entity_build(name: str,
                 dictionary: Dict[str, any],
                 location: ElementLocation,
                 config: Config.GenerationConfig) -> List[Entity]:
    def make_result(mode: GenerationMode) -> Entity:
        return Entity(name=name,
                      original_name=name,
                      dictionary=dictionary,
                      location=location,
                      mode=mode,
                      config=config)

    normal: Entity = make_result(mode=GenerationMode.NORMAL_WITH_TEMPLATES)
    if not normal.generate_as_protocol and __generate_templates(config):
        return [normal, make_result(mode=GenerationMode.TEMPLATE)]
    return [normal]


def type_property_build(dictionary: Dict[str, any],
                        outer_name: str,
                        location: ElementLocation,
                        mode: GenerationMode,
                        config: Config.GenerationConfig) -> Tuple[PropertyType, List[Declarable]]:
    name: str = alias(config.lang, dictionary) or outer_name

    if is_boolean_int_property(dictionary):
        return BoolInt(), []

    any_of_entities: List[Dict[str, any]] = dictionary.get('anyOf')
    if is_list_of_type(any_of_entities, Dict) and all(is_dict_with_keys_of_type(d, str) for d in any_of_entities):
        name = fixing_reserved_typename(name, config.lang)
        documentation_properties = _build_documentation_generator_properties(dictionary=dictionary,
                                                                             location=location,
                                                                             mode=mode)
        include_in_documentation_toc = False
        if documentation_properties is not None:
            include_in_documentation_toc = documentation_properties.include_in_toc
        entity_enum: List[Declarable] = _entity_enumeration_build(
            generated_entities=[],
            entities=any_of_entities,
            name=name,
            original_name=outer_name,
            include_in_documentation_toc=include_in_documentation_toc,
            root_entity=dictionary.get('root_entity', False),
            generate_case_for_templates=generate_cases_for_templates(config.lang, dictionary),
            location=location + 'anyOf',
            mode=mode,
            config=config)
        return Object(name=name, object=None, format=ObjectFormat.DEFAULT), entity_enum

    type_value: str = dictionary.get('type')
    number_constraints: Optional[str] = dictionary.get('constraint')

    if type_value == 'integer':
        return Int(constraint=number_constraints, long_type=dictionary.get('long_type', False)), []
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
        if 'items' not in dictionary:
            return RawArray(), []
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
                                original_name=outer_name,
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
        name = alias(lang=config.lang, dictionary=dictionary) or dict_field
        properties_list.append(Property(name=name,
                                        description=dictionary.get('description'),
                                        description_translations=dictionary.get('description_translations', {}),
                                        dict_field=dict_field,
                                        property_type=property_type,
                                        optional=not is_required,
                                        is_deprecated=dictionary.get('deprecated', False),
                                        mode=mode,
                                        supports_expressions_flag=dictionary.get('supports_expressions', True),
                                        default_value=default_value,
                                        platforms=platforms(dictionary)))
        inner_types_list.extend(inner_types)

    return sorted(properties_list, key=lambda p: isinstance(p.property_type, StaticString)), inner_types_list


def is_boolean_int_property(dictionary: Dict[str, any]) -> bool:
    one_of_entities: List[Dict[str, any]] = dictionary.get('oneOf')
    if one_of_entities is None:
        return False
    if len(one_of_entities) != 2:
        return False
    type1: str = one_of_entities[0].get('type')
    type2: str = one_of_entities[1].get('type')
    return (type1 == 'boolean' and type2 == 'integer') or (type1 == 'integer' and type2 == 'boolean')
