from __future__ import annotations
from typing import List, Optional, cast

from ...schema.modeling.entities import (
    Declarable,
    PropertyType,
    Property,
    Entity,
    Array,
    Bool,
    BoolInt,
    Color,
    Dictionary,
    RawArray,
    Double,
    Int,
    Object,
    StaticString,
    String,
    StringEnumeration,
    Url,
)
from ... import utils
from ...schema.modeling.text import Text, EMPTY


def update_base(obj: Declarable) -> Declarable:
    if isinstance(obj, Entity):
        obj.__class__ = PythonEntity
        cast(PythonEntity, obj).update_base()
    return obj


def update_property_type_base(property_type: PropertyType):
    if isinstance(property_type, Array):
        property_type.__class__ = PythonArray
        cast(PythonArray, property_type).update_base()
    elif isinstance(property_type, Bool):
        property_type.__class__ = PythonBool
    elif isinstance(property_type, BoolInt):
        property_type.__class__ = PythonBoolInt
    elif isinstance(property_type, Color):
        property_type.__class__ = PythonColor
    elif isinstance(property_type, Dictionary):
        property_type.__class__ = PythonDictionary
    elif isinstance(property_type, RawArray):
        property_type.__class__ = PythonRawArray
    elif isinstance(property_type, Double):
        property_type.__class__ = PythonDouble
    elif isinstance(property_type, Int):
        property_type.__class__ = PythonInt
    elif isinstance(property_type, Object):
        property_type.__class__ = PythonObject
        cast(PythonObject, property_type).update_base()
    elif isinstance(property_type, StaticString):
        property_type.__class__ = PythonStaticString
    elif isinstance(property_type, String):
        property_type.__class__ = PythonString
    elif isinstance(property_type, Url):
        property_type.__class__ = PythonUrl
    else:
        raise NotImplementedError


def _python_full_name(obj: Declarable) -> str:
    full_name = utils.capitalize_camel_case(obj.name)
    if obj.parent is not None:
        full_name = utils.capitalize_camel_case(obj.parent.name) + full_name
    return full_name


def _make_python_imports(items: List[str]) -> Text:
    if not items:
        return EMPTY
    result = Text()
    for item in items:
        result += f'from . import {utils.snake_case(item)}'
    return result


def as_python_commentary(s: str) -> Optional[Text]:
    result = Text()
    words = s.replace('<li>', '').replace('</li>', '').split(' ')
    line = ''
    for word in words:
        if not line:
            line = str(word)
        elif (len(line) + len(word)) > 80:
            result += f'# {line}'
            line = str(word)
        else:
            line += f' {word}'
    result += f'# {line}'
    return result


class PythonEntity(Entity):
    def update_base(self):
        for prop in self.properties:
            prop.__class__ = PythonProperty
            cast(PythonProperty, prop).update_base()

    @property
    def static_properties(self) -> List[Property]:
        return list(filter(lambda p: isinstance(p.property_type, StaticString), self.properties))

    @property
    def referenced_top_level_types(self) -> List[str]:
        return list(filter(None, map(lambda p: p.property_type_py.referenced_top_level_type_name, self.properties)))

    @property
    def imports(self) -> Optional[Text]:
        types = self.referenced_top_level_types
        for inner_type in self.inner_types:
            if isinstance(inner_type, PythonEntity):
                types += inner_type.referenced_top_level_types
        python_full_name = _python_full_name(self)
        unique_types = list(filter(lambda t: t != python_full_name, set(types)))
        return None if not unique_types else _make_python_imports(sorted(unique_types))

    def generate_init(self, filename: str) -> Text:
        main_required = ['self', '*']
        required = []
        static_properties = self.static_properties
        instance_properties = self.instance_properties
        optional = list(map(lambda p: f'{p.escaped_name}: str = {p.static_value}', static_properties))
        for p in cast(List[PythonProperty], instance_properties):
            optional.append(f'{p.escaped_name}: typing.Optional[{p.property_type_py.final_type(filename)}] = None')
        result = Text()
        result += 'def __init__('
        result += Text(', '.join(main_required) + ',').indented(indent_width=4)
        for p in required + optional:
            result += Text(p + ',').indented(indent_width=4)
        result += Text('**kwargs: typing.Any,').indented(indent_width=4)
        result += '):'
        super_init = Text('super().__init__(')
        for p in cast(List[PythonProperty], static_properties + instance_properties):
            escaped_name = p.escaped_name
            super_init += Text(f'{escaped_name}={escaped_name},').indented(indent_width=4)
        super_init += Text('**kwargs,').indented(indent_width=4)
        super_init += ')'
        result += super_init.indented(indent_width=4)
        return result

    @property
    def should_generate_as_python_class(self) -> bool:
        return len(self.static_properties) != 0

    @property
    def class_properties_declaration(self) -> Text:
        result = Text()
        for p in cast(List[PythonProperty], self.static_properties):
            field = f'default={p.static_value}'
            commentary = p.commentary
            if commentary is not None:
                commentary = ' '.join(commentary.lines).replace('\n', '').replace('# ', '').replace("'", "\\'")
                commentary = utils.python_long_sting_split(commentary)
                indent = len(field) + 4
                commentary = utils.indent(
                    commentary, indent=indent, indent_first_line=False
                )
                field += f", description={commentary}"

            result += Text(f'{p.escaped_name}: str = Field({field})').indented(indent_width=4)
        dynamic_declaration = self.dynamic_properties_declaration
        if dynamic_declaration.lines:
            result += dynamic_declaration
        return result

    @property
    def dynamic_properties_declaration(self) -> Text:
        result = Text()
        py_filename = _python_full_name(self)
        props = cast(List[PythonProperty], self.instance_properties)
        for ind, p in enumerate(props):
            result += p.make_declaration(filename=py_filename).indented(indent_width=4)
        return result


class PythonProperty(Property):
    def update_base(self):
        update_property_type_base(self.property_type)

    @property
    def escaped_name(self) -> str:
        return utils.snake_case(self.name)

    @property
    def property_type_py(self) -> PythonPropertyType:
        return cast(PythonPropertyType, self.property_type)

    def constructor_required_value(self, filename: str) -> Optional[str]:
        if self.optional:
            return None
        return f'{self.escaped_name}: {self.property_type_py.final_type(filename)}'

    def constructor_optional_value(self, filename: str) -> Optional[str]:
        if not self.optional:
            return None
        return f'{self.escaped_name}: typing.Optional[{self.property_type_py.final_type(filename)}] = None'

    @property
    def static_value(self) -> str:
        if isinstance(self.property_type, StaticString):
            return f"'{self.property_type.value}'"
        else:
            raise TypeError(f'{self.property_type} is not a static type')

    @property
    def commentary(self) -> Optional[Text]:
        result = Text()
        description = self.description_doc()
        if description:
            result += as_python_commentary(description)
        if self.is_deprecated:
            result += '@deprecated'
        if result.lines:
            return result
        return None

    def make_declaration(self, filename: str) -> Text:
        fields: List[str] = []

        constraints = self.property_type_py.constraints
        if constraints:
            fields.append(constraints)

        commentary = self.commentary
        if commentary is not None:
            commentary = ' '.join(commentary.lines).replace('\n', '').replace('# ', '').replace("'", "\\'")
            commentary = utils.python_long_sting_split(commentary)
            commentary = utils.indent(commentary, indent=4, indent_first_line=False, indent_last_line=False)
            fields.append(f"description={commentary}")

        if utils.snake_case(self.name) != self.name:
            fields.append(f"name='{self.name}'")

        if self.optional:
            prefix = 'typing.Optional['
            suffix = ']'
        else:
            prefix = ''
            suffix = ''
        type_decl = f'{prefix}{self.property_type_py.final_type(filename)}{suffix}'

        fields_text = utils.indent(", \n".join(fields), indent=4)
        return Text(f'{self.escaped_name}: {type_decl} = Field(\n{fields_text})')


class PythonPropertyType(PropertyType):
    @property
    def referenced_top_level_type_name(self) -> Optional[str]:
        if isinstance(self, Object):
            obj = self.object
            if obj is not None and obj.parent is None:
                return _python_full_name(obj)
            return None
        elif isinstance(self, PythonArray):
            return self.property_type_py.referenced_top_level_type_name
        return None

    def is_expr(self) -> bool:
        if isinstance(self, (Int, Double)):
            return True
        if isinstance(self, (Bool, BoolInt)):
            return True
        if isinstance(self, (Color, String, Url)):
            return True
        if isinstance(self, Object):
            if isinstance(self.object, StringEnumeration):
                return True
        return False

    def final_type(self, filename: str) -> str:
        typename = self.type_name(filename)
        if self.is_expr():
            return f"typing.Union[Expr, {typename}]"
        return typename

    def type_name(self, filename: str) -> str:
        if isinstance(self, Int):
            return 'int'
        elif isinstance(self, Double):
            return 'float'
        elif isinstance(self, (Bool, BoolInt)):
            return 'bool'
        elif isinstance(self, (Color, String, Url)):
            return 'str'
        elif isinstance(self, StaticString):
            raise TypeError(f'{self} is a static type')
        elif isinstance(self, Dictionary):
            return 'typing.Dict[str, typing.Any]'
        elif isinstance(self, RawArray):
            return 'typing.Sequence[typing.Any]'
        elif isinstance(self, PythonArray):
            return f'typing.Sequence[{self.property_type_py.final_type(filename)}]'
        elif isinstance(self, Object):
            if self.object is None:
                raise ValueError(f'Invalid {self}')
            obj = self.object
            if utils.snake_case(filename) == utils.snake_case(_python_full_name(obj)) or \
                    obj.parent is not None:
                return _python_full_name(obj)
            py_full_name = _python_full_name(obj)
            return f'{utils.snake_case(py_full_name)}.{py_full_name}'

    @property
    def constraints(self) -> str:
        if isinstance(self, (Bool, BoolInt, Dictionary, RawArray)):
            return ''
        elif isinstance(self, Array):
            result = ''
            if self.min_items > 0:
                result += f'min_items={self.min_items}'
            return result
        elif isinstance(self, Color):
            return 'format="color"'
        elif isinstance(self, (Double, Int)):
            return ''
        elif isinstance(self, Object):
            return ''
        elif isinstance(self, StaticString):
            return ''
        elif isinstance(self, Url):
            return 'format="uri"'
        elif isinstance(self, String):
            result = ''
            if self.min_length > 0:
                result += f'min_length={self.min_length}'
            return result
        else:
            raise TypeError


class PythonArray(PythonPropertyType, Array):
    def update_base(self):
        if not isinstance(self.property_type, PythonPropertyType):
            update_property_type_base(self.property_type)

    @property
    def property_type_py(self) -> PythonPropertyType:
        return cast(PythonPropertyType, self.property_type)


class PythonBool(PythonPropertyType, Bool):
    pass


class PythonBoolInt(PythonPropertyType, BoolInt):
    pass


class PythonColor(PythonPropertyType, Color):
    pass


class PythonDictionary(PythonPropertyType, Dictionary):
    pass


class PythonRawArray(PythonPropertyType, RawArray):
    pass


class PythonDouble(PythonPropertyType, Double):
    pass


class PythonInt(PythonPropertyType, Int):
    pass


class PythonObject(PythonPropertyType, Object):
    def update_base(self):
        if isinstance(self.object, Entity):
            self.object.__class__ = PythonEntity
            cast(PythonEntity, self.object).update_base()


class PythonStaticString(PythonPropertyType, StaticString):
    pass


class PythonString(PythonPropertyType, String):
    pass


class PythonUrl(PythonPropertyType, Url):
    pass
