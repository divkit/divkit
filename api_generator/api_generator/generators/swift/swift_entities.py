from __future__ import annotations
from typing import List, Optional, cast
from enum import Enum
from .utils import fixing_keywords
from ..base import declaration_comment
from ...schema.modeling.entities import (
    Entity,
    EntityEnumeration,
    StringEnumeration,
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
    ObjectFormat,
    SwiftGeneratorProperties,
)
from ...config import GenerationMode
from ... import utils
from ...schema.modeling.text import Text, EMPTY

PARENT_PROPERTY = Property(
    name='parent',
    description=None,
    description_translations={},
    dict_field='type',
    property_type=String(
        min_length=0,
        formatted=False,
        regex=None
    ),
    optional=True,
    is_deprecated=False,
    mode=GenerationMode.NORMAL_WITHOUT_TEMPLATES,
    supports_expressions_flag=False,
    default_value=None,
    platforms=None
)


class SwiftAccessLevel(str, Enum):
    PUBLIC = 'public '
    INTERNAL = ''


def swift_template_deserializable_args_decl(mode: GenerationMode):
    return ', templateToType: [TemplateName: String]' if mode.is_template else ''


def swift_template_deserializable_args(mode: GenerationMode):
    return ', templateToType: templateToType' if mode.is_template else ''


def _swift_default_value_declaration_comment(p: Property) -> str:
    if isinstance(p.property_type, Object) and not isinstance(p.property_type.object, StringEnumeration):
        property_type = cast(SwiftPropertyType, Object(name='',
                                                       object=p.property_type.object,
                                                       format=ObjectFormat.DEFAULT))
        comment_value = property_type.internal_declaration(p.default_value)
    else:
        comment_value = p.default_value
    return comment_value


class SwiftEntity(Entity):
    def update_bases(self):
        Int.__bases__ = (SwiftPropertyType, PropertyType,)
        Bool.__bases__ = (SwiftPropertyType, PropertyType,)
        BoolInt.__bases__ = (SwiftPropertyType, PropertyType,)
        Double.__bases__ = (SwiftPropertyType, PropertyType,)
        StaticString.__bases__ = (SwiftPropertyType, PropertyType,)
        Object.__bases__ = (SwiftPropertyType, PropertyType,)
        Array.__bases__ = (SwiftPropertyType, PropertyType,)
        Url.__bases__ = (SwiftPropertyType, PropertyType,)
        Color.__bases__ = (SwiftPropertyType, PropertyType,)
        String.__bases__ = (SwiftPropertyType, PropertyType,)
        Dictionary.__bases__ = (SwiftPropertyType, PropertyType,)
        RawArray.__bases__ = (SwiftPropertyType, PropertyType,)
        for prop in self.properties:
            prop.__class__ = SwiftProperty

    def update_base(self):
        self.generator_properties: SwiftGeneratorProperties = self.generator_properties

    @property
    def has_static_type(self) -> bool:
        return any(p.name == 'type' and isinstance(p.property_type, StaticString) for p in self.properties)

    @property
    def has_parent_property(self) -> bool:
        return self.has_static_type

    @property
    def properties_swift(self) -> List[SwiftProperty]:
        self.update_bases()
        if self.generation_mode.is_template and self.has_parent_property:
            PARENT_PROPERTY.__class__ = SwiftProperty
            return cast(List[SwiftProperty], [PARENT_PROPERTY] + self.properties)
        else:
            return cast(List[SwiftProperty], self.properties)

    @property
    def properties_to_declare_swift(self) -> List[SwiftProperty]:
        return sorted(self.properties_swift, key=lambda p: not isinstance(p.property_type, StaticString))

    @property
    def instance_properties_swift(self) -> List[SwiftProperty]:
        return list(filter(lambda p: not isinstance(p.property_type, StaticString), self.properties_swift))

    @property
    def deserializing_constructor_declaration(self) -> Text:
        props = self.instance_properties_swift
        args_decl = swift_template_deserializable_args_decl(self.generation_mode)
        if not props:
            return Text(f'public init(dictionary: [String: Any]{args_decl}) throws {{}}')
        result = Text(f'public convenience init(dictionary: [String: Any]{args_decl}) throws {{')
        lines = Text('self.init(')
        for prop in props:
            tail = '' if prop == props[-1] else ','
            if prop.name == PARENT_PROPERTY.name:
                lines += f'  {prop.declaration_name}: dictionary["type"] as? String{tail}'
            else:
                lines += f'  {prop.declaration_name}: dictionary.{prop.deserialization_expression}{tail}'
        lines += ')'
        result += lines.indented()
        result += '}'
        return result

    @property
    def plain_constructor_declaration(self) -> Text:
        props = self.instance_properties_swift
        if not props:
            return Text('init() {}')

        result = Text('init(')
        tails = [','] * (len(props) - 1) + ['']
        for prop, tail in zip(props, tails):
            if not self.generation_mode.is_template:
                if prop.parsed_value_is_optional:
                    nullability = '' if prop.should_be_optional else '?'
                    generate_optional_args = True
                    generate_optional_args = self.generator_properties.generate_optional_args
                    optionality = ' = nil' if generate_optional_args else ''
                else:
                    nullability = ''
                    optionality = ''
            else:
                nullability = ''
                optionality = ' = nil' if prop.mode.is_template else ''
            result += f'  {prop.declaration_name}: {prop.type_declaration}{nullability}{optionality}{tail}'
        result += ') {'

        for prop in props:
            decl = f'self.{prop.declaration_name} = {fixing_keywords(prop.declaration_name)}'
            if self.generation_mode.is_template:
                res = decl
            else:
                public_default_value = self.generator_properties.public_default_values
                res = prop.add_default_value_to(decl, public_default_value)
            result += Text(indent_width=2, init_lines=res)
        result += '}'

        return result

    @property
    def resolve_value_only_by_links_body(self) -> Text:
        result = Text()

        template_props = list(filter(lambda p: p.mode.is_template, self.instance_properties_swift))
        required_props = list(filter(lambda p: not p.parsed_value_is_optional, template_props))
        return_type = self.resolved_declaration_prefix + utils.capitalize_camel_case(self.resolved_name)
        fun_name = 'private static func resolveOnlyLinks'
        name = utils.capitalize_camel_case(self.name)
        result += f'{fun_name}(context: TemplatesContext, parent: {name}?) -> DeserializationResult<{return_type}> {{'

        if not template_props:
            result += f'  return .success({return_type}())'
            result += '}'
            return result

        for prop in template_props:
            var_name = prop.value_resolving_local_var_name
            result += f'  let {var_name} = {{ parent?.{prop.declaration_name}?.{prop.resolve_value_expression} ?? .noValue }}()'

        result += f'  {"let" if not required_props else "var"} errors = mergeErrors('
        for index, prop in enumerate(template_props):
            separator = '' if index == (len(template_props) - 1) else ','
            map_body = f'.nestedObjectError(field: "{prop.dict_field}", error: $0)'
            var_name = prop.value_resolving_local_var_name
            result += Text(f'    {var_name}.errorsOrWarnings?.map {{ {map_body} }}{separator}')
        result += '  )'

        for prop in required_props:
            result += f'  if case .noValue = {prop.value_resolving_local_var_name} {{'
            field_error = f'.requiredFieldIsMissing(field: "{prop.dict_field}")'
            result += f'    errors.append({field_error})'
            result += '  }'

        if required_props:
            result += '  guard'
            for prop in required_props[:-1]:
                result += f'    let {prop.declaration_name}NonNil = {prop.value_resolving_local_var_name}.value,'
            last_prop = required_props[-1]
            result += f'    let {last_prop.declaration_name}NonNil = {last_prop.value_resolving_local_var_name}.value'
            result += '  else {'
            result += '    return .failure(NonEmptyArray(errors)!)'
            result += '  }'

        result += f'  let result = {return_type}('
        for index, prop in enumerate(template_props):
            prop_name = prop.declaration_name
            suffix = 'Value.value' if prop.parsed_value_is_optional else 'NonNil'
            separator = '' if index == (len(template_props) - 1) else ','
            result += Text(f'    {prop_name}: {{ {prop_name}{suffix} }}(){separator}')
        result += '  )'
        partial_success = '.partialSuccess(result, warnings: NonEmptyArray(errors)!)'
        result += f'  return errors.isEmpty ? .success(result) : {partial_success}'
        result += '}'

        return result

    @property
    def resolve_value_declaration(self) -> Text:
        result = Text()
        return_type = self.resolved_declaration_prefix + utils.capitalize_camel_case(self.resolved_name)
        name = utils.capitalize_camel_case(self.name)
        fun_name = 'public static func resolveValue'
        params = f'context: TemplatesContext, parent: {name}?, useOnlyLinks: Bool'
        result += f'{fun_name}({params}) -> DeserializationResult<{return_type}> {{'

        template_props = list(filter(lambda p: p.mode.is_template, self.instance_properties_swift))
        if not template_props:
            result += f'  return .success({return_type}())'
            result += '}'
            return result

        result += '  if useOnlyLinks {'
        result += '    return resolveOnlyLinks(context: context, parent: parent)'
        result += '  }'

        for prop in template_props:
            if prop.property_type.can_be_templated:
                initial_value = ' = .noValue'
            else:
                name = prop.declaration_name
                if prop.internal_validator_declaration(GenerationMode.NORMAL_WITH_TEMPLATES) is None or \
                        prop.supports_expressions:
                    value = ''
                else:
                    value = f'validatedBy: ResolvedValue.{prop.validator_var_name}'
                initial_value = f' = {{ parent?.{name}?.value({value}) ?? .noValue }}()'
            mode = SwiftProperty.SwiftMode(value=GenerationMode.NORMAL_WITH_TEMPLATES,
                                           use_expressions=prop.supports_expressions)
            val_type = cast(SwiftPropertyType, prop.property_type).prefixed_declaration(mode)
            result += f'  var {prop.value_resolving_local_var_name}: DeserializationResult<{val_type}>{initial_value}'

        result += '  _ = {'
        result += '    // Each field is parsed in its own lambda to keep the stack size managable'
        result += '    // Otherwise the compiler will allocate stack for each intermediate variable'
        result += '    // upfront even when we don\'t actually visit a relevant branch'
        result += '    for (key, __dictValue) in context.templateData {'

        for prop in template_props:
            result += '      _ = {'
            result += f'        if key == "{prop.dict_field}" {{'
            local_var_name = prop.value_resolving_local_var_name
            deserialize = cast(SwiftProperty, prop).deserialize_from_value_expression
            result += f'         {local_var_name} = {deserialize}.merged(with: {local_var_name})'
            result += '        }'
            result += '      }()'
        for prop in template_props:
            result += '      _ = {'
            result += f'       if key == parent?.{prop.declaration_name}?.link {{'
            local_var_name = prop.value_resolving_local_var_name
            deserialize = cast(SwiftProperty, prop).deserialize_from_value_expression
            result += f'         {local_var_name} = {local_var_name}.merged(with: {{ {deserialize} }})'
            result += '        }'
            result += '      }()'
        result += '    }'
        result += '  }()'

        templateable_props = list(filter(lambda p: p.property_type.can_be_templated, template_props))
        if templateable_props:
            result += '  if let parent = parent {'
            for prop in templateable_props:
                local_var_name = prop.value_resolving_local_var_name
                merged_with = f'parent.{prop.declaration_name}?.{prop.resolve_value_expression}'
                result += f'    _ = {{ {local_var_name} = {local_var_name}.merged(with: {{ {merged_with} }}) }}()'
            result += '  }'

        required_props = list(filter(lambda p: not p.parsed_value_is_optional, template_props))
        result += f'  {"let" if not required_props else "var"} errors = mergeErrors('
        for index, prop in enumerate(template_props):
            separator = '' if index == (len(template_props) - 1) else ','
            map_body = f'.nestedObjectError(field: "{prop.dict_field}", error: $0)'
            var_name = prop.value_resolving_local_var_name
            result += f'    {var_name}.errorsOrWarnings?.map {{ {map_body} }}{separator}'
        result += '  )'

        for prop in required_props:
            result += f'  if case .noValue = {prop.value_resolving_local_var_name} {{'
            field_error = f'.requiredFieldIsMissing(field: "{prop.dict_field}")'
            result += f'    errors.append({field_error})'
            result += '  }'

        if required_props:
            result += '  guard'
            for prop in required_props[:-1]:
                result += f'    let {prop.declaration_name}NonNil = {prop.value_resolving_local_var_name}.value,'
            last_prop = required_props[-1]
            result += f'    let {last_prop.declaration_name}NonNil = {last_prop.value_resolving_local_var_name}.value'
            result += '  else {'
            result += '    return .failure(NonEmptyArray(errors)!)'
            result += '  }'

        result += f'  let result = {return_type}('
        for index, prop in enumerate(template_props):
            name = prop.declaration_name
            suffix = 'Value.value' if prop.parsed_value_is_optional else 'NonNil'
            separator = '' if index == (len(template_props) - 1) else ','
            result += f'    {name}: {{ {name}{suffix} }}(){separator}'
        result += '  )'
        partial_success = '.partialSuccess(result, warnings: NonEmptyArray(errors)!'
        result += f'  return errors.isEmpty ? .success(result) : {partial_success})'
        result += '}'

        return result

    @property
    def resolve_template_declaration(self) -> Text:
        self_type = utils.capitalize_camel_case(self.name)

        result = Text(f'private func mergedWithParent(templates: [TemplateName: Any]) throws -> {self_type} {{')
        props = cast(List[SwiftProperty], self.instance_properties)
        if self.has_parent_property and props:
            compare_with_self_type = ''
            if self.static_type is not None:
                compare_with_self_type = ', parent != Self.type'

            result += f'  guard let parent = parent{compare_with_self_type} else {{ return self }}'
            result += f'  guard let parentTemplate = templates[parent] as? {self_type} else {{'
            result += '    throw DeserializationError.unknownType(type: parent)'
            result += '  }'
            result += '  let mergedParent = try parentTemplate.mergedWithParent(templates: templates)'
            result += EMPTY
            result += f'  return {self_type}('
            result += '    parent: nil,'
            for prop in props[:-1]:
                result += Text(f'{prop.parent_template_constructor_line},').indented(indent_width=4)
            result += f'    {props[-1].parent_template_constructor_line}'
            result += '  )'
        else:
            result += '  return self'
        result += '}'
        result += EMPTY
        result += f'public func resolveParent(templates: [TemplateName: Any]) throws -> {self_type} {{'
        if any(p.property_type.can_be_templated for p in props):
            result += '  let merged = try mergedWithParent(templates: templates)'
            result += EMPTY
            result += f'  return {self_type}('
            if self.has_parent_property:
                result += '    parent: nil,'
            tails = [','] * (len(props) - 1) + ['']
            for prop, tail in zip(props, tails):
                name = f'merged.{prop.declaration_name}'
                if prop.property_type.can_be_templated:
                    try_directive = '' if prop.parsed_value_is_optional else 'try '
                    resolve_parent_function = 'tryResolveParent' if prop.parsed_value_is_optional else 'resolveParent'
                    validator_decl = prop.validator_declaration
                    validator = '' if validator_decl is None else f', validator: Self.{prop.validator_var_name}'
                    expr = f'{try_directive}{name}?.{resolve_parent_function}(templates: templates{validator})'
                else:
                    expr = name
                result += Text(indent_width=4, init_lines=f'{prop.declaration_name}: {expr}{tail}')
            result += '  )'
        elif props:
            result += '  return try mergedWithParent(templates: templates)'
        else:
            result += '  return self'

        result += '}'
        return result

    @property
    def default_values_static_declaration(self) -> List[str]:
        default_values = []
        for p in self.properties_to_declare_swift:
            default_value = p.default_value_declaration
            if default_value is not None:
                declaration = f'public static let {p.static_default_value_name} = {default_value}'
                default_values.append(declaration)
        return default_values


class SwiftProperty(Property):
    class SwiftMode:
        def __init__(self, value: GenerationMode, use_expressions: bool):
            self.value: GenerationMode = value
            self.use_expressions: bool = use_expressions

    @property
    def swift_mode(self) -> SwiftMode:
        return SwiftProperty.SwiftMode(value=self.mode, use_expressions=self.supports_expressions)

    @property
    def declaration_name(self) -> str:
        return utils.fixing_first_digit(utils.lower_camel_case(self.name))

    @property
    def static_default_value_name(self) -> str:
        return utils.fixing_first_digit(utils.constant_upper_case(self.name)) + "_DEFAULT"

    @property
    def value_resolving_local_var_name(self) -> str:
        return self.declaration_name + 'Value'

    @property
    def resolve_value_expression(self) -> str:
        optional_or_empty = 'Optional' if self.parsed_value_is_optional else ''
        resolved_validator_arg = self.validator_arg(entity_name='ResolvedValue',
                                                    mode=GenerationMode.NORMAL_WITH_TEMPLATES)
        use_only_links_arg = ', useOnlyLinks: true' if self.property_type.can_be_templated else ''
        transformed = cast(SwiftPropertyType, self.property_type).transform_arg
        expr = f'context: context{transformed}{resolved_validator_arg}{use_only_links_arg}'
        return f'resolve{optional_or_empty}Value({expr})'

    @property
    def should_be_optional(self) -> bool:
        return self.optional and self.default_value is None

    def declaration(self, access_level: SwiftAccessLevel) -> Text:
        if isinstance(self.property_type, StaticString):
            name = utils.lower_camel_case(self.name)
            value = self.property_type.value
            return Text(f'{access_level.value}static let {name}: String = "{value}"')
        else:
            name = fixing_keywords(self.declaration_name)
            type_declaration = self.type_declaration
            comment = declaration_comment(self, _swift_default_value_declaration_comment)
            return Text(f'{access_level.value}let {name}: {type_declaration}{comment}')

    def expression_resolving_method(self, access_level: SwiftAccessLevel) -> Text:
        result = Text(f'{self.expression_resolving_method_declaration(access_level)} {{')
        if isinstance(self.property_type, Array):
            item_type = cast(SwiftPropertyType, self.property_type.property_type)
            method = self.expression_resolving_method_name(item_type)
            map_text = f'resolver.{method}($0)'
            result += Text(f'{self.declaration_name}.map {{ {map_text} }}.compactMap {{ $0 }}').indented()
        else:
            prop_type = cast(SwiftPropertyType, self.property_type)
            method = self.expression_resolving_method_name(prop_type)
            default_value = self.expression_resolving_method_default_value(prop_type)
            expression_str = f'{self.declaration_name}'
            result += Text(f'resolver.{method}({expression_str}){default_value}').indented()
        result += '}'
        result += EMPTY
        return result

    def expression_resolving_method_name(self, property_type: SwiftPropertyType) -> str:
        if isinstance(property_type, Color):
            return 'resolveColor'
        elif isinstance(property_type, String):
            return 'resolveString'
        elif isinstance(property_type, Url):
            return 'resolveUrl'
        elif isinstance(property_type, (Int, Double, Bool, BoolInt)):
            return 'resolveNumeric'
        elif isinstance(property_type, Object) and isinstance(property_type.object, StringEnumeration):
            return 'resolveEnum'
        elif isinstance(property_type, RawArray):
            return 'resolveArray'
        elif isinstance(property_type, Array):
            return self.expression_resolving_method_name(cast(SwiftPropertyType, property_type.property_type))
        else:
            raise NotImplementedError

    @property
    def type_declaration(self) -> str:
        if self.mode in [GenerationMode.NORMAL_WITH_TEMPLATES, GenerationMode.NORMAL_WITHOUT_TEMPLATES]:
            optionality = '?' if self.should_be_optional else ''
            return cast(SwiftPropertyType, self.property_type).declaration(self.swift_mode) + optionality
        elif self.mode is GenerationMode.TEMPLATE:
            return f'Field<{cast(SwiftPropertyType, self.property_type).declaration(self.swift_mode)}>?'

    def expression_declaration(self, optionality: bool = False) -> str:
        mode = SwiftProperty.SwiftMode(value=self.swift_mode.value, use_expressions=False)
        return cast(SwiftPropertyType, self.property_type).declaration(mode) + ('?' if optionality else '')

    def expression_resolving_method_declaration(self, access_level: SwiftAccessLevel) -> str:
        result = self.expression_declaration(optionality=self.default_value_declaration is None)
        access = access_level.value
        name = utils.capitalize_camel_case(self.name)
        return f'{access}func resolve{name}(_ resolver: ExpressionResolver) -> {result}'

    @property
    def default_value_declaration(self) -> Optional[str]:
        if self.default_value is None:
            return None
        return cast(SwiftPropertyType, self.property_type).internal_declaration(self.default_value)

    def expression_resolving_method_default_value(self, prop_type: SwiftPropertyType) -> str:
        default_value_declaration = self.default_value_declaration
        if default_value_declaration is None:
            return ''
        prefix = ''
        if isinstance(prop_type, Object) and isinstance(prop_type.object, StringEnumeration):
            prefix = self.expression_declaration(optionality=False)
        return f' ?? {prefix}{default_value_declaration}'

    @property
    def validator_var_name(self) -> str:
        return f'{self.declaration_name}Validator'

    @property
    def validator_var_type(self) -> str:
        if isinstance(self.property_type, Array):
            array_or_empty = 'Array'
            item_type = cast(SwiftPropertyType, self.property_type.property_type)
            generic_arg = item_type.prefixed_declaration(self.swift_mode)
        else:
            array_or_empty = ''
            prop_type = cast(SwiftPropertyType, self.property_type)
            generic_arg = prop_type.prefixed_declaration(SwiftProperty.SwiftMode(value=self.swift_mode.value,
                                                                                 use_expressions=False))
        return f'Any{array_or_empty}ValueValidator<{generic_arg}>'

    @property
    def validator_declaration(self) -> Optional[Text]:
        validator = self.internal_validator_declaration(mode=self.mode)
        if validator is None:
            return validator
        result = Text(f'static let {self.validator_var_name}: {self.validator_var_type} =')
        result += utils.indented(validator)
        return result

    def internal_validator_declaration(self, mode: GenerationMode) -> Optional[str]:
        if mode is GenerationMode.TEMPLATE:
            return None
        if isinstance(self.property_type, RawArray) and self.property_type.min_items > 0:
            validator_name = 'makeArrayValidator'
            validator_args = [f'minItems: {self.property_type.min_items}']
        elif isinstance(self.property_type, Array) and self.property_type.min_items > 0:
            validator_name = 'makeArrayValidator'
            validator_args = [f'minItems: {self.property_type.min_items}']
        elif isinstance(self.property_type, Array):
            return None
        elif isinstance(self.property_type, String) and \
                (self.property_type.min_length > 0 or self.property_type.regex is not None):
            validator_name = 'makeStringValidator'
            min_length = self.property_type.min_length
            validator_args = []
            if min_length > 0:
                validator_args.append(f'minLength: {min_length}')
            if self.property_type.regex is not None:
                escaped_pattern = self.property_type.regex.pattern.replace('\\', '\\\\')
                validator_args.append(f'regex: "{escaped_pattern}"')
        elif isinstance(self.property_type, Url) and self.property_type.schemes is not None:
            validator_name = 'makeURLValidator'
            joined = ', '.join(map(lambda val: f'"{val}"', self.property_type.schemes))
            validator_args = [f'schemes: [{joined}]']
        elif isinstance(self.property_type, (Int, Double)):
            constraint = self.property_type.constraint
            if constraint is None:
                return None
            constraint = constraint.replace('number', '$0')
            validator_name = 'makeValueValidator'
            validator_args = [f'valueValidator: {{ {constraint} }}']
        else:
            return None

        prefix = f'{validator_name}('
        separator = f',\n{" " * len(prefix)}'
        return f'{prefix}{separator.join(validator_args)})'

    def validator_arg(self, entity_name: str, mode: GenerationMode) -> str:
        if self.internal_validator_declaration(mode) is None:
            return ''
        return f', validator: {entity_name}.{self.validator_var_name}'

    @property
    def parsed_value_is_optional(self) -> bool:
        return self.optional or self.default_value is not None

    @property
    def deserialization_expression(self) -> str:
        if isinstance(self.property_type, Array):
            type_suffix = 'Array'
        else:
            type_suffix = 'Field'
        if self.property_type.can_be_templated:
            template_to_type_arg = swift_template_deserializable_args(self.mode)
        else:
            template_to_type_arg = ''
        optional_suffix = 'Optional' if self.parsed_value_is_optional or self.mode.is_template else ''
        expression_suffix = 'Expression' if self.supports_expressions else ''
        validator_arg_string = self.validator_arg(entity_name='Self', mode=self.mode)
        transformed = cast(SwiftPropertyType, self.property_type).transform_arg
        expr = f'"{self.dict_field}"{template_to_type_arg}{transformed}{validator_arg_string}'
        return f'get{optional_suffix}{expression_suffix}{type_suffix}({expr})'

    def add_default_value_to(self, declaration: str, public_default_value: bool = False) -> str:
        prop_type = cast(SwiftPropertyType, self.property_type)
        if self.default_value is not None:
            default_value_declaration_to_use = prop_type.internal_declaration(self.default_value) \
                if not public_default_value else f'Self.{self.static_default_value_name}'
        else:
            return declaration

        if self.supports_expressions:
            default_value_declaration_to_use = f'.value({default_value_declaration_to_use})'

        return f'{declaration} ?? {default_value_declaration_to_use}'

    @property
    def deserialize_from_value_expression(self) -> str:
        template_args = ''
        if self.property_type.can_be_templated:
            template_args = ', templates: context.templates, templateToType: context.templateToType'
        validator_arg_str = self.validator_arg(entity_name='ResolvedValue',
                                               mode=GenerationMode.NORMAL_WITH_TEMPLATES)
        type_arg = ''
        if self.property_type.can_be_templated:
            if isinstance(self.property_type, Array):
                prop = cast(SwiftPropertyType, self.property_type.property_type)
            else:
                prop = cast(SwiftPropertyType, self.property_type)
            type_arg = f', type: {prop.prefixed_declaration(self.swift_mode)}.self'
        transform = cast(SwiftPropertyType, self.property_type).transform_arg
        return f'deserialize(__dictValue{template_args}{transform}{validator_arg_str}{type_arg})'

    @property
    def parent_template_constructor_line(self) -> str:
        return f'{self.declaration_name}: {self.declaration_name} ?? mergedParent.{self.declaration_name}'

    @property
    def serialization_declaration(self) -> str:
        prefix = f'result["{self.dict_field}"] = '
        prop = cast(SwiftPropertyType, self.property_type)
        suffix = prop.serialization_suffix(self.supports_expressions)
        return f'{prefix}{self.declaration_name}{"?" if self.should_be_optional and suffix else ""}{suffix}'


class SwiftPropertyType(PropertyType):
    def declaration(self, mode: SwiftProperty.SwiftMode) -> str:
        return self._declaration(prefixed=False, mode=mode)

    def prefixed_declaration(self, mode: SwiftProperty.SwiftMode) -> str:
        return self._declaration(prefixed=True, mode=mode)

    def _declaration(self, prefixed: bool, mode: SwiftProperty.SwiftMode) -> str:
        result = self.__declaration(prefixed, mode)
        if mode.use_expressions and not isinstance(self, Array):
            return f'Expression<{result}>'
        else:
            return result

    def __declaration(self, prefixed: bool, mode: SwiftProperty.SwiftMode) -> str:
        if isinstance(self, Int):
            return 'Int'
        elif isinstance(self, Double):
            return 'Double'
        elif isinstance(self, (Bool, BoolInt)):
            return 'Bool'
        elif isinstance(self, String):
            return 'String'
        elif isinstance(self, StaticString):
            raise TypeError
        elif isinstance(self, Url):
            return 'URL'
        elif isinstance(self, Color):
            return 'Color'
        elif isinstance(self, Dictionary):
            return '[String: Any]'
        elif isinstance(self, RawArray):
            return '[Any]'
        elif isinstance(self, Object):
            if self.name.startswith('$predefined_'):
                return self.name.replace('$predefined_', '')
            prefix: str = ''
            if prefixed:
                obj = self.object
                if obj is not None:
                    if mode.value in [GenerationMode.NORMAL_WITH_TEMPLATES,
                                      GenerationMode.NORMAL_WITHOUT_TEMPLATES]:
                        prefix = obj.resolved_declaration_prefix
                    elif mode.value is GenerationMode.TEMPLATE:
                        prefix = obj.template_declaration_prefix
            name_to_use: str
            obj = self.object
            if isinstance(obj, Entity):
                name_to_use = obj.resolved_name + mode.value.name_suffix
            elif isinstance(obj, EntityEnumeration):
                name_to_use = obj.resolved_name + mode.value.name_suffix
            elif isinstance(obj, StringEnumeration):
                name_to_use = obj.name
            else:
                raise TypeError
            return prefix + utils.capitalize_camel_case(name_to_use)
        elif isinstance(self, Array):
            element_type = cast(SwiftPropertyType, self.property_type)
            return f'[{element_type._declaration(prefixed, mode)}]'

    def internal_declaration(self, default_value: str) -> Optional[str]:
        if isinstance(self, (Int, Bool, BoolInt, Double)):
            return default_value
        elif isinstance(self, String):
            value_with_escaping_quotes = default_value.replace('"', '\"')
            return f'"{value_with_escaping_quotes}"'
        elif isinstance(self, Url):
            return f'URL(string: "{default_value}")!'
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
            return f'Color.colorWithARGBHexCode(0x{color_argb_hex})'
        elif isinstance(self, Array):
            without_whitespaces = default_value.replace(' ', '').replace('\n', '')
            if not without_whitespaces.startswith('[') or not without_whitespaces.endswith(']'):
                return None
            if without_whitespaces == '[]':
                values = []
            else:
                values = without_whitespaces[1:-1].split(',')
            item_type = cast(SwiftPropertyType, self.property_type)
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
                return f'.{utils.fixing_first_digit(utils.lower_camel_case(enum_case[0]))}'
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
                obj = cast(SwiftPropertyType, Object(name='', object=enum_case[1], format=ObjectFormat.DEFAULT))
                case_constructor: Optional[str] = obj.internal_declaration(default_value)
                if case_constructor is None:
                    return None
                return f'.{utils.lower_camel_case(enum_case[0])}({case_constructor})'

            entity: SwiftEntity = cast(SwiftEntity, self.object)
            entity.__class__ = SwiftEntity
            args = []
            for prop in entity.instance_properties_swift:
                str_type = default_value_dict.get(prop.dict_field)
                if str_type is None:
                    continue
                declaration = cast(SwiftPropertyType, prop.property_type).internal_declaration(str_type)
                default_value = f'.value({declaration})' if prop.supports_expressions else declaration
                args.append(f'{prop.declaration_name}: {default_value}')
            args = ', '.join(args)
            return f'{entity.declaration_prefix}{utils.capitalize_camel_case(entity.original_name)}({args})'
        elif isinstance(self, Dictionary):
            return f'(try! JSONSerialization.jsonObject(jsonString: """\n{default_value}\n""") as! [String: Any])'
        else:
            return None

    @property
    def transform_arg(self) -> str:
        prefix = ', transform: '
        if isinstance(self, Url):
            return f'{prefix}URL.init(string:)'
        elif isinstance(self, Color):
            return f'{prefix}Color.color(withHexString:)'
        elif isinstance(self, Object):
            if self.format is ObjectFormat.DEFAULT:
                return ''
            return f'{prefix}{utils.capitalize_camel_case(self.name)}.init(JSONString:)'
        elif isinstance(self, Array):
            return cast(SwiftPropertyType, self.property_type).transform_arg
        else:
            return ''

    @property
    def is_equatable(self) -> bool:
        if isinstance(self, (Dictionary, RawArray)):
            return False
        elif isinstance(self, Array):
            return cast(SwiftPropertyType, self.property_type).is_equatable
        else:
            return True

    def serialization_suffix(self, use_expressions: bool) -> str:
        if isinstance(self, (Dictionary)):
            return ''
        elif isinstance(self, (String, Int, Double, Bool, BoolInt, RawArray)):
            return '.toValidSerializationValue()' if use_expressions else ''
        elif isinstance(self, Object):
            if isinstance(self.object, StringEnumeration):
                return '.toValidSerializationValue()' if use_expressions else '.rawValue'
            else:
                return '.toDictionary()'
        elif isinstance(self, Url):
            return '.toValidSerializationValue()' if use_expressions else '.absoluteString'
        elif isinstance(self, Color):
            return '.toValidSerializationValue()' if use_expressions else '.hexString'
        elif isinstance(self, Array):
            suffix = cast(SwiftPropertyType, self.property_type).serialization_suffix(use_expressions)
            return '' if not suffix else f'.map {{ $0{suffix} }}'
        elif isinstance(self, StaticString):
            raise TypeError('Can\'t serialize static value')


class SwiftEntityEnumeration(EntityEnumeration):
    def resolve_parent_implementation(self, access_level: SwiftAccessLevel) -> Text:
        access = access_level.value
        name = utils.capitalize_camel_case(self.name)
        result = Text(f'{access}func resolveParent(templates: [TemplateName: Any]) throws -> {name} {{')
        result += '  switch self {'
        for name in self._resolved_entity_names:
            lower_name = utils.lower_camel_case(name)
            result += f'  case let .{lower_name}Template(value):'
            result += f'    return .{lower_name}Template(try value.resolveParent(templates: templates))'
        result += '  }'
        result += '}'
        return result

    def resolve_value_implementation(self, access_level: SwiftAccessLevel) -> Text:
        access = access_level.value
        name = utils.capitalize_camel_case(self.name)
        params = f'context: TemplatesContext, parent: {name}?, useOnlyLinks: Bool'
        return_type = f'DeserializationResult<{self.resolved_prefixed_declaration}>'
        result = Text(f'{access}static func resolveValue({params}) -> {return_type} {{')
        result += '  guard let parent = parent else {'
        result += '    if useOnlyLinks {'
        result += '      return .failure(NonEmptyArray(.missingType(representation: context.templateData)))'
        result += '    } else {'
        result += '      return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)'
        result += '    }'
        result += '  }'
        result += EMPTY
        result += '  return {'
        result += f'    var result: DeserializationResult<{self.resolved_prefixed_declaration}>!'
        for name in self._resolved_entity_names:
            lower_name = utils.lower_camel_case(name)
            result += '    result = result ?? {'
            result += f'      if case let .{lower_name}Template(value) = parent {{'
            result += '        let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)'
            result += '        switch result {'
            result += f'          case let .success(value): return .success(.{lower_name}(value))'
            result += f'          case let .partialSuccess(value, warnings): return .partialSuccess(.{lower_name}(value), warnings: warnings)'
            result += '          case let .failure(errors): return .failure(errors)'
            result += '          case .noValue: return .noValue'
            result += '        }'
            result += '      } else { return nil }'
            result += '    }()'
        result += '    return result'
        result += '  }()'
        result += '}'
        return result

    @property
    def resolve_unknown_value_implementation(self) -> Text:
        params = 'context: TemplatesContext, useOnlyLinks: Bool'
        return_type = f'DeserializationResult<{self.resolved_prefixed_declaration}>'
        result = Text(f'private static func resolveUnknownValue({params}) -> {return_type} {{')
        if self.default_entity_declaration:
            default_type = utils.capitalize_camel_case(self.default_entity_declaration)
            result += f'  let type = (context.templateData["type"] as? String ?? {default_type}.type)'
            result += '    .flatMap { context.templateToType[$0] ?? $0 } '
        else:
            result += '  guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {'
            result += '    return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))'
            result += '  }'
        result += EMPTY
        result += '  return {'
        result += f'    var result: DeserializationResult<{self.resolved_prefixed_declaration}>?'
        for name in self._resolved_entity_names:
            upper_name = utils.capitalize_camel_case(name)
            lower_name = utils.lower_camel_case(name)
            result += f'  result = result ?? {{ if type == {upper_name}.type {{'
            result += f'    let result = {{ {upper_name}Template.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }}()'
            result += '    switch result {'
            result += f'    case let .success(value): return .success(.{lower_name}(value))'
            result += f'    case let .partialSuccess(value, warnings): return .partialSuccess(.{lower_name}(value), warnings: warnings)'
            result += '    case let .failure(errors): return .failure(errors)'
            result += '    case .noValue: return .noValue'
            result += '    }'
            result += '  } else { return nil } }()'
        result += '  return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))'
        result += '  }()'
        result += '}'
        return result
