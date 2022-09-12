from typing import List, cast, Optional

from ... import utils
from ...schema.modeling.entities import (
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
    Declarable
)
from ...schema.modeling.text import Text, EMPTY


def _as_type_script_commentary_content(s: str) -> Text:
    result = Text()
    words = s.replace('<li>', '').replace('</li>', '').split(' ')
    line = ''
    for word in words:
        if not line:
            line = str(word)
        elif (len(line) + len(word)) > 93:
            result += f' * {line}'
            line = str(word)
        else:
            line += f' {word}'
    result += f' * {line}'
    return result


def _as_type_script_commentary(s: str) -> Text:
    if not s:
        return EMPTY
    return Text('/**') + _as_type_script_commentary_content(s) + Text(' */')


def _type_script_full_name(d: Declarable) -> str:
    full_name = utils.capitalize_camel_case(d.name)
    if d.parent is not None:
        full_name = utils.capitalize_camel_case(d.parent.name) + full_name
    if isinstance(d, Entity):
        d.__class__ = TypeScriptEntity
        if not cast(TypeScriptEntity, d).should_generate_as_type_script_class:
            full_name = f'I{full_name}'
    return full_name


def _type_script_type_name(property_type: PropertyType, supports_expressions: bool) -> str:
    if isinstance(property_type, (Int, Double)):
        return 'number'
    elif isinstance(property_type, Bool):
        return 'boolean'
    elif isinstance(property_type, BoolInt):
        return 'IntBoolean'
    elif isinstance(property_type, (Color, String, Url)):
        return 'string'
    elif isinstance(property_type, StaticString):
        raise TypeError('Is a static type')
    elif isinstance(property_type, Dictionary):
        return '{}'
    elif isinstance(property_type, Array):
        item_type = property_type.property_type
        array_type_name = _type_script_type_name(item_type, supports_expressions)
        if supports_expressions and not isinstance(item_type, Array):
            array_type_name += ' | DivExpression'
        return f'NonEmptyArray<{array_type_name}>'
    elif isinstance(property_type, Object):
        if property_type.object is None:
            raise ValueError(f'Invalid {property_type}')
        return _type_script_full_name(property_type.object)
    else:
        raise NotImplementedError


def _referenced_top_level_type_name(property_type: PropertyType) -> Optional[str]:
    if isinstance(property_type, Object):
        obj = property_type.object
        if obj is not None and obj.parent is None:
            return _type_script_full_name(obj)
        return None
    elif isinstance(property_type, Array):
        return _referenced_top_level_type_name(property_type.property_type)
    return None


def _make_type_script_imports(items: List[str]) -> Text:
    if not items:
        return EMPTY
    result = Text()
    result += 'import {'
    for item in items:
        result += f'    {item},'
    result += "} from './';"
    result += EMPTY
    return result


class TypeScriptEntity(Entity):
    @property
    def static_properties(self) -> List[Property]:
        return list(filter(lambda p: isinstance(p.property_type, StaticString), self.properties))

    @property
    def should_generate_as_type_script_class(self) -> bool:
        return len(self.static_properties) != 0

    @property
    def props_interface_name(self) -> str:
        return f'{_type_script_full_name(self)}Props'

    @property
    def referenced_top_level_types(self) -> List[str]:
        return list(filter(None, map(lambda p: _referenced_top_level_type_name(p.property_type), self.properties)))

    @property
    def imports(self) -> Optional[Text]:
        types = self.referenced_top_level_types
        for inner_type in self.inner_types:
            if isinstance(inner_type, Entity):
                inner_type.__class__ = TypeScriptEntity
                types += cast(TypeScriptEntity, inner_type).referenced_top_level_types
        type_script_full_name = _type_script_full_name(self)
        unique_types = list(filter(lambda t: t != type_script_full_name, set(types)))
        return None if not unique_types else _make_type_script_imports(sorted(unique_types))

    @property
    def class_declaration(self) -> Text:
        result = Text()
        commentary = _as_type_script_commentary(self.description_doc())
        if commentary != EMPTY:
            result += commentary
        prop_name = self.props_interface_name
        result += f'export class {_type_script_full_name(self)}<T extends {prop_name} = {prop_name}> {{'
        result += f'    readonly _props?: Exact<{prop_name}, T>;'

        class_properties_declaration = self.class_properties_declaration
        if class_properties_declaration.lines:
            result += EMPTY
            result += class_properties_declaration

        constructor_declaration = self.constructor_declaration
        if constructor_declaration.lines:
            result += EMPTY
            result += constructor_declaration
        result += '}'
        result += EMPTY
        result += f'export interface {prop_name} {{'
        dynamic_properties_declaration = self.dynamic_properties_declaration
        if dynamic_properties_declaration.lines:
            result += dynamic_properties_declaration
        result += '}'
        return result

    @property
    def class_properties_declaration(self) -> Text:
        result = Text()
        for prop in self.static_properties:
            prop.__class__ = TypeScriptProperty
            prop = cast(TypeScriptProperty, prop)
            commentary = prop.commentary
            if commentary is not None:
                result += commentary.indented(indent_width=4)
            result += f'    readonly {prop.escaped_name} = {prop.static_value};'
        dynamic_properties_declaration = self.dynamic_properties_declaration
        if dynamic_properties_declaration.lines:
            result += dynamic_properties_declaration
        return result

    @property
    def constructor_declaration(self) -> Text:
        optional_marker = '' if any(not p.optional for p in self.instance_properties) else '?'
        result = Text(f'    constructor(props{optional_marker}: Exact<{self.props_interface_name}, T>) {{')
        for prop in self.instance_properties:
            result += f'        this.{prop.name} = props{optional_marker}.{prop.name};'
        result += '    }'
        return result

    @property
    def dynamic_properties_declaration(self) -> Text:
        result = Text()
        for prop in self.instance_properties:
            prop.__class__ = TypeScriptProperty
            prop = cast(TypeScriptProperty, prop)
            commentary = prop.commentary
            if commentary is not None:
                result += commentary.indented(indent_width=4)
            result += f'    {prop.make_declaration(is_templatable=self._type_script_templatable)};'
        return result

    @property
    def interface_declaration(self) -> Text:
        result = Text()
        commentary = _as_type_script_commentary(self.description_doc())
        if commentary != EMPTY:
            result += commentary
        result += f'export interface {_type_script_full_name(self)} {{'
        result += self.dynamic_properties_declaration
        result += '}'
        return result


class TypeScriptProperty(Property):
    @property
    def commentary(self) -> Optional[Text]:
        result = Text()
        if self.description_doc() is not None:
            result += _as_type_script_commentary_content(self.description_doc())

        if self.is_deprecated:
            if result.lines:
                result += ' *'
            result += ' * @deprecated'

        if result.lines:
            result = Text('/**') + result + Text(' */')
            return result

        return None

    @property
    def escaped_name(self) -> str:
        return self.name if '-' not in self.name else f"'{self.name}'"

    @property
    def static_value(self) -> str:
        if isinstance(self.property_type, StaticString):
            return f"'{self.property_type.value}'"
        else:
            raise TypeError(f'{self.property_type.__class__} is not a static type')

    def make_declaration(self, is_templatable: bool) -> str:
        optional_mark = '?' if self.optional else ''
        type_name = _type_script_type_name(self.property_type, self.supports_expressions)
        type_decl = f'Type<{type_name}>' if is_templatable else type_name
        if self.supports_expressions and not isinstance(self.property_type, Array):
            type_decl += ' | DivExpression'
        return f'{self.escaped_name}{optional_mark}: {type_decl}'
