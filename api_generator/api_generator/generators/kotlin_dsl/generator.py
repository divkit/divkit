from typing import List

from .kotlin_dsl_entities import (
    update_base,
    KotlinDSLEntity,
    KotlinDSLEntityEnumeration
)
from ..base import Generator
from ... import utils
from ...schema.modeling.entities import (
    StringEnumeration,
    EntityEnumeration,
    Declarable, Entity,
)
from ...schema.modeling.text import Text, EMPTY


class KotlinDSLGenerator(Generator):

    def __init__(self, config):
        super(KotlinDSLGenerator, self).__init__(config)
        self.kotlin_annotations = config.generation.kotlin_annotations

    def generate(self, objects: List[Declarable]):
        objects_dsl: List[Declarable] = list(map(lambda obj: update_base(obj), objects))
        super(KotlinDSLGenerator, self).generate(objects_dsl)

    def filename(self, name: str) -> str:
        return f'{utils.capitalize_camel_case(name)}.kt'

    def _entity_declaration(self, entity: KotlinDSLEntity) -> Text:
        return self.__entity_declaration_with(entity, with_factory_methods=True)

    def __entity_declaration_with(self, entity: KotlinDSLEntity, with_factory_methods: bool) -> Text:
        if entity.generate_as_protocol:
            return Text()
        result = Text()
        for annotation in self.kotlin_annotations.classes:
            result += annotation
        result += f'class {utils.capitalize_camel_case(entity.name)} internal constructor('
        constructor_parameter_declaration = entity.constructor_parameter_declaration.indented(indent_width=4)
        if constructor_parameter_declaration.lines:
            result += constructor_parameter_declaration
        result += f'){entity.supertype_declaration} {{'
        body_decl = self.__body_declaration(entity).indented(indent_width=4)
        if body_decl.lines:
            result += body_decl
        result += '}'
        if with_factory_methods:
            result += EMPTY
            result += entity.factory_methods_declaration(for_templates=True)
            result += EMPTY
            result += entity.factory_methods_declaration(for_templates=False)
        return result

    def __body_declaration(self, entity: KotlinDSLEntity) -> Text:
        result = Text()
        if entity.enclosing_enumerations:
            static_type = entity.static_type
            if static_type is not None:
                result += EMPTY
                result += f'@JsonProperty("type") override val type = "{static_type}"'
        if entity.instance_properties:
            result += entity.serialization_declaration
        nested_classes_declaration = self.__nested_classes_declaration(entity)
        if nested_classes_declaration.lines:
            result += EMPTY
            result += nested_classes_declaration
        return result

    def __nested_classes_declaration(self, entity: KotlinDSLEntity) -> Text:
        def declaration_of(decl_type, decl_method) -> Text:
            def sort_predicate(d: Declarable):
                return d.name

            inner_types_decl = Text()
            inner_types = sorted(filter(lambda t: isinstance(t, decl_type), entity.inner_types), key=sort_predicate)
            for ind, p in enumerate(inner_types):
                if isinstance(p, decl_type):
                    inner_types_decl += decl_method(p)
                    if ind != len(inner_types) - 1:
                        inner_types_decl += EMPTY

            return inner_types_decl

        declarations = [declaration_of(StringEnumeration, self._string_enumeration_declaration),
                        declaration_of(EntityEnumeration, self._entity_enumeration_declaration),
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

    def _entity_enumeration_declaration(self, entity_enumeration: KotlinDSLEntityEnumeration) -> Text:
        result = Text()
        for annotation in self.kotlin_annotations.classes:
            result += annotation
        result += f'sealed interface {entity_enumeration.enumeration_name}{entity_enumeration.supertype_declaration} {{'
        result += '    val type: String'
        result += '}'
        result += EMPTY
        if entity_enumeration.generate_case_for_templates:
            result += entity_enumeration.template_case_declaration
        return result

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        decl_name = utils.capitalize_camel_case(string_enumeration.name)
        result = Text(f'enum class {decl_name}(@JsonValue val value: String) {{')
        for name, value in string_enumeration.cases:
            case_decl = utils.fixing_first_digit(utils.constant_upper_case(name))
            result += Text(f'{case_decl}("{value}"),').indented(indent_width=4)
        result += '}'
        return result
