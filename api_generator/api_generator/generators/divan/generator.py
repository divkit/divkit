from typing import List, Dict, Union

from .divan_entities import (
    update_base,
    full_name,
    DivanEntity,
    DivanEntityEnumeration,
    DivanStringEnumeration,
)
from ..base import Generator
from ..documentation.translations import translations
from ... import utils
from ...schema.modeling.entities import (
    StringEnumeration,
    EntityEnumeration,
    Entity,
    Declarable,
    DescriptionLanguage,
)
from ...schema.modeling.text import Text, EMPTY


class DivanGenerator(Generator):
    def __init__(self, config):
        super(DivanGenerator, self).__init__(config)
        self.kotlin_annotations = config.generation.kotlin_annotations
        self.translations = translations(DescriptionLanguage.EN)
        self.remove_prefix = config.generation.remove_prefix
        self.supertype_entities = set(
            map(lambda s: utils.capitalize_camel_case(s), config.generation.supertype_entities)
        )

    def generate(self, objects: List[Declarable]):
        updated_objects = list(map(lambda obj: update_base(obj, self.remove_prefix), objects))
        super(DivanGenerator, self).generate(updated_objects)
        self._generate_enums_values(updated_objects)

    def filename(self, name: str) -> str:
        return f'{utils.capitalize_camel_case(name, self.remove_prefix)}.kt'

    def _entity_declaration(self, entity: DivanEntity) -> Text:
        return self.__entity_declaration_with(entity, with_factory_methods=True)

    def __entity_declaration_with(self, entity: DivanEntity, with_factory_methods: bool) -> Text:
        if entity.generate_as_protocol:
            return Text()
        result_declaration = Text()

        result_declaration += entity.header_comment_block(self.translations)
        for annotation in self.kotlin_annotations.classes + self.kotlin_annotations.top_level_definitions:
            result_declaration += annotation

        if entity.is_deprecated:
            result_declaration += f'@Deprecated("{self.translations["div_generator_deprecated_message"]}")'

        entity_name = utils.capitalize_camel_case(entity.name, self.remove_prefix)

        has_properties = len(entity.instance_properties) > 0
        if not has_properties:
            result_declaration += f'data object {entity_name}{entity.supertype_declaration} {{'
        else:
            result_declaration += f'data class {entity_name} internal constructor('
            result_declaration += '    @JsonIgnore'
            result_declaration += '    val properties: Properties,'
            result_declaration += f'){entity.supertype_declaration} {{'

        serialization_declaration = entity.serialization_declaration(has_properties).indented(indent_width=4)

        if serialization_declaration.lines:
            result_declaration += serialization_declaration

        if has_properties:
            if entity.generator_properties is None or entity.generator_properties.plus_operator_declaration:
                result_declaration += EMPTY
                result_declaration += entity.operator_plus_declaration.indented(indent_width=4)

            result_declaration += EMPTY
            result_declaration += entity.properties_class_declaration(self.translations).indented(indent_width=4)

        nested_classes_declaration = self.__nested_classes_declaration(entity)
        if nested_classes_declaration.lines:
            result_declaration += EMPTY
            result_declaration += nested_classes_declaration.indented(indent_width=4)

        result_declaration += '}'

        result_declaration += EMPTY

        if with_factory_methods:
            def add_methods_declarations(ent: DivanEntity):
                nonlocal result_declaration

                def add_declaration(comment_block: Text, declaration: Text):
                    nonlocal result_declaration
                    if not declaration.lines:
                        return
                    method_declaration = comment_block
                    for annotation in self.kotlin_annotations.top_level_definitions:
                        method_declaration += annotation
                    method_declaration += declaration
                    result_declaration += method_declaration
                    result_declaration += EMPTY

                for comment_block, declaration in ent.alternative_factories_declarations:
                    add_declaration(comment_block, declaration)

                add_declaration(ent.params_comment_block(), ent.factory_method_declaration)

                if has_properties:
                    add_declaration(ent.params_comment_block(), ent.properties_factory_method_declaration)
                    add_declaration(ent.params_comment_block(), ent.references_factory_method_declaration)
                    add_declaration(ent.params_comment_block(), ent.override_method_declaration)
                    add_declaration(ent.params_comment_block(), ent.defer_method_declaration)
                    add_declaration(ent.evaluatable_params_comment_block, ent.evaluate_method_declaration)

                    if self.supertype_entities.intersection(ent.supertypes_list):
                        add_declaration(ent.params_comment_block(), ent.override_component_method_declaration)
                        add_declaration(ent.params_comment_block(), ent.defer_component_method_declaration)
                        add_declaration(ent.evaluatable_params_comment_block, ent.evaluate_component_method_declaration)
                        add_declaration(Text(), ent.operator_plus_component_declaration)

                add_declaration(Text(), ent.as_list_method_declaration)

            def sort_predicate(d: Declarable):
                return d.name

            add_methods_declarations(ent=entity)

            inner_types = sorted(entity.inner_types, key=sort_predicate)
            for ind, nested_entity in enumerate(inner_types):
                nested_entity = update_base(nested_entity, self.remove_prefix)
                if isinstance(nested_entity, DivanEntity):
                    add_methods_declarations(ent=nested_entity)
                elif isinstance(nested_entity, (DivanEntityEnumeration, DivanStringEnumeration)):
                    name = full_name(nested_entity, self.remove_prefix)
                    for annotation in self.kotlin_annotations.top_level_definitions:
                        result_declaration += annotation
                    result_declaration += f'fun {name}.asList() = listOf(this)'
                    result_declaration += EMPTY

        return result_declaration

    def __nested_classes_declaration(self, entity: DivanEntity) -> Text:
        def declaration_of(decl_type, decl_method) -> Text:
            def sort_predicate(d: Declarable):
                return d.name

            inner_types_decl = Text()
            inner_types = sorted(filter(lambda t: isinstance(t, decl_type), entity.inner_types), key=sort_predicate)
            for ind, inner_type in enumerate(inner_types):
                inner_type = update_base(inner_type, self.remove_prefix)
                if isinstance(inner_type, decl_type):
                    inner_types_decl += decl_method(inner_type)
                    if ind != len(inner_types) - 1:
                        inner_types_decl += EMPTY

            return inner_types_decl

        declarations = [declaration_of(StringEnumeration,
                                       lambda ent: self._enumeration_declaration(ent,
                                                                                 with_as_list_method=False)
                                       ),
                        declaration_of(EntityEnumeration,
                                       lambda ent: self._enumeration_declaration(ent,
                                                                                 with_as_list_method=False)
                                       ),
                        declaration_of(Entity,
                                       lambda ent: self.__entity_declaration_with(ent,
                                                                                  with_factory_methods=False)
                                       )
                        ]
        result = Text()
        declarations = list(filter(lambda d: d.lines, declarations))
        for ind, decl in enumerate(declarations):
            result += decl
            if ind != len(declarations) - 1:
                result += EMPTY

        return result

    def _entity_enumeration_declaration(self, entity_enumeration: DivanEntityEnumeration) -> Text:
        return self._enumeration_declaration(entity_enumeration, with_as_list_method=True)

    def _string_enumeration_declaration(self, string_enumeration: DivanStringEnumeration) -> Text:
        return self._enumeration_declaration(string_enumeration, with_as_list_method=True)

    def _enumeration_declaration(self, enumeration: Union[DivanEntityEnumeration, DivanStringEnumeration],
                                 with_as_list_method: bool) -> Text:
        result_declaration = Text()
        name = utils.capitalize_camel_case(enumeration.name, self.remove_prefix)
        result_declaration += enumeration.header_comment_block(self.translations)
        for annotation in self.kotlin_annotations.classes + self.kotlin_annotations.top_level_definitions:
            result_declaration += annotation

        result_declaration += f'sealed interface {name}'

        if with_as_list_method:
            result_declaration += EMPTY
            for annotation in self.kotlin_annotations.classes + self.kotlin_annotations.top_level_definitions:
                result_declaration += annotation
            result_declaration += f'fun {name}.asList() = listOf(this)'

        return result_declaration

    def _generate_enums_values(self, divan_objects: List[Declarable]):
        enums = []
        for obj in divan_objects:
            if isinstance(obj, StringEnumeration):
                enums.append(obj)
            elif isinstance(obj, Entity):
                enums.extend(self.__inner_string_enums(obj))
        values = dict()
        for enum in enums:
            # filtering like "type" properties
            if len(enum.cases) > 1:
                for name, value in enum.cases:
                    values[value] = (values.get(value) or []) + [full_name(enum, self.remove_prefix)]
        self.__generate_enum_values(values)

    def __inner_string_enums(self, entity: Entity) -> List[str]:
        def declaration_of(decl_type, method) -> List[str]:
            def sort_predicate(d: Declarable):
                return d.name
            classes = []
            inner_types = sorted(filter(lambda t: isinstance(t, decl_type), entity.inner_types), key=sort_predicate)
            for inner_type in inner_types:
                inner_type = update_base(inner_type, self.remove_prefix)
                if isinstance(inner_type, decl_type):
                    classes += method(inner_type)
            return classes

        result = []

        result.extend(declaration_of(StringEnumeration, lambda str_enum: [str_enum]))
        result.extend(declaration_of(Entity, lambda ent: self.__inner_string_enums(ent)))

        return result

    def __generate_enum_values(self, values: Dict[str, List[str]]):
        enum_values_declaration = Text()
        for annotation in self.kotlin_annotations.top_level_definitions:
            enum_values_declaration += annotation
        enum_values_declaration += 'sealed class EnumValue('
        enum_values_declaration += utils.indented('@JsonValue', level=1, indent_width=4)
        enum_values_declaration += utils.indented('val serialized: String,', level=1, indent_width=4)
        enum_values_declaration += ')'
        enum_values_declaration += EMPTY

        for value_name in values:
            enum_values_declaration += self.__generate_enum_value_obj(value_name, sorted(values[value_name]))
            enum_values_declaration += EMPTY

        for value_name in values:
            enum_values_declaration += self.__generate_scope_extension(value_name)
            enum_values_declaration += EMPTY

        with open(f'{self._config.output_path}/EnumValues.kt', 'w') as file:
            head_for_file = self._head_for_file + '\n' if self._head_for_file.strip() else ''
            declaration = str(enum_values_declaration)
            file_content = f'{head_for_file}{declaration}'
            file.write(file_content)
            file.close()

    def __generate_enum_value_obj(self, value_name: str, enum_names: List[str]) -> Text:
        obj_declaration = Text()
        for annotation in self.kotlin_annotations.top_level_definitions:
            obj_declaration += annotation
        obj_declaration += f'object {self.format_value_object_name(value_name)} : EnumValue("{value_name}"),'
        for index, name in enumerate(enum_names):
            obj_declaration += Text(name + (',' if index + 1 != len(enum_names) else '')).indented(indent_width=4)
        return obj_declaration

    def __generate_scope_extension(self, value_name: str) -> Text:
        extension_declaration = Text()
        for annotation in self.kotlin_annotations.top_level_definitions:
            extension_declaration += annotation
        object_name = self.format_value_object_name(value_name)
        # keep original name if abbreviation
        extension_name = value_name if value_name.isupper() else utils.snake_case(value_name)
        extension_declaration += f'val DivScope.{extension_name}: {object_name}'
        extension_declaration += utils.indented(f'get() = {object_name}', level=1, indent_width=4)
        return extension_declaration

    def format_value_object_name(self, value_name: str) -> str:
        return utils.capitalize_camel_case(value_name, self.remove_prefix) + "EnumValue"
