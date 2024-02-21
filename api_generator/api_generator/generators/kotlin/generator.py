from typing import cast, List

from .kotlin_entities import (
    KotlinEntity,
    KotlinProperty,
    KotlinEntityEnumeration,
    PARSING_ERRORS_PROP_NAME,
    ENTITY_STATIC_CREATOR
)
from ..base import Generator
from ... import utils
from ...config import GenerationMode, GeneratedLanguage, TEMPLATE_SUFFIX
from ...schema.modeling.entities import (
    StringEnumeration,
    EntityEnumeration,
    Entity,
    Object,
    ObjectFormat,
    Array,
)
from ...schema.modeling.text import Text, EMPTY


class KotlinGenerator(Generator):
    def __init__(self, config):
        super(KotlinGenerator, self).__init__(config)
        self.kotlin_annotations = config.generation.kotlin_annotations
        self._error_collectors = config.generation.errors_collectors
        self._generate_equality = config.generation.generate_equality
        self.generate_serialization = config.generation.generate_serialization

    def filename(self, name: str) -> str:
        return f'{utils.capitalize_camel_case(name)}.kt'

    def _entity_declaration(self, entity: Entity) -> Text:
        entity: KotlinEntity = cast(KotlinEntity, entity)
        entity.__class__ = KotlinEntity
        entity.eval_errors_collector_enabled(self._error_collectors)
        entity.update_bases()

        if entity.generate_as_protocol:
            return self.__declaration_as_interface(entity)
        result: Text = self.__main_declaration_header(entity)

        is_template = entity.generation_mode.is_template

        if is_template:
            result += EMPTY
            result += '    constructor ('
            result += '        env: ParsingEnvironment,'
            result += f'        parent: {utils.capitalize_camel_case(entity.name)}? = null,'
            result += '        topLevel: Boolean = false,'
            result += '        json: JSONObject'
            result += '    ) {'
            result += '        val logger = env.logger'
            constructor = entity.constructor_body(with_commas=False).indented(indent_width=8)
            if constructor.lines:
                result += constructor
            result += '    }'
            result += EMPTY
            result += entity.value_resolving_declaration.indented(indent_width=4)
        else:
            prop_names = map(lambda prop_name: prop_name.declaration_name, entity.instance_properties_kotlin)

            prop_filter = ['items']
            is_div_state = utils.capitalize_camel_case(entity.name) == 'DivState'

            if is_div_state:
                prop_filter.append('states')

            generate_properties = True if len(set(prop_filter).intersection(prop_names)) > 0 else False

            result += EMPTY
            if generate_properties:
                result += '    private var _propertiesHash: Int? = null '
            result += '    private var _hash: Int? = null '
            if generate_properties:
                result += EMPTY
                result += '    override fun propertiesHash(): Int {'
                result += '        _propertiesHash?.let {'
                result += '            return it'
                result += '        }'
                if len(entity.instance_properties_kotlin) > 1:
                    result += '        val propertiesHash = '
                    result += self._generate_hash(list(filter(lambda it: it.declaration_name not in prop_filter,
                                                              entity.instance_properties_kotlin))).indented(indent_width=12)
                else:
                    result += '        val propertiesHash = javaClass.hashCode()'
                result += '        _propertiesHash = propertiesHash'
                result += '        return propertiesHash'
                result += '    }'
            result += EMPTY
            result += '    override fun hash(): Int {'
            result += '        _hash?.let {'
            result += '            return it'
            result += '        }'

            if entity.instance_properties_kotlin:
                result += '        val hash = '
                if generate_properties:
                    hash_text = Text()
                    hash_text += 'propertiesHash() +'
                    hash_text += self._generate_hash(
                        list(filter(lambda it: it.declaration_name in prop_filter, entity.instance_properties_kotlin)))

                    result += hash_text.indented(indent_width=12)
                else:
                    result += self._generate_hash(list(filter(
                        lambda it: it.declaration_name not in prop_filter,
                        entity.instance_properties_kotlin))
                    ).indented(indent_width=12)
            else:
                result += '        val hash = javaClass.hashCode()'

            result += '        _hash = hash'
            result += '        return hash'
            result += '    }'
            if self._generate_equality:
                result += EMPTY
                result += '    fun isHashCalculated() = _hash != null'

        if self.generate_serialization:
            result += EMPTY
            result += entity.serialization_declaration.indented(indent_width=4)

        if not is_template and self._generate_equality:
            result += EMPTY
            if entity.instance_properties:
                result += '    override fun equals(other: Any?): Boolean {'
                result += '        if (this === other) { return true }'
                class_name = utils.capitalize_camel_case(entity.name)
                result += f'        if (other !is {class_name} || (this.isHashCalculated() && other.isHashCalculated()'
                result += '                    && this.hash() != other.hash())) {'
                result += '            return false'
                result += '        }'
                result += EMPTY
                for prop in entity.instance_properties_kotlin:
                    prefix = "return " if prop == entity.instance_properties_kotlin[0] else "    "
                    postfix = "&&" if prop != entity.instance_properties_kotlin[-1] else ""
                    result += f'         {prefix}this.{prop.declaration_name} == other.{prop.declaration_name} {postfix}'
                result += '    }'
                result += EMPTY
                result += '    override fun hashCode() = hash()'
            else:
                result += self.__manual_equals_hash_code_declaration.indented(indent_width=4)

        if not is_template:
            patch = entity.copy_with_new_properties_declaration
            if patch:
                result += patch

        static_declarations = entity.static_declarations(self.generate_serialization)
        if static_declarations.lines:
            result += EMPTY
            result += '    companion object {'
            result += static_declarations.indented(indent_width=8)
            result += '    }'
            result += EMPTY

        if entity.inner_types:
            for inner_type in filter(lambda t: not isinstance(t, StringEnumeration) or not is_template,
                                     entity.inner_types):
                result += EMPTY
                result += self._main_declaration(inner_type).indented(indent_width=4)

        result += '}'

        return result

    @staticmethod
    def _generate_hash(props: List[KotlinProperty]) -> Text:
        hash_text = Text()
        for prop in props:
            hash_type = 'hash()' if prop.use_custom_hash else 'hashCode()'

            prop_hash = ''
            if prop.should_be_optional:
                prop_hash += '('
            prop_hash += prop.declaration_name
            if prop.should_be_optional:
                prop_hash += '?'
            prop_hash += '.'
            if isinstance(prop.property_type, Array) and prop.use_custom_hash:
                prop_hash += f'sumOf {{ it.{hash_type} }}'
            else:
                prop_hash += hash_type
            if prop.should_be_optional:
                prop_hash += ' ?: 0)'
            prop_hash += " +" if prop != props[-1] else ""
            hash_text += prop_hash
        return hash_text

    @staticmethod
    def __declaration_as_interface(entity: KotlinEntity) -> Text:
        result = Text(f'interface {utils.capitalize_camel_case(entity.name)} {{')
        for prop in entity.instance_properties_kotlin:
            result += prop.declaration(overridden=False,
                                       in_interface=True,
                                       with_comma=False,
                                       with_default=False).indented(indent_width=4)
        result += '}'
        return result

    def __main_declaration_header(self, entity: KotlinEntity) -> Text:
        result = Text()
        for annotation in self.kotlin_annotations.classes:
            result += annotation
        prefix = f'class {utils.capitalize_camel_case(entity.name)}'

        interfaces = ['JSONSerializable'] if self.generate_serialization else []
        if not entity.generation_mode.is_template:
            interfaces.append('Hashable')
        protocol_plus_super_entities = entity.protocol_plus_super_entities()
        if protocol_plus_super_entities is not None:
            interfaces.append(protocol_plus_super_entities)
        interfaces = ', '.join(interfaces)
        suffix = f' : {interfaces}' if interfaces else ''
        suffix += ' {'

        def add_instance_properties(text: Text, is_template: bool) -> Text:
            mixed_properties = entity.instance_properties_kotlin
            if entity.errors_collector_enabled:
                mixed_properties.append(KotlinProperty(
                    name=PARSING_ERRORS_PROP_NAME,
                    description='',
                    description_translations={},
                    dict_field='',
                    property_type=Object(name='List<Exception>', object=None, format=ObjectFormat.DEFAULT),
                    optional=True,
                    is_deprecated=False,
                    mode=GenerationMode.NORMAL_WITHOUT_TEMPLATES,
                    supports_expressions_flag=False,
                    default_value=None,
                    platforms=None
                ))
            for prop in mixed_properties:
                overridden = False
                if entity.implemented_protocol is not None:
                    overridden = any(p.name == prop.name for p in entity.implemented_protocol.properties)
                text += prop.declaration(
                    overridden=overridden,
                    in_interface=False,
                    with_comma=not is_template,
                    with_default=not is_template
                ).indented(indent_width=4)
            return text

        if entity.generation_mode.is_template:
            result += prefix + suffix
            if entity.instance_properties:
                result = add_instance_properties(text=result, is_template=True)
        else:
            constructor_prefix = ''
            if self.kotlin_annotations.constructors:
                constructor_annotations = ', '.join(self.kotlin_annotations.constructors)
                constructor_prefix = f' {constructor_annotations} constructor '
            if not entity.instance_properties:
                result += f'{prefix}{constructor_prefix}(){suffix}'
            else:
                result += f'{prefix}{constructor_prefix}('
                result = add_instance_properties(text=result, is_template=False)
                result += f'){suffix}'

        return result

    @property
    def __manual_equals_hash_code_declaration(self) -> Text:
        result = Text('override fun equals(other: Any?) = javaClass == other?.javaClass')
        result += EMPTY
        result += 'override fun hashCode() = javaClass.hashCode()'
        return result

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        entity_enumeration: KotlinEntityEnumeration = cast(KotlinEntityEnumeration, entity_enumeration)
        entity_enumeration.__class__ = KotlinEntityEnumeration
        declaration_name = utils.capitalize_camel_case(entity_enumeration.name)
        entity_declarations = list(map(utils.capitalize_camel_case, entity_enumeration.entity_names))
        default_entity_decl = utils.capitalize_camel_case(str(entity_enumeration.default_entity_declaration))
        result = Text()
        for annotation in self.kotlin_annotations.classes:
            result += annotation
        interfaces = ['JSONSerializable'] if self.generate_serialization else []
        if not entity_enumeration.mode.is_template:
            interfaces.append('Hashable')
        interfaces.append(entity_enumeration.mode.protocol_name(
            lang=GeneratedLanguage.KOTLIN,
            name=entity_enumeration.resolved_prefixed_declaration))
        interfaces = ', '.join(filter(None, interfaces))

        suffix = f' : {interfaces}' if interfaces else ''
        suffix += ' {'

        result += f'sealed class {declaration_name}{suffix}'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            decl = f'class {naming}(val value: {decl}) : {declaration_name}()'
            result += Text(indent_width=4, init_lines=decl)
        result += EMPTY

        if not entity_enumeration.mode.is_template:
            hash_types = ['propertiesHash', 'hash']

            for hash_type in hash_types:
                result += f'    private var _{hash_type}: Int? = null '
            result += EMPTY
            for hash_type in hash_types:
                result += self._hash_enumeration_declaration(entity_enumeration, entity_declarations, hash_type)
                result += EMPTY
            if self._generate_equality:
                result += '    fun isHashCalculated() = _hash != null'
                result += EMPTY

        result += f'    fun value(): {entity_enumeration.common_interface(GeneratedLanguage.KOTLIN) or "Any"} {{'
        result += '        return when (this) {'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            decl = f'is {naming} -> value'
            result += Text(indent_width=12, init_lines=decl)
        result += '        }'
        result += '    }'
        result += EMPTY

        if self.generate_serialization:
            result += '    override fun writeToJSON(): JSONObject {'
            result += '        return when (this) {'
            for decl in entity_declarations:
                naming = entity_enumeration.format_case_naming(decl)
                decl = f'is {naming} -> value.writeToJSON()'
                result += Text(indent_width=12, init_lines=decl)
            result += '        }'
            result += '    }'
            result += EMPTY

        if entity_enumeration.mode.is_template:
            self_name = entity_enumeration.resolved_prefixed_declaration

            result += f'    override fun resolve(env: ParsingEnvironment, data: JSONObject): {self_name} {{'
            result += '        return when (this) {'
            for decl in entity_declarations:
                case_name = entity_enumeration.format_case_naming(decl)
                line = f'is {case_name} -> {self_name}.{case_name}(value.resolve(env, data))'
                result += Text(indent_width=12, init_lines=line)
            result += '        }'
            result += '    }'
            result += EMPTY

            result += '    val type: String'
            result += '        get() {'
            result += '            return when (this) {'
            for decl in entity_declarations:
                naming = entity_enumeration.format_case_naming(decl)
                line = f'is {naming} -> {decl}.TYPE'
                result += Text(indent_width=16, init_lines=line)
            result += '            }'
            result += '        }'
            result += EMPTY
        elif self._generate_equality:
            result += '    override fun equals(other: Any?): Boolean {'
            result += '        if (this === other) { return true }'
            result += f'        if (other !is {declaration_name} || (this.isHashCalculated() && other.isHashCalculated()'
            result += '                    && this.hash() != other.hash())) {'
            result += '            return false'
            result += '        }'
            result += EMPTY
            result += '        return this.value() == other.value()'
            result += '    }'
            result += EMPTY
            result += '    override fun hashCode() = hash()'
            result += EMPTY

        if not self.generate_serialization:
            result += '}'
            return result

        result += '    companion object {'
        result += '        @Throws(ParsingException::class)'

        source_name = 'json'
        source_type = 'JSONObject'
        read_type_expr = 'json.read("type", logger = logger, env = env)'
        read_type_opt_expr = 'json.readOptional("type", logger = logger, env = env)'
        throwing_expr = 'throw typeMismatch(json = json, key = "type", value = type)'

        if entity_enumeration.mode.is_template:
            def deserialization_args(s):
                return f'env, parent?.value() as {s}?, topLevel, {source_name}'
            result += '        operator fun invoke('
            result += '            env: ParsingEnvironment,'
            result += '            topLevel: Boolean = false,'
            result += f'            {source_name}: {source_type}'
            result += f'        ): {declaration_name} {{'
            result += '            val logger = env.logger'
            if default_entity_decl:
                result += f'            val receivedType: String = {read_type_opt_expr} ?: {default_entity_decl}Template.TYPE'
            else:
                result += f'            val receivedType: String = {read_type_expr}'
            result += f'            val parent = env.templates[receivedType] as? {declaration_name}'
            result += '            val type = parent?.type ?: receivedType'
        else:
            def deserialization_args(s):
                return f'env, {source_name}'
            result += '        @JvmStatic'
            result += '        @JvmName("fromJson")'
            args = f'env: ParsingEnvironment, {source_name}: {source_type}'
            result += f'        operator fun invoke({args}): {declaration_name} {{'
            result += '            val logger = env.logger'
            if default_entity_decl:
                result += f'            val type: String = {read_type_opt_expr} ?: {default_entity_decl}.TYPE'
            else:
                result += f'            val type: String = {read_type_expr}'
        result += '            when (type) {'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            line = f'{decl}.TYPE -> return {naming}({decl}({deserialization_args(decl)}))'
            result += Text(indent_width=16, init_lines=line)

        if entity_enumeration.mode is GenerationMode.NORMAL_WITH_TEMPLATES:
            result += '            }'
            name = utils.capitalize_camel_case(entity_enumeration.name + TEMPLATE_SUFFIX)
            template_type = entity_enumeration.template_declaration_prefix + name
            result += f'            val template = env.templates.getOrThrow(type, json) as? {template_type}'
            result += '            if (template != null) {'
            result += f'                return template.resolve(env, {source_name})'
            result += '            } else {'
            result += f'                {throwing_expr}'
            result += '            }'
        else:
            result += f'                else -> {throwing_expr}'
            result += '            }'
        result += '        }'

        static_creator_lambda = f'env: ParsingEnvironment, it: JSONObject -> {declaration_name}(env, json = it)'
        result += f'        val {ENTITY_STATIC_CREATOR} = {{ {static_creator_lambda} }}'
        result += '    }'
        result += '}'
        return result

    @staticmethod
    def _hash_enumeration_declaration(
            entity_enumeration: KotlinEntityEnumeration,
            entity_declarations: List[str],
            hash_type: str
    ) -> Text:
        result = Text()
        result += f'    override fun {hash_type}(): Int {{'
        result += f'        _{hash_type}?.let {{'
        result += '            return it'
        result += '        }'
        result += '        return when(this) {'
        for i, decl in enumerate(entity_declarations, start=1):
            naming = entity_enumeration.format_case_naming(decl)
            result += f'            is {naming} -> {i * 31} + this.value.{hash_type}()'
        result += '        }.also {'
        result += f'            _{hash_type} = it'
        result += '        }'
        result += '    }'
        return result

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        declaration_name = utils.capitalize_camel_case(string_enumeration.name)
        cases_declarations = list(map(lambda s: Text(indent_width=16, init_lines=f'{s}.value -> {s}'),
                                      map(lambda s: utils.fixing_first_digit(utils.constant_upper_case(s[0])),
                                          string_enumeration.cases)))
        result = Text(f'enum class {declaration_name}(private val value: String) {{')
        for ind, case in enumerate(string_enumeration.cases):
            terminal = ',' if ind != (len(cases_declarations) - 1) else ';'
            name = utils.fixing_first_digit(utils.constant_upper_case(case[0]))
            value = case[1]
            result += Text(indent_width=4, init_lines=f'{name}("{value}"){terminal}')
        result += EMPTY

        result += '    companion object Converter {'
        result += f'        fun toString(obj: {declaration_name}): String {{'
        result += '            return obj.value'
        result += '        }'
        result += EMPTY

        result += f'        fun fromString(string: String): {declaration_name}? {{'
        result += '            return when (string) {'
        result += cases_declarations
        result += '                else -> null'
        result += '            }'
        result += '        }'
        result += EMPTY

        result += '        val FROM_STRING = { string: String ->'
        result += '            when (string) {'
        result += cases_declarations
        result += '                else -> null'
        result += '            }'
        result += '        }'
        result += '    }'
        result += '}'

        return result
