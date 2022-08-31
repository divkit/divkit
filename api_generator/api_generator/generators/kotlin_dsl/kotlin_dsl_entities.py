from __future__ import annotations
from typing import List, Optional, cast

from ...schema.modeling.entities import (
    Entity,
    EntityEnumeration,
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
from ...schema.modeling.text import Text, EMPTY


def update_base(obj: Declarable) -> Declarable:
    if isinstance(obj, Entity):
        obj.__class__ = KotlinDSLEntity
        cast(KotlinDSLEntity, obj).update_base()
    elif isinstance(obj, EntityEnumeration):
        obj.__class__ = KotlinDSLEntityEnumeration
        cast(KotlinDSLEntityEnumeration, obj).update_base()
    return obj


def update_property_type_base(property_type: PropertyType):
    if isinstance(property_type, Array):
        property_type.__class__ = KotlinDSLArray
        property_type = cast(KotlinDSLArray, property_type)
        property_type.update_base()
    elif isinstance(property_type, Bool):
        property_type.__class__ = KotlinDSLBool
    elif isinstance(property_type, BoolInt):
        property_type.__class__ = KotlinDSLBoolInt
    elif isinstance(property_type, Color):
        property_type.__class__ = KotlinDSLColor
    elif isinstance(property_type, Dictionary):
        property_type.__class__ = KotlinDSLDictionary
    elif isinstance(property_type, Double):
        property_type.__class__ = KotlinDSLDouble
    elif isinstance(property_type, Int):
        property_type.__class__ = KotlinDSLInt
    elif isinstance(property_type, Object):
        property_type.__class__ = KotlinDSLObject
        property_type = cast(KotlinDSLObject, property_type)
        property_type.update_base()
    elif isinstance(property_type, StaticString):
        property_type.__class__ = KotlinDSLStaticString
    elif isinstance(property_type, String):
        property_type.__class__ = KotlinDSLString
    elif isinstance(property_type, Url):
        property_type.__class__ = KotlinDSLUrl
    else:
        raise NotImplementedError


class KotlinDSLEntity(Entity):
    def update_base(self):
        for prop in self.properties:
            prop.__class__ = KotlinDSLProperty
            cast(KotlinDSLProperty, prop).update_base()

    @property
    def constructor_parameter_declaration(self) -> Text:
        result = Text()
        for prop in cast(List[KotlinDSLProperty], self.instance_properties):
            present_in_supertype = False
            if self.implemented_protocol is not None:
                present_in_supertype = any(p.name == prop.name for p in self.implemented_protocol.properties)
            result += f'{prop.constructor_parameter_declaration(is_abstract=False, is_overridden=present_in_supertype)},'
        return result

    @property
    def supertype_declaration(self) -> str:
        supertypes = []
        enumeration = self.enclosing_enumeration
        if enumeration is not None:
            supertypes.append(f'{utils.capitalize_camel_case(enumeration.name)}()')
        if self.root_entity:
            supertypes.append('Root')
        additional_supertypes = self.protocol_plus_super_entities
        if additional_supertypes is not None:
            supertypes.append(additional_supertypes)
        if not supertypes:
            return ''
        return f' : {", ".join(supertypes)}'

    def factory_methods_declaration(self, for_templates: bool) -> Text:
        if for_templates:
            result = self.template_factory_methods_declaration
        else:
            result = self.regular_factory_methods_declaration

        for inner_type in sorted(filter(lambda t: isinstance(t, Entity), self.inner_types), key=lambda t: t.name):
            result += EMPTY
            result += cast(KotlinDSLEntity, inner_type).factory_methods_declaration(for_templates)
        return result

    @property
    def reordered_instance_properties(self) -> List[KotlinDSLProperty]:
        return cast(List[KotlinDSLProperty],
                    sorted(self.instance_properties, key=lambda p: p.optional))

    @property
    def template_factory_methods_declaration(self) -> Text:
        properties = cast(List[KotlinDSLProperty], self.instance_properties)
        reordered_properties = self.reordered_instance_properties

        result = Text()
        name = utils.lower_camel_case(self.name)
        pref_decl = self.resolved_prefixed_declaration
        result += f'fun <T> TemplateContext<T>.{name}(): LiteralProperty<{pref_decl}> {{'
        result += f'    return value({pref_decl}('
        for prop in properties:
            prop_name = prop.name_declaration
            result += utils.indented(f'{prop_name} = null,', indent_width=8)
        result += '    ))'
        result += '}'

        if not properties:
            return result

        result += EMPTY
        result += f'fun <T> TemplateContext<T>.{name}('
        for prop in reordered_properties:
            result += utils.indented(prop.factory_parameter_declaration(force_optionality=True,
                                                                        type_wrapper='Property'), indent_width=4)
        result += f'): LiteralProperty<{pref_decl}> {{'
        result += f'    return value({pref_decl}('
        for prop in properties:
            prop_name = prop.name_declaration
            result += utils.indented(f'{prop_name} = {prop_name},', indent_width=8)
        result += '    ))'
        result += '}'

        result += EMPTY
        result += f'fun <T> TemplateContext<T>.{name}('
        for prop in reordered_properties:
            result += utils.indented(prop.factory_parameter_declaration(force_optionality=True), indent_width=4)
        result += f'): LiteralProperty<{pref_decl}> {{'
        result += f'    return value({pref_decl}('
        for prop in properties:
            prop_name = prop.name_declaration
            result += utils.indented(f'{prop_name} = optionalValue({prop_name}),', indent_width=8)
        result += '    ))'
        result += '}'

        return result

    @property
    def all_properties_are_optional(self) -> bool:
        return all(p.effectively_optional for p in cast(List[KotlinDSLProperty], self.instance_properties))

    @property
    def regular_factory_methods_declaration(self) -> Text:
        properties = cast(List[KotlinDSLProperty], self.instance_properties)
        reordered_properties = self.reordered_instance_properties

        name = utils.lower_camel_case(self.name)
        pref_decl = self.resolved_prefixed_declaration

        result = Text()

        if self.all_properties_are_optional or not properties:
            result += f'fun CardContext.{name}(): {pref_decl} {{'
            result += f'    return {pref_decl}('
            for prop in properties:
                prop_name = prop.name_declaration
                result += utils.indented(f'{prop_name} = null,', indent_width=8)
            result += '    )'
            result += '}'

            if not properties:
                return result

            result += EMPTY

        result += f'fun CardContext.{name}('
        for prop in reordered_properties:
            result += utils.indented(prop.factory_parameter_declaration(force_optionality=False,
                                                                        type_wrapper='ValueProperty'), indent_width=4)
        result += f'): {pref_decl} {{'
        result += f'    return {pref_decl}('
        for prop in properties:
            prop_name = prop.name_declaration
            result += utils.indented(f'{prop_name} = {prop_name},', indent_width=8)
        result += '    )'
        result += '}'

        result += EMPTY
        result += f'fun CardContext.{name}('
        for prop in reordered_properties:
            result += utils.indented(prop.factory_parameter_declaration(force_optionality=False), indent_width=4)
        result += f'): {pref_decl} {{'
        result += f'    return {pref_decl}('
        for prop in properties:
            prop_name = prop.name_declaration
            prop_val = f'optionalValue({prop_name})' if prop.effectively_optional else f'value({prop_name})'
            result += utils.indented(f'{prop_name} = {prop_val},', indent_width=8)
        result += '    )'
        result += '}'

        return result

    @property
    def serialization_declaration(self) -> Text:
        result = Text()
        result += EMPTY
        result += '@JsonAnyGetter'
        result += 'internal fun properties(): Map<String, Any> {'
        result += '    return propertyMapOf('
        for prop in cast(List[KotlinDSLProperty], self.instance_properties):
            result += utils.indented(f'"{prop.name}" to {prop.name_declaration},', indent_width=8)
        result += '    )'
        result += '}'
        return result


class KotlinDSLProperty(Property):
    def update_base(self):
        update_property_type_base(self.property_type)

    def constructor_parameter_declaration(self, is_abstract: bool, is_overridden: bool) -> Text:
        qualifiers = ''
        if not is_abstract:
            qualifiers += '@JsonIgnore '
        if is_overridden:
            qualifiers += 'override '
            assert not is_abstract
        prop_type = cast(KotlinDSLPropertyType, self.property_type)
        type = f'Property<{cast(KotlinDSLPropertyType, prop_type.declaration(prefixed=False))}>'
        return Text(f'{qualifiers}val {self.name_declaration}: {type}?')

    @property
    def name_declaration(self) -> str:
        name = utils.lower_camel_case(self.name)
        if isinstance(self.property_type, StaticString):
            name = utils.constant_upper_case(self.name)
        return utils.fixing_first_digit(name)

    def factory_parameter_declaration(self, force_optionality: bool, type_wrapper: Optional[str] = None) -> str:
        is_optional = force_optionality or self.effectively_optional
        optional_mark = '?' if is_optional else ''
        decl = cast(KotlinDSLPropertyType, self.property_type).declaration(prefixed=True)
        if type_wrapper is not None:
            type_decl = f'{type_wrapper}<{decl}>{optional_mark}'
        else:
            type_decl = f'{decl}{optional_mark}'
        default_value = ' = null' if is_optional else ''
        return f'{self.name_declaration}: {type_decl}{default_value},'

    @property
    def effectively_optional(self) -> bool:
        return self.optional or self.default_value is not None


class KotlinDSLPropertyType(PropertyType):
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
            element_type = cast(KotlinDSLPropertyType, self.property_type).declaration(prefixed)
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


class KotlinDSLArray(KotlinDSLPropertyType, Array):
    def update_base(self):
        if not isinstance(self.property_type, KotlinDSLPropertyType):
            update_property_type_base(self.property_type)

    @property
    def property_type_dsl(self) -> KotlinDSLPropertyType:
        return cast(KotlinDSLPropertyType, self.property_type)


class KotlinDSLBool(KotlinDSLPropertyType, Bool):
    pass


class KotlinDSLBoolInt(KotlinDSLPropertyType, BoolInt):
    pass


class KotlinDSLColor(KotlinDSLPropertyType, Color):
    pass


class KotlinDSLDictionary(KotlinDSLPropertyType, Dictionary):
    pass


class KotlinDSLDouble(KotlinDSLPropertyType, Double):
    pass


class KotlinDSLInt(KotlinDSLPropertyType, Int):
    pass


class KotlinDSLObject(KotlinDSLPropertyType, Object):
    def update_base(self):
        if isinstance(self.object, Entity):
            self.object.__class__ = KotlinDSLEntity
            cast(KotlinDSLEntity, self.object).update_base()


class KotlinDSLStaticString(KotlinDSLPropertyType, StaticString):
    pass


class KotlinDSLString(KotlinDSLPropertyType, String):
    pass


class KotlinDSLUrl(KotlinDSLPropertyType, Url):
    pass


class KotlinDSLEntityEnumeration(EntityEnumeration):
    def update_base(self):
        self._entities = list(map(lambda e: (e[0], update_base(e[1])), self._entities))

    @property
    def supertype_declaration(self) -> str:
        if not self._root_entity:
            return ''
        return ' : Root'

    @property
    def enumeration_name(self) -> str:
        return utils.capitalize_camel_case(self.name)

    @property
    def template_case_name(self) -> str:
        return f'Templated{self.enumeration_name}'

    @property
    def template_case_declaration(self) -> Text:
        result = Text()
        result += f'private class {self.template_case_name} constructor('
        result += '    @JsonProperty(\"type\") override val type: String,'
        result += '    @JsonIgnore val bindings: Array<out TemplateBinding<*>>'
        result += f') : {self.enumeration_name}() {{'
        result += EMPTY
        result += '    @JsonAnyGetter'
        result += '    fun properties(): Map<String, Any> {'
        result += '        return propertyMapOf(*bindings)'
        result += '    }'
        result += '}'
        result += EMPTY
        result += 'fun <T> TemplateContext<T>.template('
        result += '    type: String,'
        result += '    vararg bindings: PropertyOverriding<*>'
        result += f'): LiteralProperty<{self.enumeration_name}> {{'
        result += f'    return LiteralProperty{self.template_case_name}(type, bindings))'
        result += '}'
        result += EMPTY
        result += 'fun CardContext.template('
        result += '    type: String,'
        result += '    vararg bindings: TemplateBinding<*>'
        result += f'): {self.enumeration_name} {{'
        result += f'    return {self.template_case_name}(type, bindings)'
        result += '}'
        return result
