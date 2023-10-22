from typing import cast, List

from .typescript_entities import (
    TypeScriptEntity,
    _type_script_full_name,
    _make_type_script_imports
)
from ..base import Generator
from ... import utils
from ...schema.modeling.entities import (
    StringEnumeration,
    EntityEnumeration,
    Entity,
    Declarable,
)
from ...schema.modeling.text import Text, EMPTY


class TypeScriptGenerator(Generator):
    def filename(self, name: str) -> str:
        return f'{utils.capitalize_camel_case(name)}.ts'

    def generate(self, objects: List[Declarable]):
        super(TypeScriptGenerator, self).generate(objects)
        self.__generate_index_file(objects)

    def __generate_index_file(self, objects: List[Declarable]):
        file_content = Text('// Generated code. Do not modify.')
        file_content += EMPTY
        object_names = sorted(map(lambda o: utils.capitalize_camel_case(o.name), objects))
        for obj_name in object_names:
            file_content += f"export * from './{obj_name}';"
        with open(f'{self._config.output_path}/index.ts', 'w') as file:
            file.write(str(file_content))
            file.close()

    def _entity_declaration(self, entity: Entity) -> Text:
        result = Text()
        entity.__class__ = TypeScriptEntity
        entity = cast(TypeScriptEntity, entity)
        imports = entity.imports
        if imports is not None:
            result += imports
        result += self.__entity_declaration_without_imports(entity)
        return result

    def __entity_declaration_without_imports(self, entity: TypeScriptEntity) -> Text:
        if entity.should_generate_as_type_script_class:
            result = entity.class_declaration
        else:
            result = entity.interface_declaration
        if entity.inner_types:
            result += EMPTY
            result += self.__inner_types_declaration(entity)
        return result

    def __inner_types_declaration(self, entity: TypeScriptEntity) -> Text:
        def declaration_of(decl_type, decl_method) -> Text:
            def sort_predicate(d: Declarable):
                return d.name
            inner_types_decl = Text()
            inner_types = sorted(filter(lambda t: isinstance(t, decl_type), entity.inner_types), key=sort_predicate)
            for p in inner_types:
                if isinstance(p, decl_type):
                    inner_types_decl += decl_method(p)
                    inner_types_decl += EMPTY
            return inner_types_decl
        declarations = [declaration_of(StringEnumeration, self._string_enumeration_declaration),
                        declaration_of(EntityEnumeration, self.__entity_enumeration_declaration_without_imports),
                        declaration_of(Entity, self.__entity_declaration_without_imports)]
        result = Text()
        for decl in declarations:
            if decl.lines:
                result += decl

        return result

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        imports = sorted(filter(None, map(lambda e: _type_script_full_name(e[1]) if e[1] is not None else None,
                                          entity_enumeration.entities)))
        if entity_enumeration.default_entity_declaration:
            imports.append(f'{utils.capitalize_camel_case(entity_enumeration.default_entity_declaration)}Props')
        result = _make_type_script_imports(items=imports)
        result += self.__entity_enumeration_declaration_without_imports(entity_enumeration)
        return result

    @staticmethod
    def __entity_enumeration_declaration_without_imports(entity_enumeration: EntityEnumeration) -> Text:
        result = Text(f'export type {_type_script_full_name(entity_enumeration)} =')
        names = list(filter(None, map(lambda e: _type_script_full_name(e[1]) if e[1] is not None else None,
                                      entity_enumeration.entities)))

        if entity_enumeration.default_entity_declaration:
            names.append(f'{utils.capitalize_camel_case(entity_enumeration.default_entity_declaration)}Props')

        if entity_enumeration.generate_case_for_templates:
            names.append('TemplateBlock')
        for ind, name in enumerate(names):
            ending = ';' if ind == (len(names) - 1) else ''
            result += f'    | {name}{ending}'
        return result

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        result = Text(f'export type {_type_script_full_name(string_enumeration)} =')
        values = list(map(lambda case: case[1], string_enumeration.cases))
        for ind, value in enumerate(values):
            ending = ';' if ind == (len(values) - 1) else ''
            result += f"    | '{value}'{ending}"
        return result
