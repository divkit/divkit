from typing import List, cast

from .divan_entities import (
    update_base,
    DivanEntity,
    DivanEntityEnumeration,
    DivanStringEnumeration,
)
from ..base import Generator
from ...schema.modeling.text import Text, EMPTY
from ...schema.modeling.entities import (
    StringEnumeration,
    EntityEnumeration,
    Entity,
    Declarable,
)

from ... import utils


class DivanGenerator(Generator):
    def __init__(self, config):
        super(DivanGenerator, self).__init__(config)
        self.kotlin_annotations = config.generation.kotlin_annotations
        self.top_level_annotations = config.generation.top_level_annotations

    def generate(self, objects: List[Declarable]):
        super(DivanGenerator, self).generate(list(map(lambda obj: update_base(obj), objects)))

    def filename(self, name: str) -> str:
        return f'{utils.capitalize_camel_case(name)}.kt'

    def _entity_declaration(self, entity: DivanEntity) -> Text:
        return self.__entity_declaration_with(entity, with_factory_methods=True)

    def __entity_declaration_with(self, entity: DivanEntity, with_factory_methods: bool) -> Text:
        result_declaration = Text()
        result_declaration += entity.header_comment_block
        for annotation in self.top_level_annotations + self.kotlin_annotations:
            result_declaration += annotation

        result_declaration += f'class {utils.capitalize_camel_case(entity.name)} internal constructor('
        result_declaration += '    @JsonIgnore'
        result_declaration += '    val properties: Properties,'
        result_declaration += f'){entity.supertype_declaration} {{'

        serialization_declaration = entity.serialization_declaration.indented(indent_width=4)

        if serialization_declaration.lines:
            result_declaration += serialization_declaration
            result_declaration += EMPTY

        result_declaration += entity.operator_plus_declaration.indented(indent_width=4)

        result_declaration += EMPTY
        result_declaration += entity.properties_class_declaration.indented(indent_width=4)

        nested_classes_declaration = self.__nested_classes_declaration(entity)
        if nested_classes_declaration.lines:
            result_declaration += EMPTY
            result_declaration += nested_classes_declaration.indented(indent_width=4)

        result_declaration += '}'

        result_declaration += EMPTY

        if with_factory_methods:
            def add_methods_declarations(for_entity: DivanEntity):
                nonlocal result_declaration

                method_declaration = for_entity.params_comment_block
                for annotation in self.top_level_annotations:
                    method_declaration += annotation
                method_declaration += for_entity.override_method_declaration
                result_declaration += method_declaration

                result_declaration += EMPTY

                method_declaration = for_entity.params_comment_block
                for annotation in self.top_level_annotations:
                    method_declaration += annotation
                method_declaration += for_entity.defer_method_declaration
                result_declaration += method_declaration

                result_declaration += EMPTY

                method_declaration = entity.evaluatable_params_comment_block
                for annotation in self.top_level_annotations:
                    method_declaration += annotation
                method_declaration += entity.evaluate_method_declaration
                result_declaration += method_declaration

            def sort_predicate(d: Declarable):
                return d.name

            add_methods_declarations(for_entity=entity)
            result_declaration += EMPTY

            inner_types = sorted(filter(lambda t: isinstance(t, Entity), entity.inner_types), key=sort_predicate)
            for ind, nested_entity in enumerate(inner_types):
                add_methods_declarations(for_entity=cast(DivanEntity, nested_entity))
                result_declaration += EMPTY

        return result_declaration

    def __nested_classes_declaration(self, entity: DivanEntity) -> Text:
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

    def _entity_enumeration_declaration(self, entity_enumeration: DivanEntityEnumeration) -> Text:
        return Text()

    def _string_enumeration_declaration(self, string_enumeration: DivanStringEnumeration) -> Text:
        return Text()
