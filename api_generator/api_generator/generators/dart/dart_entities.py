from __future__ import annotations
from typing import List, Optional, cast

from ..base import declaration_comment
from ... import utils
from .utils import allowed_name, get_full_name, make_imports
from ...schema.modeling.entities import (
    Declarable,
    Entity,
    Property,
    PropertyType,
    Int,
    Bool,
    BoolInt,
    Double,
    StaticString,
    Object,
    Array,
    Url,
    Color,
    String,
    Dictionary,
    RawArray,
    StringEnumeration,
    EntityEnumeration,
    ObjectFormat,
)


def update_base(obj: Declarable) -> Declarable:
    if isinstance(obj, Entity):
        obj.__class__ = DartEntity
        cast(DartEntity, obj).update_base()
    return obj


def update_property_type_base(property_type: PropertyType):
    if isinstance(property_type, Array):
        property_type.__class__ = DartArray
        cast(DartArray, property_type).update_base()
    elif isinstance(property_type, Bool):
        property_type.__class__ = DartBool
    elif isinstance(property_type, BoolInt):
        property_type.__class__ = DartBoolInt
    elif isinstance(property_type, Color):
        property_type.__class__ = DartColor
    elif isinstance(property_type, Dictionary):
        property_type.__class__ = DartDictionary
    elif isinstance(property_type, RawArray):
        property_type.__class__ = DartRawArray
    elif isinstance(property_type, Double):
        property_type.__class__ = DartDouble
    elif isinstance(property_type, Int):
        property_type.__class__ = DartInt
    elif isinstance(property_type, Object):
        property_type.__class__ = DartObject
        cast(DartObject, property_type).update_base()
    elif isinstance(property_type, StaticString):
        property_type.__class__ = DartStaticString
    elif isinstance(property_type, String):
        property_type.__class__ = DartString
    elif isinstance(property_type, Url):
        property_type.__class__ = DartUrl
    else:
        raise NotImplementedError


class DartEntity(Entity):
    def update_base(self):
        for prop in self.properties:
            prop.__class__ = DartProperty
            cast(DartProperty, prop).update_base()

    @property
    def referenced_top_level_types(self) -> List[str]:
        return list(filter(None, map(lambda p: p.property_type_dart.referenced_top_level_type_name, self.properties)))

    @property
    def imports(self) -> List[str]:
        types = self.referenced_top_level_types
        for inner_type in self.inner_types:
            if isinstance(inner_type, DartEntity):
                types += inner_type.referenced_top_level_types
        full_name = get_full_name(self)
        unique_types = list(filter(lambda t: t != full_name, set(types)))
        return [] if not unique_types else make_imports(sorted(unique_types))

    @property
    def import_parsing_utils(self) -> List[str]:
        # ToDo(man-y): Follow the import execution logic if necessary.
        # if any(isinstance(item, (Color, Int, Bool, BoolInt, Double, Url)) for item in
        #        [p.property_type for p in self.instance_properties]):
        return ["import '../utils/parsing_utils.dart';"]
        # else:
        #     return []

    @property
    def is_main_declaration(self) -> bool:
        return self.parent is None

    @property
    def has_super(self) -> bool:
        return self._implemented_protocol is not None

    @property
    def super_entity(self) -> DartEntity:
        return cast(DartEntity, self._implemented_protocol)

    @property
    def props(self) -> List[DartProperty]:
        self.update_base()
        return cast(List[DartProperty], self.properties)

    @property
    def static_properties(self) -> List[DartProperty]:
        return list(filter(lambda p: isinstance(p.property_type, StaticString), self.props))

    @property
    def instance_properties(self) -> List[DartProperty]:
        return list(filter(lambda p: not isinstance(p.property_type, StaticString), self.props))


pass


class DartProperty(Property):
    def update_base(self):
        update_property_type_base(self.property_type)

    @property
    def property_type_dart(self) -> DartPropertyType:
        return cast(DartPropertyType, self.property_type)

    @property
    def declaration_name(self) -> str:
        if isinstance(self.property_type, StaticString):
            name = utils.constant_upper_case(self.name)
        else:
            name = utils.lower_camel_case(self.name)
        return utils.fixing_first_digit(name)

    @property
    def has_default(self) -> bool:
        prop_type = cast(DartPropertyType, self.property_type)
        return self.default_value is not None or prop_type.empty_constructor is not None

    @property
    def type_declaration(self) -> str:
        optionality = '?' if self.optional and not self.has_default else ''
        declaration = cast(DartPropertyType, self.property_type).declaration()
        if self.supports_expressions:
            declaration = f"Expression<{declaration}>"
        return declaration + optionality

    @property
    def get_default_import(self) -> Optional[str]:
        prop = self.property_type_dart
        default = self.default_value
        if default is not None and isinstance(prop, Object):
            if prop.object is None:
                return None
            if isinstance(prop.object, EntityEnumeration):
                default_value_dict = utils.json_dict(default)
                type_val = default_value_dict.get('type')
                enum_case = None
                for case in prop.object.entities:
                    ent = case[1]
                    if isinstance(ent, Entity) and ent.static_type == type_val:
                        enum_case = case
                if enum_case is None:
                    raise ValueError(type_val)
                return utils.capitalize_camel_case(enum_case[0])

    # ToDo(man-y): Transfer the parsing strategy to the subtypes themselves.
    def get_parse_strategy(self) -> str:
        prop_type = cast(DartPropertyType, self.property_type)
        prop_type_decl = prop_type.declaration()
        required_cast = '' if self.optional or self.has_default else '!'

        expr = 'Expr' if self.supports_expressions else ''
        fallback = f', fallback: {self.fallback_declaration}' if self.supports_expressions and self.has_default else ''

        if isinstance(prop_type, Int):
            return f"safeParseInt{expr}(json['{self.name}']{fallback}){required_cast}"
        elif isinstance(prop_type, Color):
            return f"safeParseColor{expr}(json['{self.name}']{fallback}){required_cast}"
        elif isinstance(prop_type, Double):
            return f"safeParseDouble{expr}(json['{self.name}']{fallback}){required_cast}"
        elif isinstance(prop_type, (Bool, BoolInt)):
            return f"safeParseBool{expr}(json['{self.name}']{fallback}){required_cast}"
        elif isinstance(prop_type, (String, StaticString)):
            return f"safeParseStr{expr}(json['{self.name}']?.toString(){fallback}){required_cast}"
        elif isinstance(prop_type, Dictionary):
            return f"safeParseClass{expr}(json{fallback}){required_cast}"
        elif isinstance(prop_type, RawArray):
            return f"safeParseClass{expr}(json['{self.name}']{fallback}){required_cast}"
        elif isinstance(prop_type, Url):
            return f"safeParseUri{expr}(json['{self.name}']){required_cast}"
        elif prop_type.is_class():
            return f"safeParseClass{expr}({prop_type_decl}.fromJson(json['{self.name}']){fallback}){required_cast}"
        elif prop_type.is_list():
            inner_item_type = prop_type.get_list_inner_class()
            if inner_item_type.is_class():
                if inner_item_type.is_string_enumeration():
                    return f"safeParseClass{expr}((json['{self.name}'] as List<dynamic>{'?' if self.optional or self.has_default else ''})" \
                           f"{'?' if self.optional or self.has_default else ''}.map((s) => {prop_type_decl[5:-1]}" \
                           f".fromJson(s)!).toList(){fallback}){'' if self.optional or self.has_default else '!'}"
                else:
                    return f"safeParseClass{expr}((json['{self.name}'] as List<dynamic>{'?' if self.optional or self.has_default else ''})" \
                           f"{'?' if self.optional or self.has_default else ''}.map((j) => {prop_type_decl[5:-1]}." \
                           f"fromJson(j as Map <String, dynamic>)!{fallback}).toList()){'' if self.optional or self.has_default else '!'}"
            else:
                return f"safeParseClass{expr}((json['{self.name}'] as List<dynamic>{'?' if self.optional or self.has_default else ''})" \
                       f"{'?' if self.optional or self.has_default else ''}.map((v) => (v as {prop_type_decl[5:-1]}))" \
                       f".toList(){fallback}){'' if self.optional or self.has_default else '!'}"

    @property
    def fallback_declaration(self) -> str:
        prop_type = cast(DartPropertyType, self.property_type)
        empty_dict_deserialization = prop_type.empty_constructor
        if self.default_value is not None:
            return prop_type.internal_declaration(self.default_value)
        elif empty_dict_deserialization is not None:
            return empty_dict_deserialization
        else:
            return ''

    def add_default_value_to(self, declaration: str, in_constructor=True) -> str:
        prop_type = cast(DartPropertyType, self.property_type)
        empty_dict_deserialization = prop_type.empty_constructor
        if self.default_value is not None:
            default_value_declaration_to_use = prop_type.internal_declaration(self.default_value)
        elif empty_dict_deserialization is not None:
            default_value_declaration_to_use = empty_dict_deserialization
        else:
            return declaration

        if self.supports_expressions:
            default_value_declaration_to_use = f'const Expression.value({default_value_declaration_to_use})'

        if in_constructor:
            default_value_declaration_to_use = f'{declaration} = {default_value_declaration_to_use}'
        else:
            default_value_declaration_to_use = f'{declaration} ?? {default_value_declaration_to_use}'

        return default_value_declaration_to_use

    @property
    def comment(self) -> str:
        return declaration_comment(self, lambda p: cast(str, self.property_type_dart.internal_declaration(
            self.default_value)))

    pass


class DartPropertyType(PropertyType):
    @property
    def empty_constructor(self) -> Optional[str]:
        if isinstance(self, Object) and isinstance(self.object, Entity) and \
                self.object.all_properties_are_optional_except_default_values:
            return f'const {get_full_name(self.object)}()'
        return None

    @property
    def referenced_top_level_type_name(self) -> Optional[str]:
        if isinstance(self, Object):
            obj = self.object
            if obj is not None and obj.parent is None:
                return get_full_name(obj)
            return None
        elif isinstance(self, DartArray):
            return self.property_type_dart.referenced_top_level_type_name
        return None

    def is_string_enumeration(self):
        return isinstance(self, Object) and isinstance(self.object, StringEnumeration)

    def is_class(self):
        return isinstance(self, (Object, DartObject))

    def get_list_inner_class(self) -> DartPropertyType:
        if isinstance(self, (Array, DartArray)):
            return cast(DartPropertyType, self.property_type)

    def is_list(self):
        return isinstance(self, (Array, DartArray))

    def declaration(self) -> str:
        if isinstance(self, (Int, Color)):
            return 'int'
        elif isinstance(self, Double):
            return 'double'
        elif isinstance(self, (Bool, BoolInt)):
            return 'bool'
        elif isinstance(self, (String, StaticString)):
            return 'String'
        elif isinstance(self, Dictionary):
            return 'Map<String, dynamic>'
        elif isinstance(self, RawArray):
            return 'List<dynamic>'
        elif isinstance(self, Url):
            return 'Uri'
        elif isinstance(self, Array):
            item_type = cast(DartPropertyType, self.property_type)
            item_decl = item_type.declaration()
            return f'List<{item_decl}>'
        elif isinstance(self, Object):
            if self.name.startswith('$predefined_'):
                return self.name.replace('$predefined_', '')

            return self.object.resolved_prefixed_declaration.replace('.', '')

    def internal_declaration(self, default_value: str) -> Optional[str]:
        if isinstance(self, (Int, Bool, BoolInt, Double)):
            return default_value
        elif isinstance(self, String):
            value_with_escaping_quotes = default_value.replace('"', '\"')
            return f'"{value_with_escaping_quotes}"'
        elif isinstance(self, Url):
            return f'const Uri.parse("{default_value}")'
        elif isinstance(self, Color):
            color_value = default_value[1::].upper()
            if len(color_value) == 3:
                joined = ''.join(c + c for c in color_value)
                color_argb_hex = f'FF{joined}'
            elif len(color_value) == 4:
                color_argb_hex = ''.join(c + c for c in color_value)
            elif len(color_value) == 6:
                color_argb_hex = f'FF{color_value}'
            elif len(color_value) == 8:
                color_argb_hex = color_value
            else:
                raise ValueError
            return f'0x{color_argb_hex}'
        elif isinstance(self, Array):
            without_whitespaces = default_value.replace(' ', '').replace('\n', '')
            if not without_whitespaces.startswith('[') or not without_whitespaces.endswith(']'):
                return None
            if without_whitespaces == '[]':
                values = []
            else:
                values = without_whitespaces[1:-1].split(',')
            item_type = cast(DartPropertyType, self.property_type)
            declarations = list(filter(None, map(lambda value: item_type.internal_declaration(value), values)))
            if len(values) != len(declarations):
                return None
            joined = ', '.join(declarations)
            return f'[{joined}]'
        elif isinstance(self, Object):
            if self.object is None:
                return None

            if isinstance(self.object, StringEnumeration):
                enum_case = next((case for case in self.object.cases if case[1] == default_value), None)
                if enum_case is None:
                    raise ValueError(default_value)
                str_enum = cast(StringEnumeration, self.object)
                full_name = get_full_name(str_enum)

                return f'{full_name}.{allowed_name(utils.fixing_first_digit(utils.lower_camel_case(enum_case[0])))}'

            default_value_dict = utils.json_dict(default_value)
            if isinstance(self.object, EntityEnumeration):
                type_val = default_value_dict.get('type')
                enum_case = None
                for case in self.object.entities:
                    ent = case[1]
                    if isinstance(ent, Entity) and ent.static_type == type_val:
                        enum_case = case
                if enum_case is None:
                    raise ValueError(type_val)
                prop_type = cast(DartPropertyType,
                                 DartObject(name='', object=enum_case[1], format=ObjectFormat.DEFAULT))
                case_constructor: Optional[str] = prop_type.internal_declaration(default_value)
                if case_constructor is None:
                    return None

                return f'const {get_full_name(self.object)}({case_constructor})'

            if isinstance(self.object, DartEntity):
                entity = cast(DartEntity, self.object)
                entity.__class__ = DartEntity

                pref = ''
                prop_init = ''
                args = []
                for prop in entity.instance_properties:
                    str_type = default_value_dict.get(prop.dict_field)
                    if str_type is None:
                        continue
                    declaration = cast(DartPropertyType, prop.property_type).internal_declaration(str_type)

                    if prop.supports_expressions:
                        declaration = f'Expression.value({declaration})'

                    args.append(f'{prop.declaration_name}: {declaration}')

                if len(args) != 0:
                    prop_init = ', '.join(args)
                    prop_init += ','
                    pref = 'const '

                return f'{pref}{get_full_name(entity)}({prop_init})'
        else:
            return None

    pass


class DartArray(DartPropertyType, Array):
    def update_base(self):
        if not isinstance(self.property_type, DartPropertyType):
            update_property_type_base(self.property_type)

    @property
    def property_type_dart(self) -> DartPropertyType:
        return cast(DartPropertyType, self.property_type)


class DartBool(DartPropertyType, Bool):
    pass


class DartBoolInt(DartPropertyType, BoolInt):
    pass


class DartColor(DartPropertyType, Color):
    pass


class DartDictionary(DartPropertyType, Dictionary):
    pass


class DartRawArray(DartPropertyType, RawArray):
    pass


class DartDouble(DartPropertyType, Double):
    pass


class DartInt(DartPropertyType, Int):
    pass


class DartObject(DartPropertyType, Object):
    def update_base(self):
        if isinstance(self.object, Entity):
            self.object.__class__ = DartEntity
            cast(DartEntity, self.object).update_base()


class DartStaticString(DartPropertyType, StaticString):
    pass


class DartString(DartPropertyType, String):
    pass


class DartUrl(DartPropertyType, Url):
    pass
