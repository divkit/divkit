from __future__ import annotations
from typing import List, cast

from ...schema.modeling.entities import (
    Entity,
    EntityEnumeration,
    StringEnumeration,
    Declarable,
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
from ... import utils
from ...schema.modeling.text import Text


def update_base(obj: Declarable) -> Declarable:
    if isinstance(obj, Entity):
        obj.__class__ = DivanEntity
        cast(DivanEntity, obj).update_base()
    elif isinstance(obj, EntityEnumeration):
        obj.__class__ = DivanEntityEnumeration
        cast(DivanEntityEnumeration, obj).update_base()
    elif isinstance(obj, StringEnumeration):
        obj.__class__ = DivanStringEnumeration
    return obj


def comment(*lines) -> Text:
    comment_block = Text('/**')
    for line in lines:
        comment_block += f' * {line}'
    comment_block += ' */'
    return comment_block


def default_value_comment(value) -> Text:
    return comment(f'Значение по умолчанию: {value}')


def update_property_type_base(property_type: PropertyType):
    if isinstance(property_type, Array):
        property_type.__class__ = DivanArray
        property_type = cast(DivanArray, property_type)
        property_type.update_base()
    elif isinstance(property_type, Bool):
        property_type.__class__ = DivanBool
    elif isinstance(property_type, BoolInt):
        property_type.__class__ = DivanBoolInt
    elif isinstance(property_type, Color):
        property_type.__class__ = DivanColor
    elif isinstance(property_type, Dictionary):
        property_type.__class__ = DivanDictionary
    elif isinstance(property_type, Double):
        property_type.__class__ = DivanDouble
    elif isinstance(property_type, Int):
        property_type.__class__ = DivanInt
    elif isinstance(property_type, Object):
        property_type.__class__ = DivanObject
        property_type = cast(DivanObject, property_type)
        property_type.update_base()
    elif isinstance(property_type, StaticString):
        property_type.__class__ = DivanStaticString
    elif isinstance(property_type, String):
        property_type.__class__ = DivanString
    elif isinstance(property_type, Url):
        property_type.__class__ = DivanUrl
    else:
        raise NotImplementedError


class DivanEntity(Entity):
    def update_base(self):
        for prop in self.properties:
            prop.__class__ = DivanProperty
            cast(DivanProperty, prop).update_base()

    @property
    def supertype_declaration(self) -> str:
        supertypes = []
        enumeration = self.enclosing_enumeration
        if enumeration is not None:
            supertypes.append(f'{utils.capitalize_snake_case(enumeration.name)}()')
        if self.root_entity:
            supertypes.append('Root')
        additional_supertypes = self.protocol_plus_super_entities
        if additional_supertypes is not None:
            supertypes.append(additional_supertypes)
        if not supertypes:
            return ''
        return f' : {", ".join(supertypes)}'

    @property
    def header_comment_block(self) -> Text:
        required_prop_names = sorted([prop.name for prop in self.properties if not prop.optional], reverse=True)
        factory_method_name = utils.snake_case(self.name)  # TODO(use parent naming)
        lines = [
            '',
            f'Можно создать при помощи метода [{factory_method_name}].'
            '',
            f'Обязательные поля: {", ".join(required_prop_names)}'
        ]
        return comment(*lines)

    @property
    def serialization_declaration(self) -> Text:
        result = Text()
        static_type = self.static_type
        if static_type is None:
            result += '@JsonAnyGetter'
            result += 'internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())'
        else:
            result += '@JsonAnyGetter'
            result += 'internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith('
            result += f'    mapOf("type" to "{static_type}")'
            result += ')'
        return result

    @property
    def properties_class_declaration(self) -> Text:
        declaration = Text()
        declaration += 'class Properties internal constructor('
        for prop in cast(List[DivanProperty], self.instance_properties):
            declaration += f'{prop.constructor_parameter_declaration.indented(indent_width=4)},'
        declaration += ') {'
        merge_with_declaration = Text('internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {')
        merge_with_declaration += '    val result = mutableMapOf<String, Any>()'
        merge_with_declaration += '    result.putAll(properties)'
        for prop in cast(List[DivanProperty], self.instance_properties):
            merge_with_declaration += f'    result.tryPutProperty("{prop.name}", {prop.name_declaration})'
        merge_with_declaration += '    return result'
        merge_with_declaration += '}'
        declaration += merge_with_declaration.indented(indent_width=4)
        declaration += '}'
        return declaration


class DivanEntityEnumeration(EntityEnumeration):
    def update_base(self):
        self._entities = list(map(lambda e: (e[0], update_base(e[1])), self._entities))


class DivanStringEnumeration(StringEnumeration):
    pass


class DivanProperty(Property):
    def update_base(self):
        update_property_type_base(self.property_type)

    @property
    def constructor_parameter_declaration(self) -> Text:
        prop_type = cast(DivanPropertyType, self.property_type)
        type_declaration = f'Property<{cast(DivanPropertyType, prop_type.declaration(prefixed=False))}>'
        default_value = self.default_value
        declaration = Text()
        if default_value is not None:
            declaration += default_value_comment(default_value)
        declaration += f'val {self.name_declaration}: {type_declaration}?'
        return declaration

    @property
    def name_declaration(self) -> str:
        name = utils.lower_camel_case(self.name)
        if isinstance(self.property_type, StaticString):
            name = utils.constant_upper_case(self.name)
        return utils.fixing_first_digit(name)


class DivanPropertyType(PropertyType):
    def declaration(self, prefixed: bool) -> str:
        if isinstance(self, Int):
            return 'Int'
        elif isinstance(self, Double):
            return 'Double'
        elif isinstance(self, Bool):
            return 'Boolean'
        elif isinstance(self, BoolInt):
            return 'BoolInt'
        elif isinstance(self, (String, StaticString)):
            return 'String'
        elif isinstance(self, Color):
            return 'Color'
        elif isinstance(self, Url):
            return 'URI'
        elif isinstance(self, Dictionary):
            return 'Map<String, Any>'
        elif isinstance(self, Array):
            element_type = cast(DivanPropertyType, self.property_type).declaration(prefixed)
            return f'List<{element_type}>'
        elif isinstance(self, Object):
            if self.name.startswith('$predefined_'):
                return self.name.replace('$predefined_', '')
            obj_name = utils.capitalize_camel_case(self.name)
            obj = self.object
            if obj is not None:
                if prefixed:
                    obj_name = obj.resolved_prefixed_declaration
                else:
                    obj_name = utils.capitalize_camel_case(obj.resolved_name)
            return obj_name
        else:
            raise NotImplementedError


class DivanArray(DivanPropertyType, Array):
    def update_base(self):
        if not isinstance(self.property_type, DivanPropertyType):
            update_property_type_base(self.property_type)

    @property
    def property_type_dsl(self) -> DivanPropertyType:
        return cast(DivanPropertyType, self.property_type)


class DivanBool(DivanPropertyType, Bool):
    pass


class DivanBoolInt(DivanPropertyType, BoolInt):
    pass


class DivanColor(DivanPropertyType, Color):
    pass


class DivanDictionary(DivanPropertyType, Dictionary):
    pass


class DivanDouble(DivanPropertyType, Double):
    pass


class DivanInt(DivanPropertyType, Int):
    pass


class DivanObject(DivanPropertyType, Object):
    def update_base(self):
        if isinstance(self.object, Entity):
            self.object.__class__ = DivanEntity
            cast(DivanEntity, self.object).update_base()


class DivanStaticString(DivanPropertyType, StaticString):
    pass


class DivanString(DivanPropertyType, String):
    pass


class DivanUrl(DivanPropertyType, Url):
    pass
