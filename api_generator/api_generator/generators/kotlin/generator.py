from typing import cast, List
import os.path

from .kotlin_entities import (
    KotlinEntity,
    KotlinEntityEnumeration,
    ENTITY_STATIC_CREATOR,
    ENTITY_PARSER_NAME,
    TEMPLATE_PARSER_NAME,
    TEMPLATE_RESOLVER_NAME
)
from ..base import Generator
from ... import utils
from ...config import GenerationMode, GeneratedLanguage, TEMPLATE_SUFFIX
from ...schema.modeling.entities import (
    Declarable,
    StringEnumeration,
    EntityEnumeration,
    Entity
)
from ...schema.modeling.text import Text, EMPTY


class KotlinGenerator(Generator):
    def __init__(self, config):
        super(KotlinGenerator, self).__init__(config)
        self.kotlin_annotations = config.generation.kotlin_annotations
        self._error_collectors = config.generation.errors_collectors
        self.generate_equality = config.generation.generate_equality
        self.generate_serialization = config.generation.generate_serialization
        self.generate_serializers = config.generation.generate_serializers
        self._entity_generator = KotlinEntityGenerator(self)
        self._entity_template_generator = KotlinEntityTemplateGenerator(self)
        self._string_enumeration_generator = KotlinStringEnumerationGenerator()
        self._serializer_generator = KotlinSerializerGenerator(self)

    def generate(self, objects: List[Declarable]):
        super(KotlinGenerator, self).generate(objects)
        if self.generate_serializers:
            serializers = list(filter(lambda obj: self.__is_serializer(obj), objects))
            flatten_serializers: List[Declarable] = []
            for serializer in serializers:
                self.__flatten_object(serializer, flatten_serializers)
            self.generate_parser_component(flatten_serializers)

    @staticmethod
    def __is_serializer(obj: Declarable) -> bool:
        if isinstance(obj, Entity):
            return cast(Entity, obj).generation_mode.is_serializer
        elif isinstance(obj, EntityEnumeration):
            return cast(EntityEnumeration, obj).mode.is_serializer
        else:
            return False

    def generate_parser_component(self, objects: List[Declarable]):
        component_declaration = Text()
        component_declaration += 'internal class JsonParserComponent {'
        for obj in objects:
            serializer_type_prefix = (obj.entity_declaration_prefix.replace('.', '')
                                      + utils.capitalize_camel_case(obj.name_for(GenerationMode.NORMAL_WITH_TEMPLATES)))
            serializer_name_prefix = utils.lower_camel_case(serializer_type_prefix)
            component_declaration += EMPTY
            component_declaration += f'    val {serializer_name_prefix}JsonEntityParser = lazy {{ {serializer_type_prefix}JsonParser.{ENTITY_PARSER_NAME}(this) }}'
            component_declaration += f'    val {serializer_name_prefix}JsonTemplateParser = lazy {{ {serializer_type_prefix}JsonParser.{TEMPLATE_PARSER_NAME}(this) }}'
            component_declaration += f'    val {serializer_name_prefix}JsonTemplateResolver = lazy {{ {serializer_type_prefix}JsonParser.{TEMPLATE_RESOLVER_NAME}(this) }}'
        component_declaration += '}'
        head_for_file = self._head_for_file + '\n'
        file_content = f'{head_for_file}{component_declaration}\n'
        filename = os.path.join(self._output_path, 'JsonParserComponent.kt')
        with open(filename, 'w') as file:
            file.write(file_content.__str__())
            file.close()

    def __flatten_object(self, obj: Declarable, list: List[Declarable]):
        list.append(obj)
        if not isinstance(obj, Entity):
            return
        entity = cast(Entity, obj)
        for inner_type in filter(lambda t: isinstance(t, Entity) or isinstance(t, EntityEnumeration), entity.inner_types):
            self.__flatten_object(inner_type, list)

    def filename(self, name: str) -> str:
        return f'{utils.capitalize_camel_case(name)}.kt'

    def _entity_declaration(self, entity: Entity) -> Text:
        entity: KotlinEntity = cast(KotlinEntity, entity)
        entity.__class__ = KotlinEntity

        entity.eval_errors_collector_enabled(self._error_collectors)
        entity.update_bases()

        if entity.generation_mode.is_template:
            return self._entity_template_generator.entity_declaration(entity)
        elif entity.generation_mode.is_serializer:
            return self._serializer_generator.entity_serializer_declaration(entity)
        else:
            return self._entity_generator.entity_declaration(entity)

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        entity_enumeration: KotlinEntityEnumeration = cast(KotlinEntityEnumeration, entity_enumeration)
        entity_enumeration.__class__ = KotlinEntityEnumeration
        if entity_enumeration.mode.is_template:
            return self._entity_template_generator.entity_enumeration_declaration(entity_enumeration)
        elif entity_enumeration.mode.is_serializer:
            return self._serializer_generator.entity_enumeration_serializer_declaration(entity_enumeration)
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
        self._generate_serializers = generator.generate_serializers

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
            result += entity.serialization_declaration(self._generate_serializers).indented(indent_width=4)

        static_declarations = entity.static_declarations(self._generate_serialization, self._generate_serializers)
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
        if self._generate_serializers:
            serializer_name = entity_enumeration.entity_serializer_name_declaration
            result += f'        return builtInParserComponent.{serializer_name}'
            result += '            .value'
            result += '            .serialize(context = builtInParsingContext, value = this)'
        else:
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
        if self._generate_serializers:
            serializer_name = entity_enumeration.entity_serializer_name_declaration
            result += f'            return builtInParserComponent.{serializer_name}'
            result += '                .value'
            result += '                .deserialize(context = env, data = json)'
        else:
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
        self._generate_serializers = generator.generate_serializers

    def entity_declaration(self, entity: KotlinEntity) -> Text:
        result: Text = entity.header_declaration(self._kotlin_annotations, self._generate_serialization)

        if self._generate_serialization:
            result += EMPTY
            result += '    constructor('
            result += '        env: ParsingEnvironment,'
            result += f'        parent: {utils.capitalize_camel_case(entity.name)}? = null,'
            result += '        topLevel: Boolean = false,'
            result += '        json: JSONObject'
            if self._generate_serializers:
                if entity.instance_properties_kotlin:
                    result += '    ) : this('
                    for property in entity.instance_properties_kotlin:
                        result += f'        {property.declaration_name} = Field.nullField(false),'
                    result += '    ) {'
                    result += '        throw UnsupportedOperationException("Do not use this constructor directly.")'
                    result += '    }'
                else:
                    result += '    ) : this()'
            else:
                if entity.instance_properties_kotlin:
                    result += '    ) : this('
                    result += entity.constructor_body(with_commas=True).indented(indent_width=8)
                    result += '    )'
                else:
                    result += '    ) : this()'

        result += EMPTY
        result += entity.value_resolving_declaration(self._generate_serializers).indented(indent_width=4)

        if self._generate_serialization:
            result += EMPTY
            result += entity.serialization_declaration(self._generate_serializers).indented(indent_width=4)

        static_declarations = entity.static_declarations(self._generate_serialization, self._generate_serializers)
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
            if self._generate_serializers:
                serializer_name = entity_enumeration.template_serializer_name_declaration
                result += f'        return builtInParserComponent.{serializer_name}'
                result += '            .value'
                result += '            .serialize(context = builtInParsingContext, value = this)'
            else:
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
        if self._generate_serializers:
            resolver_name = entity_enumeration.template_resolver_name_declaration
            result += f'        return builtInParserComponent.{resolver_name}'
            result += '            .value'
            result += '            .resolve(context = env, template = this, data = data)'
        else:
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

        read_type_expr = 'json.read("type", logger = logger, env = env)'
        read_type_opt_expr = 'json.readOptional("type", logger = logger, env = env)'
        throwing_expr = 'throw typeMismatch(json = json, key = "type", value = type)'

        result += EMPTY
        result += '    companion object {'
        result += EMPTY
        result += '        @Throws(ParsingException::class)'
        result += '        operator fun invoke('
        result += '            env: ParsingEnvironment,'
        result += '            topLevel: Boolean = false,'
        result += '            json: JSONObject'
        result += f'        ): {declaration_name} {{'
        if self._generate_serializers:
            serializer_name = entity_enumeration.template_serializer_name_declaration
            result += f'            return builtInParserComponent.{serializer_name}'
            result += '                .value'
            result += '                .deserialize(context = env, data = json)'
        else:
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
        result += '        @JvmField'
        result += f'        val TO_STRING = {{ value: {declaration_name} -> toString(value) }}'
        result += EMPTY
        result += '        @JvmField'
        result += '        val FROM_STRING = { value: String -> fromString(value) }'
        result += '    }'
        result += '}'

        return result


class KotlinSerializerGenerator:
    def __init__(self, generator: KotlinGenerator):
        self._generator = generator

    def entity_serializer_declaration(self, entity: KotlinEntity) -> Text:
        result = entity.serializer_declaration()
        if entity.inner_types:
            for inner_type in filter(lambda t: not isinstance(t, StringEnumeration) or True, entity.inner_types):
                if isinstance(inner_type, Entity):
                    result += EMPTY
                    inner_entity: KotlinEntity = cast(KotlinEntity, inner_type)
                    inner_entity.__class__ = KotlinEntity
                    result += self.entity_serializer_declaration(inner_entity)
                elif isinstance(inner_type, EntityEnumeration):
                    result += EMPTY
                    inner_entity_enumeration: KotlinEntityEnumeration = cast(KotlinEntityEnumeration, inner_type)
                    inner_entity_enumeration.__class__ = KotlinEntityEnumeration
                    result += self.entity_enumeration_serializer_declaration(inner_entity_enumeration)
        return result

    def entity_enumeration_serializer_declaration(self, entity_enumeration: KotlinEntityEnumeration) -> Text:
        entities = entity_enumeration.entities_kotlin
        result = Text()
        result += f'internal class {entity_enumeration.serializer_type_declaration}('
        result += '    private val component: JsonParserComponent'
        result += ') {'
        result += EMPTY
        result += self.__entity_enumeration_entity_serializer_declaration(entity_enumeration, entities).indented(indent_width=4)
        result += EMPTY
        result += self.__entity_enumeration_template_serializer_declaration(entity_enumeration, entities).indented(indent_width=4)
        result += EMPTY
        result += self.__entity_enumeration_template_resolver_declaration(entity_enumeration, entities).indented(indent_width=4)
        result += '}'
        return result

    def __entity_enumeration_entity_serializer_declaration(
        self,
        entity_enumeration: KotlinEntityEnumeration,
        entities: List[KotlinEntity]
    ) -> Text:
        entity_type = entity_enumeration.prefixed_declaration_for(mode=GenerationMode.NORMAL_WITH_TEMPLATES)
        template_type = entity_enumeration.prefixed_declaration_for(mode=GenerationMode.TEMPLATE)
        default_entity_decl = utils.capitalize_camel_case(str(entity_enumeration.default_entity_declaration))

        read_type_expr = 'JsonPropertyParser.readString(context, data, "type")'
        read_type_opt_expr = 'JsonPropertyParser.readOptionalString(context, data, "type")'

        result = Text()
        result += f'class {ENTITY_PARSER_NAME}('
        result += '    private val component: JsonParserComponent'
        result += f') : Parser<JSONObject, {entity_type}> {{'
        result += EMPTY
        result += '    @Throws(ParsingException::class)'
        result += f'    override fun deserialize(context: ParsingContext, data: JSONObject): {entity_type} {{'
        if default_entity_decl:
            result += f'        val type: String = {read_type_opt_expr} ?: {default_entity_decl}.TYPE'
        else:
            result += f'        val type: String = {read_type_expr}'
        result += '        when (type) {'
        for subentity in entities:
            variant_name = entity_enumeration.format_case_naming(utils.capitalize_camel_case(subentity.name))
            subentity_type = utils.capitalize_camel_case(subentity.name_for(mode=GenerationMode.NORMAL_WITH_TEMPLATES))
            line = f'{subentity_type}.TYPE -> return {entity_type}.{variant_name}'\
                f'(component.{subentity.entity_serializer_name_declaration}.value.deserialize(context, data))'
            result += Text(indent_width=12, init_lines=line)
        result += '        }'
        result += EMPTY
        result += f'        val template = context.templates.getOrThrow(type, data) as? {template_type}'
        result += '        if (template != null) {'
        result += f'            return component.{entity_enumeration.template_resolver_name_declaration}'
        result += '                .value'
        result += '                .resolve(context, template, data)'
        result += '        } else {'
        result += '            throw typeMismatch(json = data, key = "type", value = type)'
        result += '        }'
        result += '    }'
        result += EMPTY
        result += '    @Throws(ParsingException::class)'
        result += f'    override fun serialize(context: ParsingContext, value: {entity_type}): JSONObject {{'
        result += '        return when (value) {'
        for subentity in entities:
            variant_name = entity_enumeration.format_case_naming(utils.capitalize_camel_case(subentity.name))
            line = f'is {entity_type}.{variant_name} -> component.{subentity.entity_serializer_name_declaration}.value.serialize(context, value.value)'
            result += Text(indent_width=12, init_lines=line)
        result += '        }'
        result += '    }'
        result += '}'
        return result

    def __entity_enumeration_template_serializer_declaration(
        self,
        entity_enumeration: KotlinEntityEnumeration,
        entities: List[KotlinEntity]
    ) -> Text:
        template_type = entity_enumeration.prefixed_declaration_for(mode=GenerationMode.TEMPLATE)
        default_entity_decl = utils.capitalize_camel_case(str(entity_enumeration.default_entity_declaration))

        read_type_expr = 'JsonPropertyParser.readString(context, data, "type")'
        read_type_opt_expr = 'JsonPropertyParser.readOptionalString(context, data, "type")'

        result = Text()
        result += f'class {TEMPLATE_PARSER_NAME}('
        result += '    private val component: JsonParserComponent'
        result += f') : Parser<JSONObject, {template_type}> {{'
        result += EMPTY
        result += '    @Throws(ParsingException::class)'
        result += f'    override fun deserialize(context: ParsingContext, data: JSONObject): {template_type} {{'
        if default_entity_decl:
            result += f'        val extendedType = {read_type_opt_expr} ?: {default_entity_decl}Template.TYPE'
        else:
            result += f'        val extendedType = {read_type_expr}'
        result += f'        val parent = context.templates[extendedType] as? {template_type}'
        result += '        val type = parent?.type ?: extendedType'
        result += '        when (type) {'
        for subentity in entities:
            subentity_template_type = utils.capitalize_camel_case(subentity.name_for(mode=GenerationMode.TEMPLATE))
            variant_name = entity_enumeration.format_case_naming(utils.capitalize_camel_case(subentity.name))
            line = f'{subentity_template_type}.TYPE -> return {template_type}.{variant_name}'\
                f'(component.{subentity.template_serializer_name_declaration}.value.deserialize(context, parent?.value() as {subentity_template_type}?, data))'
            result += Text(indent_width=12, init_lines=line)
        result += '            else -> throw typeMismatch(json = data, key = "type", value = type)'
        result += '        }'
        result += '    }'
        result += EMPTY
        result += '    @Throws(ParsingException::class)'
        result += f'    override fun serialize(context: ParsingContext, value: {template_type}): JSONObject {{'
        result += '        return when (value) {'
        for subentity in entities:
            variant_name = entity_enumeration.format_case_naming(utils.capitalize_camel_case(subentity.name))
            line = f'is {template_type}.{variant_name} -> component.{subentity.template_serializer_name_declaration}.value.serialize(context, value.value)'
            result += Text(indent_width=12, init_lines=line)
        result += '        }'
        result += '    }'
        result += '}'
        return result

    def __entity_enumeration_template_resolver_declaration(
        self,
        entity_enumeration: KotlinEntityEnumeration,
        entities: List[KotlinEntity]
    ) -> Text:
        entity_type = entity_enumeration.prefixed_declaration_for(mode=GenerationMode.NORMAL_WITH_TEMPLATES)
        template_type = entity_enumeration.prefixed_declaration_for(mode=GenerationMode.TEMPLATE)

        result = Text()
        result += f'class {TEMPLATE_RESOLVER_NAME}('
        result += '    private val component: JsonParserComponent'
        result += f') : TemplateResolver<JSONObject, {template_type}, {entity_type}> {{'
        result += EMPTY
        result += '    @Throws(ParsingException::class)'
        result += f'    override fun resolve(context: ParsingContext, template: {template_type}, data: JSONObject): {entity_type} {{'
        result += '        return when (template) {'
        for subentity in entities:
            variant_name = entity_enumeration.format_case_naming(utils.capitalize_camel_case(subentity.name))
            line = f'is {template_type}.{variant_name} -> {entity_type}.{variant_name}'\
                f'(component.{subentity.template_resolver_name_declaration}.value.resolve(context, template.value, data))'
            result += Text(indent_width=12, init_lines=line)
        result += '        }'
        result += '    }'
        result += '}'
        return result
