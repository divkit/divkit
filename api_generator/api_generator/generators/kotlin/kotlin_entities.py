from __future__ import annotations
from functools import reduce
import dataclasses
from typing import List, Optional, cast

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
    ObjectFormat
)
from ...config import GenerationMode
from ... import utils
from ...schema.modeling.text import Text, EMPTY

EXPRESSION_TYPE_NAME = 'Expression'
EXPRESSION_LIST_TYPE_NAME = 'ExpressionsList'
PARSING_ERRORS_PROP_NAME = "parsingErrors"
ENTITY_STATIC_CREATOR = 'CREATOR'


def _number_validator_decl(type: str, constraint: Optional[str]) -> Optional[str]:
    if constraint is None:
        return None
    return f'{{ it: {type} -> {constraint.replace("number", "it")} }}'


def _kotlin_default_value_declaration_comment(p: Property) -> str:
    if isinstance(p.property_type, Object) and not isinstance(p.property_type.object, StringEnumeration):
        property_type = cast(KotlinPropertyType, Object(name='',
                                                        object=p.property_type.object,
                                                        format=ObjectFormat.DEFAULT))
        comment_value = property_type.declaration_by_default_value(p.default_value, True)
    else:
        comment_value = p.default_value
    return comment_value


class KotlinEntity(Entity):
    errors_collector_enabled: bool

    def update_bases(self):
        Int.__bases__ = (KotlinPropertyType, PropertyType,)
        Bool.__bases__ = (KotlinPropertyType, PropertyType,)
        BoolInt.__bases__ = (KotlinPropertyType, PropertyType,)
        Double.__bases__ = (KotlinPropertyType, PropertyType,)
        StaticString.__bases__ = (KotlinPropertyType, PropertyType,)
        Object.__bases__ = (KotlinPropertyType, PropertyType,)
        Array.__bases__ = (KotlinPropertyType, PropertyType,)
        Url.__bases__ = (KotlinPropertyType, PropertyType,)
        Color.__bases__ = (KotlinPropertyType, PropertyType,)
        String.__bases__ = (KotlinPropertyType, PropertyType,)
        Dictionary.__bases__ = (KotlinPropertyType, PropertyType,)
        for prop in self.properties:
            prop.__class__ = KotlinProperty

    @property
    def properties_kotlin(self) -> List[KotlinProperty]:
        result = []
        for prop in self.properties:
            prop.__class__ = KotlinProperty
            result.append(cast(KotlinProperty, prop))
        return result

    @property
    def instance_properties_kotlin(self) -> List[KotlinProperty]:
        result = []
        for prop in self.instance_properties:
            prop.__class__ = KotlinProperty
            result.append(cast(KotlinProperty, prop))
        return result

    def eval_errors_collector_enabled(self, errors_collectors: List[str]):
        self.errors_collector_enabled = not self.generation_mode.is_template and self.original_name in errors_collectors

    def constructor_body(self, with_commas: bool, extra_properties: List[str] = None) -> Text:
        if extra_properties is None:
            extra_properties = []
        if not self.instance_properties and not extra_properties:
            return Text()
        expressions = []
        for prop in self.instance_properties_kotlin:
            expressions.append(prop.deserialization_declaration(mode=self.generation_mode))
        expressions.extend(extra_properties)
        result = Text()
        for ind, expr in enumerate(expressions):
            comma = ''
            if with_commas and ind != (len(expressions) - 1):
                comma = ','
            result += f'{expr}{comma}'
        return result

    @property
    def value_resolving_declaration(self) -> Text:
        args = 'env: ParsingEnvironment, data: JSONObject'
        result = Text(f'override fun resolve({args}): {self.resolved_prefixed_declaration} {{')
        if not self.instance_properties:
            result += f'    return {self.resolved_prefixed_declaration}()'
        else:
            result += f'    return {self.resolved_prefixed_declaration}('
            prop_decl = Text()
            props = self.instance_properties_kotlin
            for ind, p in enumerate(props):
                ending = ',' if ind != (len(props) - 1) else ''
                template_deserialization = p.make_template_deserialization(dict_field=p.dict_field,
                                                                           value_override=None)
                prop_decl += f'{template_deserialization}{ending}'
            result += prop_decl.indented(indent_width=8)
            result += '    )'
        result += '}'
        return result

    @property
    def serialization_declaration(self) -> Text:
        result = Text('override fun writeToJSON(): JSONObject {')
        result += '    val json = JSONObject()'
        for prop in self.properties_kotlin:
            result += prop.serialization_declaration.indented(indent_width=4)
        result += '    return json'
        result += '}'
        return result

    @property
    def static_declarations(self) -> Text:
        properties_kotlin = self.properties_kotlin
        is_template = self.generation_mode.is_template
        name = utils.capitalize_camel_case(self.name)
        static_properties = list(filter(lambda p: isinstance(p.property_type, StaticString), properties_kotlin))
        default_values = []
        instance_properties_kotlin = self.instance_properties_kotlin
        for p in instance_properties_kotlin:
            decl = p.default_value_declaration
            if decl is not None:
                default_values.append(decl)
        type_helpers = []
        for p in filter(lambda p: cast(KotlinPropertyType, p.property_type).is_enum_of_expressions,
                        instance_properties_kotlin):
            type_helpers.append(p.type_helper_declaration)
        groups = []
        if static_properties:
            static_decl = Text()
            for static in static_properties:
                static_decl += static.declaration(overridden=False,
                                                  in_interface=False,
                                                  with_comma=False,
                                                  with_default=False)
            groups.append(static_decl)
        if default_values:
            groups.append(Text(default_values))

        if type_helpers:
            groups.append(Text(type_helpers))

        if not is_template:
            constructor = Text()
            constructor += '@JvmStatic'
            constructor += '@JvmName("fromJson")'
            constructor += f'operator fun invoke(env: ParsingEnvironment, json: JSONObject): {name} {{'

            if self.errors_collector_enabled:
                constructor += '    val env = env.withErrorsCollector()'

            constructor += '    val logger = env.logger'
            constructor += f'    return {name}('
            extra_properties = []
            if self.errors_collector_enabled:
                extra_properties.append(f'{PARSING_ERRORS_PROP_NAME} = env.collectErrors()')

            constructor_body = self.constructor_body(with_commas=True,
                                                     extra_properties=extra_properties).indented(indent_width=8)
            if constructor_body.lines:
                constructor += constructor_body
            constructor += utils.indented(')', indent_width=4)
            constructor += '}'
            groups.append(constructor)

        validators = Text()
        for p in properties_kotlin:
            validator_or_empty = p.static_validator_expression(with_template_validators=is_template)
            if not str(validator_or_empty):
                continue
            validators += validator_or_empty
        if validators.lines:
            groups.append(validators)

        if is_template:
            readers = Text()
            for p in properties_kotlin:
                readers += p.static_reader_deserialization_expression
            groups.append(readers)
        static_creator_lambda = f'env: ParsingEnvironment, it: JSONObject -> {name}(env, json = it)'
        groups.append(Text(f'val {ENTITY_STATIC_CREATOR} = {{ {static_creator_lambda} }}'))

        result = Text()
        for ind, group in enumerate(groups):
            result += group
            if ind != (len(groups) - 1):
                result += EMPTY
        return result


class KotlinProperty(Property):
    @property
    def declaration_name(self) -> str:
        if isinstance(self.property_type, StaticString):
            name = utils.constant_upper_case(self.name)
        else:
            name = utils.lower_camel_case(self.name)
        return utils.fixing_first_digit(name)

    @property
    def default_value_var_name(self) -> str:
        return f'{utils.constant_upper_case(self.declaration_name)}_DEFAULT_VALUE'

    @property
    def default_value_declaration(self) -> Optional[str]:
        default_value_definition = self.default_value_definition
        if default_value_definition is not None:
            return f'private val {self.default_value_var_name} = {default_value_definition}'
        return None

    @property
    def default_value_definition(self) -> Optional[str]:
        if self.default_value is not None:
            declaration = cast(KotlinPropertyType, self.property_type).declaration_by_default_value(
                default_value=self.default_value,
                string_enum_prefixed=self.mode.is_template
            )
            if declaration is not None:
                return declaration
        empty_dict_deserialization = cast(KotlinPropertyType, self.property_type).empty_dict_deserialization
        if empty_dict_deserialization is not None:
            return empty_dict_deserialization
        return None

    @property
    def should_be_optional(self) -> bool:
        prop_type = cast(KotlinPropertyType, self.property_type)
        if self.mode.is_template:
            return False
        return self.optional and (self.default_value is None) and prop_type.empty_dict_deserialization is None

    @property
    def parsed_value_is_optional(self) -> bool:
        prop_type = cast(KotlinPropertyType, self.property_type)
        return self.optional or self.default_value is not None or prop_type.empty_dict_deserialization is not None

    @property
    def use_expression_type(self) -> bool:
        prop = cast(KotlinPropertyType, self.property_type)
        if prop.is_array_of_expressions:
            return False
        return self.supports_expressions or prop.is_enum_of_expressions

    @property
    def type_declaration(self) -> str:
        if self.mode.is_template:
            if self.use_expression_type:
                prefix = f'Field<{EXPRESSION_TYPE_NAME}<'
                suffix = '>>'
            else:
                prefix = 'Field<'
                suffix = '>'
        else:
            if self.use_expression_type:
                prefix = f'{EXPRESSION_TYPE_NAME}<'
                suffix = '>'
            else:
                prefix = ''
                suffix = ''
        type_decl = cast(KotlinPropertyType, self.property_type).declaration(self.mode)
        return f'{prefix}{type_decl}{suffix}{"?" if self.should_be_optional else ""}'

    def declaration(self, overridden: bool, in_interface: bool, with_comma: bool, with_default: bool) -> Text:
        if isinstance(self.property_type, StaticString):
            assert not overridden and not with_comma
            return Text(f'const val {self.declaration_name} = "{self.property_type.value}"')
        if overridden:
            prefix = 'override '
            assert not in_interface
        elif not in_interface:
            prefix = '@JvmField final '
        else:
            prefix = ''
        comma = ',' if with_comma else ''
        default_assignment = ''
        if with_default:
            if self.should_be_optional:
                default_assignment = ' = null'
            elif self.default_value_declaration is not None:
                default_assignment = f' = {self.default_value_var_name}'
        comment = declaration_comment(self, _kotlin_default_value_declaration_comment)
        return Text(f'{prefix}val {self.declaration_name}: {self.type_declaration}{default_assignment}{comma}{comment}')

    def deserialization_declaration(self, mode: GenerationMode) -> str:
        deserialization_expr = self.deserialization_expression(mode=mode, reuse_logger_instance=True)
        return f'{self.declaration_name} = {deserialization_expr}{self.default_value_coalescing(mode)}'

    def deserialization_expression(self, mode: GenerationMode, reuse_logger_instance: bool) -> str:
        if isinstance(self.property_type, Array):
            strict = 'Strict' if self.property_type.strict_parsing else ''
            if self.supports_expressions and cast(KotlinPropertyType, self.property_type).is_array_of_expressions:
                list_or_empty = f'{strict}{EXPRESSION_LIST_TYPE_NAME}'
            else:
                list_or_empty = f'{strict}List'
            expression_or_empty = ''
            expression_suffix_or_empty = ''
        else:
            if self.supports_expressions or cast(KotlinPropertyType, self.property_type).is_enum_of_expressions:
                expression_or_empty = EXPRESSION_TYPE_NAME
                expression_suffix_or_empty = 'WithExpression'
            else:
                expression_or_empty = ''
                expression_suffix_or_empty = ''
            list_or_empty = ''
        optionality = 'Optional' if self.parsed_value_is_optional else ''
        if reuse_logger_instance:
            logger_arg = 'logger'
        else:
            logger_arg = 'env.logger'
        if mode.is_template:
            method_name = f'read{optionality}{list_or_empty}Field{expression_suffix_or_empty}'
            key_value = f'"{self.dict_field}"'
            template_args = f'topLevel, parent?.{self.declaration_name}'
        else:
            method_name = f'read{optionality}{expression_or_empty}{list_or_empty}'
            key_value = 'key' if self.mode.is_template else f'"{self.dict_field}"'
            template_args = ''
        creator = self.creator_declaration(mode=mode) or ''
        kotlin_type = cast(KotlinPropertyType, self.property_type)
        transform = kotlin_type.deserialization_transform(
            string_enum_prefixed=self.mode.is_template
        )
        arg_list = ['json', key_value, template_args, creator, transform,
                    self.validator_arg(with_template_validators=mode.is_template), logger_arg, 'env']

        if self.supports_expressions and not mode.is_template and self.default_value_definition is not None:
            arg_list.append(self.default_value_var_name)

        if self.supports_expressions or kotlin_type.is_array_of_expressions or kotlin_type.is_enum_of_expressions:
            arg_list.append(kotlin_type.type_helper_reference(self))

        args = ', '.join(filter(lambda s: s, arg_list))
        receiver = 'JsonTemplateParser.' if mode.is_template else 'JsonParser.'
        return f'{receiver}{method_name}({args})'

    def creator_declaration(self, mode: GenerationMode) -> Optional[str]:
        creator_type = None
        if isinstance(self.property_type, Object) and not isinstance(self.property_type.object, StringEnumeration):
            creator_type = self.property_type
        elif isinstance(self.property_type, Array):
            item = self.property_type.property_type
            if isinstance(item, Object) and not isinstance(item.object, StringEnumeration):
                creator_type = item

        if creator_type is None:
            return None

        type_decl = cast(KotlinPropertyType, creator_type).declaration_by_prefixed(
            prefixed=self.mode.is_template and self.mode != mode,
            mode=mode
        )

        return f'{type_decl}.{ENTITY_STATIC_CREATOR}'

    def validator_arg(self, with_template_validators: bool) -> str:
        validator_definition = self.validator_definition
        if validator_definition is None:
            return ''
        return self.validator_instance_name(with_template_validators)

    def validator_instance_name(self, with_templates: bool) -> str:
        name = utils.constant_upper_case(self.name)
        return f'{name}_TEMPLATE_VALIDATOR' if with_templates else f'{name}_VALIDATOR'

    @property
    def validator_definition(self) -> Optional[str]:
        if isinstance(self.property_type, Array) and self.property_type.min_items > 0:
            return f'{{ it: List<*> -> it.size >= {self.property_type.min_items} }}'
        elif isinstance(self.property_type, String) and (
            self.property_type.min_length > 0 or
                self.optional or self.property_type.regex is not None):
            expressions = []
            length_field = 'rawLength' if self.property_type.formatted else 'length'
            min_length = self.property_type.min_length
            actual_min_length = 1 if min_length == 0 and self.optional else min_length
            if actual_min_length > 0:
                expressions.append(f'it.{length_field} >= {actual_min_length}')
            regex = self.property_type.regex
            if regex is not None:
                escaped_pattern = regex.pattern.replace('\\', '\\\\')
                expressions.append(f'it: String -> it.doesMatch("{escaped_pattern}")')
            if not expressions:
                return ''
            return f'{{ it: String -> {" && ".join(expressions)} }}'
        elif isinstance(self.property_type, Url) and self.property_type.schemes is not None:
            scheme_list = ', '.join(map(lambda s: f'"{s}"', self.property_type.schemes))
            return f'{{ it.hasScheme(listOf({scheme_list})) }}'
        elif isinstance(self.property_type, Int):
            return _number_validator_decl('Int', self.property_type.constraint)
        elif isinstance(self.property_type, Double):
            return _number_validator_decl('Double', self.property_type.constraint)
        else:
            return None

    def default_value_coalescing(self, mode: GenerationMode) -> str:
        if not mode.is_template and self.default_value_definition is not None:
            return f' ?: {self.default_value_var_name}'
        return ''

    def make_template_deserialization(self, dict_field: str, value_override: Optional[str]) -> str:
        optionality = 'Optional' if self.optional else ''
        plain_or_empty = 'Template' if self.property_type.can_be_templated else ''
        if value_override is None:
            value_override = ''
        else:
            value_override = f', valueOverride = {value_override}'
        reader = self.reader_declaration_name
        if isinstance(self.property_type, Array):
            kotlin_prop = cast(KotlinPropertyType, self.property_type)
            if kotlin_prop.is_array_of_expressions:
                list_or_empty = 'ExpressionList'
                validator = ''
                expression_or_empty = ''
            else:
                list_or_empty = 'List'
                validator = f', {self.validator_arg(with_template_validators=False)}'
                expression_or_empty = ''
                if self.supports_expressions and not kotlin_prop.is_enum_of_expressions:
                    expression_or_empty = EXPRESSION_TYPE_NAME
        else:
            list_or_empty = ''
            validator = ''
            expression_or_empty = ''
        method_name = f'.resolve{optionality}{plain_or_empty}{expression_or_empty}{list_or_empty}'
        method_args = f'env = env, key = "{dict_field}", data = data{validator}{value_override}, reader = {reader}'
        def_val = self.default_value_coalescing(mode=GenerationMode.NORMAL_WITHOUT_TEMPLATES)
        return f'{self.declaration_name} = {self.declaration_name}{method_name}({method_args}){def_val}'

    @property
    def reader_declaration_name(self) -> str:
        return f'{utils.constant_upper_case(self.name)}_READER'

    @property
    def serialization_declaration(self) -> Text:
        is_field = self.mode.is_template and not isinstance(self.property_type, StaticString)
        suffix = 'Field' if is_field else ''
        value_arg = 'field' if is_field else 'value'
        if self.use_expression_type:
            if is_field:
                expression_prefix_or_empty = ''
                expression_suffix_or_empty = 'WithExpression'
            else:
                expression_prefix_or_empty = EXPRESSION_TYPE_NAME
                expression_suffix_or_empty = ''
        else:
            expression_prefix_or_empty = ''
            if cast(KotlinPropertyType, self.property_type).is_array_of_expressions:
                expression_prefix_or_empty = EXPRESSION_LIST_TYPE_NAME
            expression_suffix_or_empty = ''
        serialization_transform = cast(KotlinPropertyType, self.property_type).serialization_transform(
            string_enum_prefixed=self.mode.is_template
        )
        args = f'key = "{self.dict_field}", {value_arg} = {self.declaration_name}{serialization_transform}'
        return Text(f'json.write{expression_prefix_or_empty}{suffix}{expression_suffix_or_empty}({args})')

    @property
    def type_helper_declaration(self) -> str:
        type_decl = cast(KotlinPropertyType, self.property_type).prefixed_declaration(
            mode=GenerationMode.NORMAL_WITHOUT_TEMPLATES)
        definition = f'TypeHelper.from(default = {type_decl}.values().first()) {{ it is {type_decl} }}'
        return f'private val {cast(KotlinPropertyType, self.property_type).type_helper_reference(self)} = {definition}'

    def static_validator_expression(self, with_template_validators: bool) -> Text:
        result = Text()
        definition = self.validator_definition or ''
        if not definition:
            return result

        if isinstance(self.property_type, Array) and self.property_type.min_items > 0:
            item_type = cast(KotlinPropertyType, self.property_type.property_type)
            list_type = item_type.prefixed_declaration(
                GenerationMode.NORMAL_WITHOUT_TEMPLATES)
            validator_instance_name = self.validator_instance_name(with_templates=False)
            result += f'private val {validator_instance_name} = ListValidator<{list_type}> {definition}'

            if with_template_validators:
                templated_list_type = item_type.prefixed_declaration(GenerationMode.TEMPLATE)
                validator_instance_name = self.validator_instance_name(with_templates=True)
                result += f'private val {validator_instance_name} = ListValidator<{templated_list_type}> {definition}'
        else:
            prop_type = cast(KotlinPropertyType, self.property_type)
            validator_type = prop_type.prefixed_declaration(GenerationMode.NORMAL_WITH_TEMPLATES)
            validator_instance_name_with = self.validator_instance_name(with_templates=True)
            validator_instance_name_without = self.validator_instance_name(with_templates=False)
            result += f'private val {validator_instance_name_with} = ValueValidator<{validator_type}> {definition}'
            result += f'private val {validator_instance_name_without} = ValueValidator<{validator_type}> {definition}'

        return result

    @property
    def static_reader_deserialization_expression(self) -> str:
        lambda_val = 'key, json, env -> ' + self.deserialization_expression(mode=GenerationMode.NORMAL_WITH_TEMPLATES,
                                                                            reuse_logger_instance=False)
        reader_type = cast(KotlinPropertyType, self.property_type).declaration_by_prefixed(
            prefixed=True,
            mode=GenerationMode.NORMAL_WITH_TEMPLATES
        )
        if self.use_expression_type:
            reader_type = f'{EXPRESSION_TYPE_NAME}<{reader_type}>'

        optional = '?' if self.parsed_value_is_optional else ''
        return f'val {self.reader_declaration_name}: Reader<{reader_type}{optional}> = {{ {lambda_val} }}'


class KotlinPropertyType(PropertyType):
    @property
    def is_array_of_expressions(self) -> bool:
        if isinstance(self, Array):
            return self.property_type.supports_expressions and \
                not cast(KotlinPropertyType, self.property_type).is_enum_of_expressions
        return False

    @property
    def is_enum_of_expressions(self) -> bool:
        return isinstance(self, Object) and isinstance(self.object, StringEnumeration)

    @property
    def empty_dict_deserialization(self) -> Optional[str]:
        if isinstance(self, Object) and isinstance(self.object, Entity) and \
                self.object.all_properties_are_optional_except_default_values:
            return f'{self.object.resolved_prefixed_declaration}()'
        return None

    def declaration_by_default_value(self, default_value: str, string_enum_prefixed: bool) -> Optional[str]:
        def wrap(value: str) -> str:
            if self.supports_expressions and not self.is_array_of_expressions and not self.is_enum_of_expressions:
                return f'{EXPRESSION_TYPE_NAME}.constant({value})'
            return value

        if isinstance(self, (Int, Bool, BoolInt)):
            return wrap(default_value)
        elif isinstance(self, Double):
            return wrap(str(default_value) if '.' in str(default_value) else f'{default_value}.0')
        elif isinstance(self, Color):
            return wrap(f'{default_value.replace("#", "0x")}.toInt()')
        elif isinstance(self, String):
            escaping = default_value.replace("\"", "\\\"")
            return wrap(f'"{escaping}"')
        elif isinstance(self, Url):
            return wrap(f'Uri.parse("{default_value}")')
        elif isinstance(self, Array):
            without_whitespaces = default_value.replace(' ', '').replace('\n', '')
            if not without_whitespaces.startswith('[') or not without_whitespaces.endswith(']'):
                return None
            if without_whitespaces == '[]':
                values = []
            else:
                values = without_whitespaces[1:-1].split(',')
            item_type = cast(KotlinPropertyType, self.property_type)
            declarations = list(filter(None, map(
                lambda value: item_type.declaration_by_default_value(value, string_enum_prefixed),
                values)))
            if len(values) != len(declarations):
                return None
            joined = ', '.join(declarations)
            return f'listOf({joined})'
        elif isinstance(self, Object):
            if self.object is None:
                return None

            if isinstance(self.object, StringEnumeration):
                if string_enum_prefixed:
                    name = self.object.resolved_prefixed_declaration
                else:
                    name = utils.capitalize_camel_case(self.object.name)
                def_val = utils.fixing_first_digit(utils.constant_upper_case(default_value))
                return wrap(f'Expression.constant({name}.{def_val})')

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
                obj = cast(KotlinPropertyType, Object(name='', object=enum_case[1], format=ObjectFormat.DEFAULT))
                case_constructor: Optional[str] = obj.declaration_by_default_value(default_value, True)
                if case_constructor is None:
                    return None
                obj_name = self.object.resolved_prefixed_declaration
                self.object.__class__ = KotlinEntityEnumeration
                case_name = cast(KotlinEntityEnumeration, self.object).format_case_naming(
                    utils.capitalize_camel_case(enum_case[0])
                )
                return wrap(f'{obj_name}.{case_name}({case_constructor})')

            entity: KotlinEntity = cast(KotlinEntity, self.object)
            entity.__class__ = KotlinEntity
            args = []
            for prop in entity.instance_properties_kotlin:
                str_type = default_value_dict.get(prop.dict_field)
                if str_type is None:
                    continue
                declaration = cast(KotlinPropertyType, prop.property_type).declaration_by_default_value(str_type, True)
                args.append(f'{prop.declaration_name} = {declaration}')
            args = ', '.join(args)
            return wrap(f'{entity.resolved_prefixed_declaration}({args})')
        else:
            return None

    def declaration(self, mode: GenerationMode) -> str:
        return self.declaration_by_prefixed(prefixed=False, mode=mode)

    def prefixed_declaration(self, mode: GenerationMode) -> str:
        return self.declaration_by_prefixed(prefixed=True, mode=mode)

    def declaration_by_prefixed(self, prefixed: bool, mode: GenerationMode) -> str:
        if isinstance(self, (Int, Color)):
            return 'Int'
        elif isinstance(self, Double):
            return 'Double'
        elif isinstance(self, (Bool, BoolInt)):
            return 'Boolean'
        elif isinstance(self, String):
            return 'CharSequence' if self.formatted else 'String'
        elif isinstance(self, Dictionary):
            return 'JSONObject'
        elif isinstance(self, StaticString):
            return 'String'
        elif isinstance(self, Url):
            return 'Uri'
        elif isinstance(self, Array):
            item_type = cast(KotlinPropertyType, self.property_type)
            item_decl = item_type.declaration_by_prefixed(prefixed, mode)
            if item_type.supports_expressions and not item_type.is_enum_of_expressions:
                return f'{EXPRESSION_LIST_TYPE_NAME}<{item_decl}>'
            return f'List<{item_decl}>'
        elif isinstance(self, Object):
            if self.name.startswith('$predefined_'):
                return self.name.replace('$predefined_', '')
            obj_name = None
            if mode.is_template:
                if isinstance(self.object, StringEnumeration):
                    string_enum: StringEnumeration = self.object
                    return string_enum.resolved_prefixed_declaration
                else:
                    obj_name = utils.capitalize_camel_case(self.object.resolved_name + mode.name_suffix)
            elif self.object is not None:
                obj_name = utils.capitalize_camel_case(self.object.resolved_name)
            prefix = ''
            if prefixed:
                prefix = self.object.declaration_prefix if mode.is_template else self.object.resolved_declaration_prefix
            return f'{prefix}{obj_name or utils.capitalize_camel_case(self.name)}'

    def deserialization_transform(self, string_enum_prefixed: bool) -> str:
        if isinstance(self, Url):
            return 'STRING_TO_URI'
        elif isinstance(self, Color):
            return 'STRING_TO_COLOR_INT'
        elif isinstance(self, (Bool, BoolInt)):
            return 'NUMBER_TO_BOOLEAN'
        elif isinstance(self, Object) and isinstance(self.object, StringEnumeration):
            if string_enum_prefixed:
                typename = self.object.resolved_prefixed_declaration
            else:
                typename = utils.capitalize_camel_case(self.object.name)
            return f'{typename}.Converter.FROM_STRING'
        elif isinstance(self, Double):
            return 'NUMBER_TO_DOUBLE'
        elif isinstance(self, Int):
            return 'NUMBER_TO_INT'
        elif isinstance(self, String):
            return '::HtmlString' if self.formatted else ''
        elif isinstance(self, Array):
            return cast(KotlinPropertyType, self.property_type).deserialization_transform(string_enum_prefixed)
        else:
            return ''

    def type_helper_reference(self, p: KotlinProperty) -> str:
        prefix = 'TYPE_HELPER_'
        if isinstance(self, (Bool, BoolInt)):
            return f'{prefix}BOOLEAN'
        elif isinstance(self, String):
            return f'{prefix}STRING'
        elif isinstance(self, Url):
            return f'{prefix}URI'
        elif isinstance(self, Color):
            return f'{prefix}COLOR'
        elif isinstance(self, Int):
            return f'{prefix}INT'
        elif isinstance(self, Double):
            return f'{prefix}DOUBLE'
        elif isinstance(self, Array):
            if self.property_type.supports_expressions:
                return cast(KotlinPropertyType, self.property_type).type_helper_reference(p)
            return ''
        elif isinstance(self, Object):
            return f'{prefix}{utils.constant_upper_case(p.name)}'
        else:
            return ''

    def serialization_transform(self, string_enum_prefixed: bool) -> str:
        prefix = ', converter = '
        if isinstance(self, (Bool, BoolInt)):
            return f'{prefix}BOOLEAN_TO_INT'
        elif isinstance(self, String):
            return f'{prefix}SPANNED_TO_HTML' if self.formatted else ''
        elif isinstance(self, Url):
            return f'{prefix}URI_TO_STRING'
        elif isinstance(self, Object) and isinstance(self.object, StringEnumeration):
            if string_enum_prefixed:
                typename = self.object.resolved_prefixed_declaration
            else:
                typename = utils.capitalize_camel_case(self.object.name)
            return f'{prefix}{{ v: {typename} -> {typename}.toString(v) }}'
        elif isinstance(self, Color):
            return f'{prefix}COLOR_INT_TO_STRING'
        elif isinstance(self, Array):
            return cast(KotlinPropertyType, self.property_type).serialization_transform(string_enum_prefixed)
        else:
            return ''


class CaseNaming:
    def format_case_name(self, name: str) -> str:
        if isinstance(self, Suffix):
            return name + 'Case'
        elif isinstance(self, RemoveCommonPart):
            return name[len(self.prefix):len(name) - len(self.suffix)]


class Suffix(CaseNaming):
    pass


@dataclasses.dataclass
class RemoveCommonPart(CaseNaming):
    prefix: str
    suffix: str


class KotlinEntityEnumeration(EntityEnumeration):

    def format_case_naming(self, entity_name: str) -> str:
        return self.__case_naming(entity_name).format_case_name(entity_name)

    def __case_naming(self, entity_name: str) -> CaseNaming:
        def find_common_prefix(a: List[str], b: List[str]) -> List[str]:
            common = []
            for a_el, b_el in zip(a, b):
                if a_el.lower() == b_el.lower():
                    common.append(a_el)
                else:
                    break
            return common

        names = [utils.name_components(self.name), utils.name_components(entity_name)]
        common_prefix = reduce(find_common_prefix, names)
        for element in names:
            element.reverse()
        common_suffix = reduce(find_common_prefix, names)
        if common_prefix or common_suffix:
            return RemoveCommonPart(prefix=utils.lower_camel_case('_'.join(common_prefix)),
                                    suffix=utils.lower_camel_case('_'.join(common_suffix)))
        else:
            return Suffix()
