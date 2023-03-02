from __future__ import annotations
from typing import List, cast, Dict

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

GUARD_INSTANCE_PARAM = '`use named arguments`: Guard = Guard.instance,'


def full_name(obj: Declarable, remove_prefix: str) -> str:
    name = utils.capitalize_camel_case(obj.name, remove_prefix)
    current_parent = obj.parent
    while current_parent is not None:
        name = f'{utils.capitalize_camel_case(current_parent.name, remove_prefix)}.{name}'
        current_parent = current_parent.parent
    return name


def update_base(obj: Declarable, remove_prefix: str) -> Declarable:
    if isinstance(obj, Entity):
        obj.__class__ = DivanEntity
        entity = cast(DivanEntity, obj)
        entity.remove_prefix = remove_prefix
        entity.update_base()
    elif isinstance(obj, EntityEnumeration):
        obj.__class__ = DivanEntityEnumeration
        entity = cast(DivanEntityEnumeration, obj)
        entity.remove_prefix = remove_prefix
        entity.update_base()
    elif isinstance(obj, StringEnumeration):
        obj.__class__ = DivanStringEnumeration
    return obj


def comment(*lines) -> Text:
    comment_block = Text('/**')
    for line in lines:
        comment_block += f' * {line}'
    comment_block += ' */'
    return comment_block


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
    remove_prefix: str

    def update_base(self):
        for prop in self.properties:
            prop.__class__ = DivanProperty
            cast(DivanProperty, prop).update_base()

    @property
    def supertype_declaration(self) -> str:
        supertypes = self.supertypes_list
        if not supertypes:
            return ''
        return f' : {", ".join(supertypes)}'

    @property
    def supertypes_list(self) -> List[str]:
        supertypes = []
        for enumeration in self.enclosing_enumerations:
            supertypes.append(f'{utils.capitalize_camel_case(enumeration.name, self.remove_prefix)}')
        additional_supertypes = self.protocol_plus_super_entities(with_impl_protocol=False)
        if additional_supertypes is not None:
            supertypes.extend(additional_supertypes.split(', '))
        return supertypes

    def header_comment_block(self, translations: Dict[str, str]) -> Text:
        required_prop_names = sorted([prop.name for prop in self.properties if not prop.optional], reverse=True)
        factory_method_name = self.factory_method_name_with_parent
        lines = []
        description = self.description_doc()
        if description not in ['None', '']:
            lines.append(description)
            lines.append('')
        lines.append(translations['div_generator_factory_method_name'].format(factory_method_name))
        if required_prop_names:
            lines.append('')
            lines.append(translations['div_generator_required_properties'].format(', '.join(required_prop_names)))
        return comment(*lines)

    @property
    def params_comment_block(self) -> Text:
        params = [
            f'@param {cast(DivanProperty, prop).name_declaration} {prop.description_doc()}'
            for prop in self.properties if prop.description_doc() is not None
        ]
        return comment(*params) if params else Text()

    @property
    def evaluatable_params_comment_block(self) -> Text:
        params = [
            f'@param {cast(DivanProperty, prop).name_declaration} {prop.description_doc()}'
            for prop in self.properties
            if prop.description_doc() is not None and cast(DivanProperty, prop).is_evaluatable_property
        ]
        return comment(*params) if params else Text()

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

    def literal_properties_signature(
            self,
            force_named_arguments: bool,
            force_default_nulls: bool,
            exclude: List[str] = None,
    ) -> Text:
        forced_property_order = []
        if exclude is None:
            exclude = []
        is_named_guard_added = False
        signature_declaration = Text()
        for property in cast(List[DivanProperty], self.instance_properties):
            if property.name in exclude:
                continue
            if not is_named_guard_added:
                if property.name not in forced_property_order or force_named_arguments:
                    signature_declaration += GUARD_INSTANCE_PARAM
                    is_named_guard_added = True
            required = not force_default_nulls and not property.optional
            default = 'null'
            property_type = cast(DivanPropertyType, property.property_type)
            type_name_declaration = property_type.declaration(prefixed=True, remove_prefix=self.remove_prefix)
            if not required:
                type_name_declaration += '?'
            property_declaration = f'{property.name_declaration}: {type_name_declaration}'
            if not required or default != 'null':
                property_declaration += f' = {default}'
            property_declaration += ','
            signature_declaration += property_declaration
        return signature_declaration

    def reference_properties_signature(self) -> Text:
        signature_declaration = Text(GUARD_INSTANCE_PARAM)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_type = cast(DivanPropertyType, property.property_type)
            type_name_declaration = property_type.declaration(prefixed=True, remove_prefix=self.remove_prefix)
            property_declaration = f'{property.name_declaration}: ReferenceProperty<{type_name_declaration}>? = null,'
            signature_declaration += property_declaration
        return signature_declaration

    def expression_properties_signature(self) -> (Text, bool):
        signature_declaration = Text(GUARD_INSTANCE_PARAM)
        has_params_except_guard = False
        for property in cast(List[DivanProperty], self.instance_properties):
            if not property.is_evaluatable_property:
                continue
            has_params_except_guard = True
            property_type = cast(DivanPropertyType, property.property_type)
            type_name_declaration = property_type.declaration(prefixed=True, remove_prefix=self.remove_prefix)
            property_declaration = f'{property.name_declaration}: ExpressionProperty<{type_name_declaration}>? = null,'
            signature_declaration += property_declaration
        return signature_declaration, has_params_except_guard

    def properties_class_declaration(self, translations: Dict[str, str]) -> Text:
        declaration = Text()
        declaration += 'class Properties internal constructor('
        for prop in cast(List[DivanProperty], self.instance_properties):
            declaration += f'{prop.constructor_parameter_declaration(translations, self.remove_prefix).indented(indent_width=4)},'
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

    @property
    def factory_method_declaration(self) -> Text:
        type_name = full_name(self, self.remove_prefix)
        method_name = self.factory_method_name_with_parent
        declaration = Text(f'fun DivScope.{method_name}(')
        declaration += self.literal_properties_signature(
            force_named_arguments=False,
            force_default_nulls=False,
        ).indented(level=1, indent_width=4)
        declaration += f'): {type_name} = {type_name}('
        declaration += utils.indented(f'{type_name}.Properties(', level=1, indent_width=4)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            declaration += utils.indented(f'{property_name} = valueOrNull({property_name}),', level=2, indent_width=4)
        declaration += utils.indented(')', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def properties_factory_method_declaration(self) -> Text:
        type_name = full_name(self, self.remove_prefix) + '.Properties'
        method_name = self.factory_method_name_with_parent + 'Props'
        declaration = Text(f'fun DivScope.{method_name}(')
        declaration += self.literal_properties_signature(
            force_named_arguments=True,
            force_default_nulls=True,
        ).indented(level=1, indent_width=4)
        declaration += f') = {type_name}('
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            declaration += utils.indented(f'{property_name} = valueOrNull({property_name}),', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def references_factory_method_declaration(self) -> Text:
        type_name = full_name(self, self.remove_prefix) + '.Properties'
        method_name = self.factory_method_name_with_parent + 'Refs'
        declaration = Text(f'fun TemplateScope.{method_name}(')
        declaration += self.reference_properties_signature().indented(level=1, indent_width=4)
        declaration += f') = {type_name}('
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            declaration += utils.indented(f'{property_name} = {property_name},', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def operator_plus_declaration(self) -> Text:
        name = utils.capitalize_camel_case(self.name, self.remove_prefix)
        declaration = Text(f'operator fun plus(additive: Properties): {name} = {name}(')
        declaration += utils.indented('Properties(', level=1, indent_width=4)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            declaration += utils.indented(
                f'{property_name} = additive.{property_name} ?: properties.{property_name},',
                level=2,
                indent_width=4
            )
        declaration += utils.indented(')', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def operator_plus_component_declaration(self) -> Text:
        name = utils.capitalize_camel_case(self.name, self.remove_prefix)
        declaration = Text(
            f'operator fun Component<{name}>.plus(additive: {name}.Properties): Component<{name}> = Component('
        )
        declaration += utils.indented('template = template,', level=1, indent_width=4)
        declaration += utils.indented('properties = additive.mergeWith(properties)', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def as_list_method_declaration(self) -> Text:
        name = full_name(self, self.remove_prefix)
        return Text(f'fun {name}.asList() = listOf(this)')

    @property
    def override_method_declaration(self) -> Text:
        name = full_name(self, self.remove_prefix)
        declaration = Text(f'fun {name}.override(')
        declaration += self.literal_properties_signature(
            force_named_arguments=True,
            force_default_nulls=True,
        ).indented(level=1, indent_width=4)
        declaration += f'): {name} = {name}('
        declaration += utils.indented(f'{name}.Properties(', level=1, indent_width=4)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            declaration += utils.indented(
                f'{property_name} = valueOrNull({property_name}) ?: properties.{property_name},',
                level=2,
                indent_width=4
            )
        declaration += utils.indented(')', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def override_component_method_declaration(self) -> Text:
        name = full_name(self, self.remove_prefix)
        declaration = Text(f'fun Component<{name}>.override(')
        declaration += self.literal_properties_signature(
            force_named_arguments=True,
            force_default_nulls=True,
        ).indented(level=1, indent_width=4)
        declaration += f'): Component<{name}> = Component('
        declaration += utils.indented('template = template,', level=1, indent_width=4)
        declaration += utils.indented(f'properties = {name}.Properties(', level=1, indent_width=4)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            declaration += utils.indented(f'{property_name} = valueOrNull({property_name}),', level=2, indent_width=4)
        declaration += utils.indented(').mergeWith(properties)', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def defer_method_declaration(self) -> Text:
        name = full_name(self, self.remove_prefix)
        declaration = Text(f'fun {name}.defer(')
        declaration += self.reference_properties_signature().indented(level=1, indent_width=4)
        declaration += f'): {name} = {name}('
        declaration += utils.indented(f'{name}.Properties(', level=1, indent_width=4)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            declaration += utils.indented(
                f'{property_name} = {property_name} ?: properties.{property_name},',
                level=2,
                indent_width=4
            )
        declaration += utils.indented(')', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def defer_component_method_declaration(self) -> Text:
        name = full_name(self, self.remove_prefix)
        declaration = Text(f'fun Component<{name}>.defer(')
        declaration += self.reference_properties_signature().indented(level=1, indent_width=4)
        declaration += f'): Component<{name}> = Component('
        declaration += utils.indented('template = template,', level=1, indent_width=4)
        declaration += utils.indented(f'properties = {name}.Properties(', level=1, indent_width=4)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            declaration += utils.indented(f'{property_name} = {property_name},', level=2, indent_width=4)
        declaration += utils.indented(').mergeWith(properties)', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def evaluate_method_declaration(self) -> Text:
        name = full_name(self, self.remove_prefix)
        declaration = Text(f'fun {name}.evaluate(')
        expression_properties_signature, has_params_except_guard = self.expression_properties_signature()
        if not has_params_except_guard:
            return Text()
        declaration += expression_properties_signature.indented(level=1, indent_width=4)
        declaration += f'): {name} = {name}('
        declaration += utils.indented(f'{name}.Properties(', level=1, indent_width=4)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            value = f'{property_name} ?: properties.{property_name}' if property.is_evaluatable_property \
                else f'properties.{property_name}'
            declaration += utils.indented(f'{property_name} = {value},', level=2, indent_width=4)
        declaration += utils.indented(')', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def evaluate_component_method_declaration(self) -> Text:
        name = full_name(self, self.remove_prefix)
        declaration = Text(f'fun Component<{name}>.evaluate(')
        expression_properties_signature, has_params_except_guard = self.expression_properties_signature()
        if not has_params_except_guard:
            return Text()
        declaration += expression_properties_signature.indented(level=1, indent_width=4)
        declaration += f'): Component<{name}> = Component('
        declaration += utils.indented('template = template,', level=1, indent_width=4)
        declaration += utils.indented(f'properties = {name}.Properties(', level=1, indent_width=4)
        for property in cast(List[DivanProperty], self.instance_properties):
            property_name = property.name_declaration
            value = property_name if property.is_evaluatable_property else 'null'
            declaration += utils.indented(f'{property_name} = {value},', level=2, indent_width=4)
        declaration += utils.indented(').mergeWith(properties)', level=1, indent_width=4)
        declaration += ')'
        return declaration

    @property
    def factory_method_name_with_parent(self) -> str:
        name = utils.capitalize_camel_case(self.name, self.remove_prefix)
        current_parent = self.parent
        while current_parent is not None:
            name = f'{utils.capitalize_camel_case(current_parent.name, self.remove_prefix)}{name}'
            current_parent = current_parent.parent
        return name[0].lower() + name[1:]


class DivanEntityEnumeration(EntityEnumeration):
    remove_prefix: str

    def update_base(self):
        self._entities = list(map(lambda e: (e[0], update_base(e[1], self.remove_prefix)), self._entities))


class DivanStringEnumeration(StringEnumeration):

    def header_comment_block(self, translations: Dict[str, str]) -> Text:
        comment_lines = []
        description_doc = self.description_doc()
        if description_doc not in ['', 'None']:
            comment_lines.append(description_doc)
            comment_lines.append('')
        possible_values = translations['div_generator_possible_values']\
            .format(f"[{', '.join(map(lambda case: case[1], self.cases), )}]")
        comment_lines.append(possible_values)
        return comment(*comment_lines)


class DivanProperty(Property):
    def update_base(self):
        update_property_type_base(self.property_type)

    def constructor_parameter_declaration(self, translations: Dict[str, str], remove_prefix: str) -> Text:
        prop_type = cast(DivanPropertyType, self.property_type)
        prop_type_declaration = prop_type.declaration(prefixed=False, remove_prefix=remove_prefix)
        type_declaration = f'Property<{prop_type_declaration}>'
        default_value = self.default_value
        declaration = Text()

        comment_lines = []
        description_doc = self.description_doc()
        if description_doc is not None:
            comment_lines.append(description_doc)
        if default_value is not None:
            comment_lines.append(translations["div_generator_default_value"].format(default_value))
        if comment_lines:
            declaration += comment(*comment_lines)

        declaration += f'val {self.name_declaration}: {type_declaration}?'
        return declaration

    @property
    def name_declaration(self) -> str:
        name = utils.lower_camel_case(self.name)
        if isinstance(self.property_type, StaticString):
            name = utils.constant_upper_case(self.name)
        return utils.fixing_first_digit(name)

    @property
    def is_evaluatable_property(self) -> bool:
        return self.supports_expressions and cast(DivanPropertyType, self.property_type).is_primitive


class DivanPropertyType(PropertyType):
    def declaration(self, prefixed: bool, remove_prefix: str) -> str:
        if isinstance(self, Int):
            return 'Int'
        elif isinstance(self, Double):
            return 'Double'
        elif isinstance(self, (Bool, BoolInt)):
            return 'Boolean'
        elif isinstance(self, (String, StaticString)):
            return 'String'
        elif isinstance(self, Color):
            return 'Color'
        elif isinstance(self, Url):
            return 'Url'
        elif isinstance(self, Dictionary):
            return 'Map<String, Any>'
        elif isinstance(self, Array):
            element_type = cast(DivanPropertyType, self.property_type).declaration(prefixed, remove_prefix)
            return f'List<{element_type}>'
        elif isinstance(self, Object):
            if self.name.startswith('$predefined_'):
                return self.name.replace('$predefined_', '')
            obj_name = utils.capitalize_camel_case(self.name, remove_prefix)
            obj = self.object
            if obj is not None:
                if prefixed:
                    obj_name = full_name(obj, remove_prefix)
                else:
                    obj_name = utils.capitalize_camel_case(obj.resolved_name, remove_prefix)
            return obj_name
        else:
            raise NotImplementedError

    @property
    def is_primitive(self) -> bool:
        if isinstance(self, (Int, Double, Bool, BoolInt, String, StaticString, Color, Url)):
            return True
        elif isinstance(self, (Dictionary, Array, Object)):
            return False
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
