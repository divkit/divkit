from typing import cast, List

from .kotlin_entities import (
    KotlinEntity,
    KotlinEntityEnumeration,
    ENTITY_STATIC_CREATOR
)
from ..base import Generator
from ... import utils
from ...config import GenerationMode, GeneratedLanguage, TEMPLATE_SUFFIX
from ...schema.modeling.entities import (
    StringEnumeration,
    EntityEnumeration,
    Entity,
)
from ...schema.modeling.text import Text, EMPTY


class KotlinGenerator(Generator):
    def __init__(self, config):
        super(KotlinGenerator, self).__init__(config)
        self.kotlin_annotations = config.generation.kotlin_annotations
        self._error_collectors = config.generation.errors_collectors
        self.generate_equality = config.generation.generate_equality
        self.generate_serialization = config.generation.generate_serialization
        self._entity_generator = KotlinEntityGenerator(self)
        self._entity_template_generator = KotlinEntityTemplateGenerator(self)
        self._string_enumeration_generator = KotlinStringEnumerationGenerator()

    def filename(self, name: str) -> str:
        return f'{utils.capitalize_camel_case(name)}.kt'

    def _entity_declaration(self, entity: Entity) -> Text:
        entity: KotlinEntity = cast(KotlinEntity, entity)
        entity.__class__ = KotlinEntity

        entity.eval_errors_collector_enabled(self._error_collectors)
        entity.update_bases()

        if entity.generation_mode.is_template:
            return self._entity_template_generator.entity_declaration(entity)
        else:
            return self._entity_generator.entity_declaration(entity)

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        entity_enumeration: KotlinEntityEnumeration = cast(KotlinEntityEnumeration, entity_enumeration)
        entity_enumeration.__class__ = KotlinEntityEnumeration
        if entity_enumeration.mode.is_template:
            return self._entity_template_generator.entity_enumeration_declaration(entity_enumeration)
        else:
            return self._entity_generator.entity_enumeration_declaration(entity_enumeration)

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        return self._string_enumeration_generator.string_enumeration_declaration(string_enumeration)


class KotlinEntityGenerator:
    def __init__(self, generator: KotlinGenerator):
        self._generator = generator
        self._kotlin_annotations = generator.kotlin_annotations
        self._generate_equality = generator.generate_equality
        self._generate_serialization = generator.generate_serialization

    def entity_declaration(self, entity: KotlinEntity) -> Text:
        if entity.generate_as_protocol:
            return self.__declaration_as_interface(entity)
        result: Text = entity.header_declaration(self._kotlin_annotations, self._generate_serialization)
        result += entity.hash_declaration(self._generate_equality)
        result += entity.equals_resolved_declaration()
        if self._generate_equality:
            result += entity.equality_declaration
        result += entity.copy_declaration

        if self._generate_serialization:
            result += EMPTY
            result += entity.serialization_declaration.indented(indent_width=4)

        static_declarations = entity.static_declarations(self._generate_serialization)
        if static_declarations.lines:
            result += EMPTY
            result += '    companion object {'
            result += static_declarations.indented(indent_width=8)
            result += '    }'

        if entity.inner_types:
            for inner_type in filter(lambda t: not isinstance(t, StringEnumeration) or True, entity.inner_types):
                result += EMPTY
                result += self._generator._main_declaration(inner_type).indented(indent_width=4)

        result += '}'
        return result

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

    def entity_enumeration_declaration(self, entity_enumeration: KotlinEntityEnumeration) -> Text:
        declaration_name = utils.capitalize_camel_case(entity_enumeration.name)
        entity_declarations = list(map(utils.capitalize_camel_case, entity_enumeration.entity_names))
        default_entity_decl = utils.capitalize_camel_case(str(entity_enumeration.default_entity_declaration))
        result = Text()

        for annotation in self._kotlin_annotations.classes:
            result += annotation
        interfaces = ['JSONSerializable'] if self._generate_serialization else []
        interfaces.append('Hashable')
        interfaces.append(entity_enumeration.mode.protocol_name(
            lang=GeneratedLanguage.KOTLIN,
            name=entity_enumeration.resolved_prefixed_declaration))
        interfaces = ', '.join(filter(None, interfaces))

        suffix = f' : {interfaces}' if interfaces else ''
        result += f'sealed class {declaration_name}{suffix} {{'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            decl = f'class {naming}(val value: {decl}) : {declaration_name}()'
            result += Text(indent_width=4, init_lines=decl)

        hash_types = ['propertiesHash', 'hash']
        result += EMPTY
        for hash_type in hash_types:
            result += f'    private var _{hash_type}: Int? = null '
        for hash_type in hash_types:
            result += EMPTY
            result += self._hash_enumeration_declaration(entity_enumeration, entity_declarations, hash_type)
        result += EMPTY
        result += self._equals_resolved_enumeration_declaration(entity_enumeration, entity_declarations)
        if self._generate_equality:
            result += EMPTY
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

        if self._generate_equality:
            result += EMPTY
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

        if not self._generate_serialization:
            result += '}'
            return result

        result += EMPTY
        result += '    override fun writeToJSON(): JSONObject {'
        result += '        return when (this) {'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            decl = f'is {naming} -> value.writeToJSON()'
            result += Text(indent_width=12, init_lines=decl)
        result += '        }'
        result += '    }'

        read_type_expr = 'json.read("type", logger = logger, env = env)'
        read_type_opt_expr = 'json.readOptional("type", logger = logger, env = env)'
        throwing_expr = 'throw typeMismatch(json = json, key = "type", value = type)'

        result += EMPTY
        result += '    companion object {'
        result += EMPTY
        result += '        @Throws(ParsingException::class)'
        result += '        @JvmStatic'
        result += '        @JvmName("fromJson")'
        result += f'        operator fun invoke(env: ParsingEnvironment, json: JSONObject): {declaration_name} {{'
        result += '            val logger = env.logger'
        if default_entity_decl:
            result += f'            val type: String = {read_type_opt_expr} ?: {default_entity_decl}.TYPE'
        else:
            result += f'            val type: String = {read_type_expr}'
        result += '            when (type) {'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            line = f'{decl}.TYPE -> return {naming}({decl}(env, json))'
            result += Text(indent_width=16, init_lines=line)

        if entity_enumeration.mode is GenerationMode.NORMAL_WITH_TEMPLATES:
            result += '            }'
            name = utils.capitalize_camel_case(entity_enumeration.name + TEMPLATE_SUFFIX)
            template_type = entity_enumeration.template_declaration_prefix + name
            result += f'            val template = env.templates.getOrThrow(type, json) as? {template_type}'
            result += '            if (template != null) {'
            result += '                return template.resolve(env, json)'
            result += '            } else {'
            result += f'                {throwing_expr}'
            result += '            }'
        else:
            result += f'                else -> {throwing_expr}'
            result += '            }'
        result += '        }'

        static_creator_lambda = f'env: ParsingEnvironment, it: JSONObject -> {declaration_name}(env, json = it)'
        result += EMPTY
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
        result += f'        val {hash_type} = this::class.hashCode() + when(this) {{'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            result += f'            is {naming} -> this.value.{hash_type}()'
        result += '        }'
        result += f'       _{hash_type} = {hash_type}'
        result += f'       return {hash_type}'
        result += '    }'
        return result

    @staticmethod
    def _equals_resolved_enumeration_declaration(
            entity_enumeration: KotlinEntityEnumeration,
            entity_declarations: List[str]
    ) -> Text:
        result = Text()
        name = utils.capitalize_camel_case(entity_enumeration.name)
        result += f'    fun equals(other: {name}?, resolver: ExpressionResolver, otherResolver: ExpressionResolver)' +\
                  ': Boolean {'
        result += '        other ?: return false'
        result += '        return when(this) {'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            result += f'            is {naming} -> this.value.equals(other.value() as? {decl}, resolver, otherResolver)'
        result += '        }'
        result += '    }'
        return result


class KotlinEntityTemplateGenerator:
    def __init__(self, generator: KotlinGenerator):
        self._generator = generator
        self._kotlin_annotations = generator.kotlin_annotations
        self._generate_serialization = generator.generate_serialization

    def entity_declaration(self, entity: KotlinEntity) -> Text:
        result: Text = entity.header_declaration(self._kotlin_annotations, self._generate_serialization)
        result += EMPTY
        result += '    constructor ('
        result += '        env: ParsingEnvironment,'
        result += f'        parent: {utils.capitalize_camel_case(entity.name)}? = null,'
        result += '        topLevel: Boolean = false,'
        result += '        json: JSONObject'
        result += '    ) {'
        constructor = entity.constructor_body(with_commas=False)
        if constructor.lines:
            result += '        val logger = env.logger'
            result += constructor.indented(indent_width=8)
        result += '    }'
        result += EMPTY
        result += entity.value_resolving_declaration.indented(indent_width=4)

        if self._generate_serialization:
            result += EMPTY
            result += entity.serialization_declaration.indented(indent_width=4)

        static_declarations = entity.static_declarations(self._generate_serialization)
        if static_declarations.lines:
            result += EMPTY
            result += '    companion object {'
            result += static_declarations.indented(indent_width=8)
            result += '    }'

        if entity.inner_types:
            for inner_type in filter(lambda t: not isinstance(t, StringEnumeration) or False, entity.inner_types):
                result += EMPTY
                result += self._generator._main_declaration(inner_type).indented(indent_width=4)

        result += '}'
        return result

    def entity_enumeration_declaration(self, entity_enumeration: KotlinEntityEnumeration) -> Text:
        declaration_name = utils.capitalize_camel_case(entity_enumeration.name)
        entity_declarations = list(map(utils.capitalize_camel_case, entity_enumeration.entity_names))
        default_entity_decl = utils.capitalize_camel_case(str(entity_enumeration.default_entity_declaration))
        result = Text()

        for annotation in self._kotlin_annotations.classes:
            result += annotation
        interfaces = ['JSONSerializable'] if self._generate_serialization else []
        interfaces.append(entity_enumeration.mode.protocol_name(
            lang=GeneratedLanguage.KOTLIN,
            name=entity_enumeration.resolved_prefixed_declaration))
        interfaces = ', '.join(filter(None, interfaces))

        suffix = f' : {interfaces}' if interfaces else ''
        result += f'sealed class {declaration_name}{suffix} {{'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            decl = f'class {naming}(val value: {decl}) : {declaration_name}()'
            result += Text(indent_width=4, init_lines=decl)

        result += EMPTY
        result += f'    fun value(): {entity_enumeration.common_interface(GeneratedLanguage.KOTLIN) or "Any"} {{'
        result += '        return when (this) {'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            decl = f'is {naming} -> value'
            result += Text(indent_width=12, init_lines=decl)
        result += '        }'
        result += '    }'

        if self._generate_serialization:
            result += EMPTY
            result += '    override fun writeToJSON(): JSONObject {'
            result += '        return when (this) {'
            for decl in entity_declarations:
                naming = entity_enumeration.format_case_naming(decl)
                decl = f'is {naming} -> value.writeToJSON()'
                result += Text(indent_width=12, init_lines=decl)
            result += '        }'
            result += '    }'

        self_name = entity_enumeration.resolved_prefixed_declaration
        result += EMPTY
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

        if not self._generate_serialization:
            result += '}'
            return result

        result += EMPTY
        result += '    companion object {'
        result += EMPTY
        result += '        @Throws(ParsingException::class)'

        read_type_expr = 'json.read("type", logger = logger, env = env)'
        read_type_opt_expr = 'json.readOptional("type", logger = logger, env = env)'
        throwing_expr = 'throw typeMismatch(json = json, key = "type", value = type)'

        result += '        operator fun invoke('
        result += '            env: ParsingEnvironment,'
        result += '            topLevel: Boolean = false,'
        result += '            json: JSONObject'
        result += f'        ): {declaration_name} {{'
        result += '            val logger = env.logger'
        if default_entity_decl:
            result += f'            val receivedType: String = {read_type_opt_expr} ?: {default_entity_decl}Template.TYPE'
        else:
            result += f'            val receivedType: String = {read_type_expr}'
        result += f'            val parent = env.templates[receivedType] as? {declaration_name}'
        result += '            val type = parent?.type ?: receivedType'
        result += '            when (type) {'
        for decl in entity_declarations:
            naming = entity_enumeration.format_case_naming(decl)
            line = f'{decl}.TYPE -> return {naming}({decl}(env, parent?.value() as {decl}?, topLevel, json))'
            result += Text(indent_width=16, init_lines=line)
        result += f'                else -> {throwing_expr}'
        result += '            }'
        result += '        }'

        static_creator_lambda = f'env: ParsingEnvironment, it: JSONObject -> {declaration_name}(env, json = it)'
        result += EMPTY
        result += f'        val {ENTITY_STATIC_CREATOR} = {{ {static_creator_lambda} }}'
        result += '    }'
        result += '}'
        return result


class KotlinStringEnumerationGenerator:
    def string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
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
        result += EMPTY
        result += f'        fun toString(obj: {declaration_name}): String {{'
        result += '            return obj.value'
        result += '        }'
        result += EMPTY
        result += f'        fun fromString(value: String): {declaration_name}? {{'
        result += '            return when (value) {'
        result += cases_declarations
        result += '                else -> null'
        result += '            }'
        result += '        }'
        result += EMPTY
        result += f'        val TO_STRING = {{ value: {declaration_name} -> toString(value) }}'
        result += '        val FROM_STRING = { value: String -> fromString(value) }'
        result += '    }'
        result += '}'

        return result
