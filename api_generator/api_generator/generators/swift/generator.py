from typing import cast, List, Optional

from .swift_entities import (
    SwiftEntity,
    SwiftPropertyType,
    SwiftProperty,
    SwiftEntityEnumeration,
    SwiftAccessLevel,
    swift_template_deserializable_args_decl,
    swift_template_deserializable_args,
    _swift_default_value_declaration_comment
)
from ..base import Generator, declaration_comment
from ... import utils
from ...schema.modeling.entities import (
    StringEnumeration,
    EntityEnumeration,
    Entity,
    Declarable,
    Property,
    String
)
from ...schema.modeling.text import Text, EMPTY
from ...config import Config, GenerationMode, GeneratedLanguage
from . import utils as swift_utils


def _make_equatable_func_body(properties: List[SwiftProperty]) -> Text:
    if not properties:
        return Text('    return true')
    equatable_func_body = Text()
    max_guard_block_length = 3  # Splitting guard conditions by chunks of maxGuardBlockLength to improve build time

    def chunks(lst, n):
        for i in range(0, len(lst), n):
            yield lst[i:i + n]

    def make_equatable_guard_block(field_names: List[str]) -> Text:
        result = Text()
        if not field_names:
            return result
        result += 'guard'
        comparisons = list(map(lambda field_name: f'  lhs.{field_name} == rhs.{field_name}', field_names))
        result += ',\n'.join(comparisons)
        result += 'else {'
        result += '  return false'
        result += '}'
        return result

    for guard_group in chunks(list(map(lambda p: p.declaration_name, properties)), max_guard_block_length):
        equatable_func_body += make_equatable_guard_block(guard_group).indented(indent_width=4)
    equatable_func_body += '    return true'
    return equatable_func_body


def _string_id_property(obj: Declarable) -> Optional[Property]:
    if not isinstance(obj, Entity):
        return None
    p = next((p for p in obj.properties if p.name == 'id'), None)
    if p is None:
        return None
    if isinstance(p.property_type, String):
        return p
    return None


class SwiftGenerator(Generator):
    def __init__(self, config: Config):
        super(SwiftGenerator, self).__init__(config)
        self._access_level = SwiftAccessLevel.PUBLIC

    def filename(self, name: str) -> str:
        return f'{utils.capitalize_camel_case(name)}.swift'

    def _main_declaration(self, obj: Declarable) -> Text:
        main_decl = super()._main_declaration(obj)
        extension = '\n\n'.join(self.__extensions_declaration(obj))
        if extension:
            return main_decl + EMPTY + extension
        else:
            return main_decl

    def __extensions_declaration(self, obj: Declarable) -> List[str]:
        if isinstance(obj, Entity):
            return self.__entity_extensions_declaration(cast(SwiftEntity, obj))
        elif isinstance(obj, EntityEnumeration):
            return self.__entity_enumeration_extensions_declaration(obj)
        else:
            return []

    def __entity_extensions_declaration(self, entity: SwiftEntity) -> List[str]:
        self_extensions = []
        if not entity.generate_as_protocol and not entity.generation_mode.is_template:
            equatable_extension = Text('#if DEBUG')
            props = entity.instance_properties_swift
            equatable_properties = list(filter(lambda p: cast(SwiftPropertyType, p.property_type).is_equatable,
                                               props))
            if len(equatable_properties) != len(props):
                equatable_extension += '// WARNING: this == is incomplete because of [String: Any] in class fields'
            pref_decl = entity.prefixed_declaration
            equatable_extension += f'extension {pref_decl}: Equatable {{'
            access_modifier = self._access_level.value
            equatable_extension += f'  {access_modifier}static func ==(lhs: {pref_decl}, rhs: {pref_decl}) -> Bool {{'
            equatable_extension += _make_equatable_func_body(equatable_properties)
            equatable_extension += '  }'
            equatable_extension += '}'
            equatable_extension += '#endif'
            self_extensions.append(str(equatable_extension))

            extension_prefix = f'extension {entity.prefixed_declaration}'
            serialization = Text(f'{extension_prefix}: Serializable {{')
            serialization += '  public func toDictionary() -> [String: ValidSerializationValue] {'
            serialization += '    var result: [String: ValidSerializationValue] = [:]'
            if entity.has_static_type:
                serialization += '    result["type"] = Self.type'
            for prop in props:
                serialization += Text(prop.serialization_declaration).indented(indent_width=4)
            serialization += '    return result'
            serialization += '  }'
            serialization += '}'
            self_extensions.append(str(serialization))

        inner_types_extensions = []
        for inner_type in entity.inner_types:
            inner_types_extensions += self.__extensions_declaration(inner_type)

        return self_extensions + sorted(inner_types_extensions)

    def __entity_enumeration_extensions_declaration(self, entity_enumeration: EntityEnumeration) -> List[str]:
        protocol = swift_utils.implemented_swift_protocol(entity_enumeration.mode) or ''
        access_modifier = self._access_level.value
        args_decl = swift_template_deserializable_args_decl(entity_enumeration.mode)
        deserializable_extension = Text(f'extension {entity_enumeration.prefixed_declaration}: {protocol} {{')
        deserializable_extension += f'  {access_modifier}init(dictionary: [String: Any]{args_decl}) throws {{'
        body = Text()
        if entity_enumeration.mode.is_template:
            body += 'let receivedType = try dictionary.getField("type") as String'
            body += 'let blockType = templateToType[receivedType] ?? receivedType'
        else:
            body += 'let blockType = try dictionary.getField("type") as String'
        body += 'switch blockType {'
        entity_names = entity_enumeration.entity_names
        for entity, name in zip(entity_enumeration.entities, entity_names):
            obj_t = entity[1].prefixed_declaration if entity[1] is not None else utils.capitalize_camel_case(entity[0])
            low_name = utils.lower_camel_case(name)
            args = swift_template_deserializable_args(entity_enumeration.mode)
            body += f'case {obj_t}.type:'
            body += f'  self = .{low_name}(try {obj_t}(dictionary: dictionary{args}))'
        body += 'default:'
        args = f'field: "{entity_enumeration.name}", representation: dictionary'
        body += f'  throw DeserializationError.invalidFieldRepresentation({args})'
        body += '}'
        deserializable_extension += body.indented(level=2)
        deserializable_extension += '  }'
        deserializable_extension += '}'
        deserializable_extension = str(deserializable_extension)

        equatable_extension = Text('#if DEBUG')
        pref = entity_enumeration.prefixed_declaration
        equatable_extension += f'extension {pref}: Equatable {{'
        equatable_extension += f'  {access_modifier}static func ==(lhs: {pref}, rhs: {pref}) -> Bool {{'
        equatable_extension += '    switch (lhs, rhs) {'
        for name in entity_names:
            low_name = utils.lower_camel_case(name)
            equatable_extension += f'    case let (.{low_name}(l), .{low_name}(r)):'
            equatable_extension += '      return l == r'
        if len(entity_names) > 1:
            equatable_extension += '    default:'
            equatable_extension += '      return false'
        equatable_extension += '    }'
        equatable_extension += '  }'
        equatable_extension += '}'
        equatable_extension += '#endif'
        equatable_extension = str(equatable_extension)

        if entity_enumeration.mode is GenerationMode.NORMAL_WITHOUT_TEMPLATES:
            result = [deserializable_extension, equatable_extension]
        elif entity_enumeration.mode.is_template:
            result = [deserializable_extension]
        else:
            result = [equatable_extension]

        if not entity_enumeration.mode.is_template:
            serialization_extension = Text(f'extension {entity_enumeration.prefixed_declaration}: Serializable {{')
            serialization_extension += f'  {access_modifier}func toDictionary() -> [String: ValidSerializationValue] {{'
            serialization_extension += '    return value.toDictionary()'
            serialization_extension += '  }'
            serialization_extension += '}'
            result.append(str(serialization_extension))

        return result

    def _entity_declaration(self, entity: Entity) -> Text:
        entity: SwiftEntity = cast(SwiftEntity, entity)
        entity.__class__ = SwiftEntity
        entity.update_bases()

        if entity.generate_as_protocol:
            return self.__declaration_as_protocol(entity)
        result: Text = Text(self.__main_declaration_header(entity))

        if entity.inner_types:
            inner_type_declarations = []
            for inner_type in entity.inner_types:
                inner_type_declarations.append(str(super()._main_declaration(inner_type).indented()))
            result += '\n\n'.join(sorted(inner_type_declarations))
            result += EMPTY

        properties_to_declare = entity.properties_to_declare_swift
        if properties_to_declare:
            for prop in properties_to_declare:
                result += prop.declaration(self._access_level).indented()
            result += EMPTY

        filtered_props = list(filter(
            lambda p: p.supports_expressions and not p.mode.is_template,
            properties_to_declare))
        for prop in filtered_props:
            result += prop.expression_resolving_method(self._access_level).indented()

        for prop in entity.instance_properties_swift:
            validator_decl = prop.validator_declaration
            if validator_decl is not None:
                result += validator_decl.indented()
                result += EMPTY

        if entity.generation_mode is GenerationMode.NORMAL_WITHOUT_TEMPLATES or \
                entity.generation_mode.is_template:
            result += entity.deserializing_constructor_declaration.indented()
            result += EMPTY

        result += entity.plain_constructor_declaration.indented()

        if entity.generation_mode.is_template:
            result += EMPTY
            result += entity.resolve_value_only_by_links_body.indented()
            result += EMPTY
            result += entity.resolve_value_declaration.indented()
            result += EMPTY
            result += entity.resolve_template_declaration.indented()

        result += '}'

        return result

    def __declaration_as_protocol(self, entity: SwiftEntity) -> Text:
        super_protocol = ''
        if entity.swift_super_protocol is not None:
            super_protocol = f': {entity.swift_super_protocol}'
        access_modifier = self._access_level.value
        result = Text(f'{access_modifier}protocol {utils.capitalize_camel_case(entity.name)}{super_protocol} {{')
        props = entity.instance_properties_swift
        for prop in props:
            name = prop.declaration_name
            type_decl = prop.type_declaration
            comment = declaration_comment(prop, _swift_default_value_declaration_comment)
            result += Text(f'var {name}: {type_decl} {{ get }}{comment}').indented()
        for prop in filter(lambda p: p.supports_expressions and not p.mode.is_template, props):
            result += Text(prop.expression_resolving_method_declaration(SwiftAccessLevel.INTERNAL)).indented()
        result += '}'
        return result

    def __main_declaration_header(self, entity: SwiftEntity) -> str:
        protocols = list(filter(None, [entity.protocol_plus_super_entities,
                                       swift_utils.implemented_swift_protocol(entity.generation_mode)]))
        conformance = '' if not protocols else f': {", ".join(protocols)}'
        access_modifier = self._access_level.value
        return f'{access_modifier}final class {utils.capitalize_camel_case(entity.name)}{conformance} {{'

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        entity_enumeration = cast(SwiftEntityEnumeration, entity_enumeration)
        entity_enumeration.__class__ = SwiftEntityEnumeration
        access_modifier = self._access_level.value
        name = utils.capitalize_camel_case(entity_enumeration.name)
        protocol = ''
        if entity_enumeration.mode.is_template:
            protocol = ': TemplateValue'
        header = f'@frozen\n{access_modifier}enum {name}{protocol} {{'
        result = Text(header)

        for name in entity_enumeration.entity_names:
            result += Text(f'case {utils.lower_camel_case(name)}({utils.capitalize_camel_case(name)})').indented()
        result += EMPTY
        if entity_enumeration.mode.is_template:
            value_type = 'Any'
        else:
            value_type = entity_enumeration.common_interface(GeneratedLanguage.SWIFT)
            if value_type is None:
                value_type = 'Any'
        result += f'  {access_modifier}var value: {value_type} {{'
        result += '    switch self {'
        for name in entity_enumeration.entity_names:
            result += f'    case let .{utils.lower_camel_case(name)}(value):'
            result += '      return value'
        result += '    }'
        result += '  }'

        if entity_enumeration.mode.is_template:
            result += EMPTY
            result += entity_enumeration.resolve_parent_implementation(self._access_level).indented()
            result += EMPTY
            result += entity_enumeration.resolve_value_implementation(self._access_level).indented()
            result += EMPTY
            result += entity_enumeration.resolve_unknown_value_implementation.indented()
        else:
            id_properties = list(filter(None, map(lambda x: _string_id_property(x[1]), entity_enumeration.entities)))
            if len(id_properties) == len(entity_enumeration.entities):
                id_optionality = '?' if any(p.optional for p in id_properties) else ''
                result += EMPTY
                result += f'  {access_modifier}var id: String{id_optionality} {{'
                result += '    switch self {'
                for name in entity_enumeration.entity_names:
                    result += f'    case let .{utils.lower_camel_case(name)}(value):'
                    result += '      return value.id'
                result += '    }'
                result += '  }'

        result += '}'
        return result

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        access_modifier = self._access_level.value
        formatted_name = utils.capitalize_camel_case(string_enumeration.name)
        if string_enumeration.parent is not None and string_enumeration.parent.generation_mode.is_template:
            prefix = string_enumeration.resolved_declaration_prefix
            return Text(f'{access_modifier}typealias {formatted_name} = {prefix}{formatted_name}')
        result = Text(f'@frozen\n{access_modifier}enum {formatted_name}: String, CaseIterable {{')
        for case in string_enumeration.cases:
            case_name = swift_utils.fixing_keywords(utils.fixing_first_digit(utils.lower_camel_case(case[0])))
            result += f'  case {case_name} = "{case[1]}"'
        result += '}'
        return result
