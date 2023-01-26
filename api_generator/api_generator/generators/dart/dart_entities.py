from __future__ import annotations
from typing import List, Optional, cast
from ... import utils
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
)
from ...schema.modeling.text import Text, EMPTY


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


def _dart_full_name(obj: Declarable) -> str:
    full_name = utils.snake_case(obj.name)
    if obj.parent is not None:
        full_name = utils.snake_case(obj.parent.name) + full_name
    return full_name


def _make_dart_imports(items: List[str]) -> Text:
    if not items:
        return EMPTY
    result = Text()
    for item in items:
        result += f'import \'./{utils.snake_case(item)}.dart\';'
    return result


dart_keywords = [
    'default',
    # ToDo(man-y): Dart has a strange system of allowed variable names that needs to be sorted out.
    # Example:
    #   void main() {
    #       int double = 1;
    #       print(double); it works!
    #       double int = 0.0;
    #       print(int);  //  it works too!
    #       int int = 1;
    #       print(int); it not works!!!
    #   }
    # While there is an exact certainty that it is impossible to use only the 'default'.
    # 'abstract', 'else', 'import', 'show',
    # 'as', 'enum', 'in', 'static',
    # 'assert', 'export', 'interface', 'super',
    # 'async', 'extends', 'is', 'switch',
    # 'await', 'extension', 'late', 'sync',
    # 'break', 'external', 'library', 'this',
    # 'case', 'factory', 'mixin', 'throw',
    # 'catch', 'false', 'new', 'true',
    # 'class', 'final', 'null', 'try',
    # 'const', 'finally', 'on', 'typedef',
    # 'continue', 'for', 'operator', 'var',
    # 'covariant', 'Function', 'part', 'void',
    # 'default', 'get', 'required', 'while',
    # 'deferred', 'hide', 'rethrow', 'with',
    # 'do', 'if', 'return', 'yield',
    # 'dynamic', 'implements', 'set',
    # 'int', 'double', 'bool',
]


def allowed_name(name: str) -> str:
    if name in dart_keywords:
        return f'{name}_'
    else:
        return name


class DartEntity(Entity):
    def update_base(self):
        for prop in self.properties:
            prop.__class__ = DartProperty
            cast(DartProperty, prop).update_base()

    @property
    def referenced_top_level_types(self) -> List[str]:
        return list(filter(None, map(lambda p: p.property_type_dart.referenced_top_level_type_name, self.properties)))

    @property
    def imports(self) -> Optional[Text]:
        types = self.referenced_top_level_types
        for inner_type in self.inner_types:
            if isinstance(inner_type, DartEntity):
                types += inner_type.referenced_top_level_types
        dart_full_name = _dart_full_name(self)
        unique_types = list(filter(lambda t: t != dart_full_name, set(types)))
        return None if not unique_types else _make_dart_imports(sorted(unique_types))

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

    pass


class DartPropertyType(PropertyType):
    @property
    def referenced_top_level_type_name(self) -> Optional[str]:
        if isinstance(self, Object):
            obj = self.object
            if obj is not None and obj.parent is None:
                return _dart_full_name(obj)
            return None
        elif isinstance(self, DartArray):
            return self.property_type_dart.referenced_top_level_type_name
        return None

    def declaration_by_prefixed(self) -> str:
        if isinstance(self, (Int, Color)):
            return 'int'
        elif isinstance(self, Double):
            return 'double'
        elif isinstance(self, (Bool, BoolInt)):
            return 'bool'
        elif isinstance(self, String):
            return 'String'
        elif isinstance(self, Dictionary):
            return 'Map<String, dynamic>'
        elif isinstance(self, StaticString):
            return 'String'
        elif isinstance(self, Url):
            return 'Uri'
        elif isinstance(self, Array):
            item_type = cast(DartPropertyType, self.property_type)
            item_decl = item_type.declaration_by_prefixed()
            return f'List<{item_decl}>'
        elif isinstance(self, Object):
            if self.name.startswith('$predefined_'):
                return self.name.replace('$predefined_', '')

            return self.object.resolved_prefixed_declaration.replace('.', '')

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
