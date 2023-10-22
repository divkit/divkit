from typing import cast, List

from .python_entities import (
    update_base,
    as_python_commentary,
    _make_python_imports,
    _python_full_name,
    PythonEntity,
    PythonProperty
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


class PythonGenerator(Generator):
    def filename(self, name: str) -> str:
        return f'{utils.snake_case(name)}.py'

    def generate(self, objects: List[Declarable]):
        objects_py: List[Declarable] = list(map(lambda obj: update_base(obj), objects))
        super(PythonGenerator, self).generate(objects_py)
        self.__generate_package_file(objects_py)

    def __generate_package_file(self, objects: List[Declarable]):
        file_content = Text('# Generated code. Do not modify.')
        file_content += "# flake8: noqa: F405"
        file_content += EMPTY

        all_imports = set()

        def sort_key(d: Declarable):
            return d.name
        uniqs = []
        for obj in sorted(objects, key=sort_key):
            uniqs.append(utils.capitalize_camel_case(obj.name))
            if isinstance(obj, PythonEntity):
                uniqs.extend(self.__inner_types_classes(obj))

        if len(uniqs) > len(list(set(uniqs))):
            raise ValueError('Inner type classes not uniqs')

        for obj in sorted(objects, key=sort_key):
            imports = [utils.capitalize_camel_case(obj.name)]
            if isinstance(obj, PythonEntity):
                imports += self.__inner_types_classes(obj)
            all_imports = all_imports | set(imports)
            imports = ', '.join(imports)
            file_content += f'from .{utils.snake_case(obj.name)} import {imports}'

        file_content += EMPTY
        with_inners = []
        for obj in sorted(objects, key=sort_key):
            if isinstance(obj, PythonEntity):
                with_inners.append(utils.capitalize_camel_case(obj.name))
                with_inners += self.__inner_entity_classes(obj)

        for e in with_inners:
            file_content += f'{e}.update_forward_refs()'

        file_content += EMPTY
        file_content += f"__all__ = ({', '.join(sorted(repr(imp) for imp in all_imports))})"

        with open(f'{self._config.output_path}/__init__.py', 'w') as file:
            file.write(str(file_content))
            file.close()

    def __inner_types_classes(self, entity: PythonEntity) -> List[str]:
        def declaration_of(decl_type, method) -> List[str]:
            def sort_predicate(d: Declarable):
                return d.name
            classes = []
            inner_types = sorted(filter(lambda t: isinstance(t, decl_type), entity.inner_types), key=sort_predicate)
            for inner_type in inner_types:
                if isinstance(inner_type, decl_type):
                    classes += method(inner_type)
            return classes

        result = []

        result.extend(declaration_of(StringEnumeration, lambda str_enum: [_python_full_name(str_enum)]))
        result.extend(declaration_of(EntityEnumeration, lambda ent_enum: [_python_full_name(ent_enum)]))
        result.extend(declaration_of(Entity, lambda ent: [_python_full_name(ent)] + self.__inner_types_classes(ent)))

        return result

    def __inner_entity_classes(self, entity: PythonEntity) -> List[str]:
        result = []

        def sort_predicate(d: Declarable):
            return d.name

        for inner_type in sorted(entity.inner_types, key=sort_predicate):
            if isinstance(inner_type, PythonEntity):
                result.append(_python_full_name(inner_type))
                result += self.__inner_entity_classes(inner_type)
        return result

    def _entity_declaration(self, entity: PythonEntity) -> Text:
        result = Text()
        imports = entity.imports
        if imports is not None:
            result += imports
            result += EMPTY
        result += EMPTY
        result += self.__entity_declaration_without_imports(entity)
        return result

    def __entity_declaration_without_imports(self, entity: PythonEntity) -> Text:
        result = Text()
        description = entity.description_doc()
        if description.strip():
            result += as_python_commentary(description)
        py_full_name = _python_full_name(entity)
        result += f'class {py_full_name}(BaseDiv):'
        result += EMPTY
        result += entity.generate_init(filename=utils.snake_case(py_full_name)).indented(indent_width=4)
        if entity.should_generate_as_python_class:
            declaration = entity.class_properties_declaration
        else:
            declaration = entity.dynamic_properties_declaration
        if declaration.lines:
            result += EMPTY
            result += declaration

        inner_types_declaration = self.__inner_types_declaration(entity)
        if inner_types_declaration.lines:
            result += EMPTY
            result += EMPTY
            result += inner_types_declaration

        result += EMPTY
        result += EMPTY
        result += f'{py_full_name}.update_forward_refs()'
        return result

    def __inner_entity_declaration_without_imports(self, entity: PythonEntity, filename: str) -> Text:
        result = Text()
        description = entity.description_doc()
        if description:
            result += as_python_commentary(description)
        py_full_name = _python_full_name(entity)
        result += f'class {py_full_name}(BaseDiv):'
        result += EMPTY
        result += entity.generate_init(filename).indented(indent_width=4)

        if entity.should_generate_as_python_class:
            declaration = entity.class_properties_declaration
        else:
            declaration = Text()
            for p in cast(List[PythonProperty], entity.instance_properties):
                declaration += p.make_declaration(filename).indented(indent_width=4)
        if declaration.lines:
            result += EMPTY
            result += declaration

        inner_types_declaration = self.__inner_types_declaration(entity)
        if inner_types_declaration.lines:
            result += EMPTY
            result += EMPTY
            result += inner_types_declaration

        result += EMPTY
        result += EMPTY
        result += f'{py_full_name}.update_forward_refs()'
        return result

    def __inner_types_declaration(self, entity: PythonEntity) -> Text:
        def declaration_of(decl_type, decl_method) -> Text:
            def sort_predicate(d: Declarable):
                return d.name
            inner_types_decl = Text()
            inner_types = sorted(filter(lambda t: isinstance(t, decl_type), entity.inner_types), key=sort_predicate)
            for ind, inner_type in enumerate(inner_types):
                update_base(inner_type)
                inner_types_decl += decl_method(inner_type)
                if ind != (len(inner_types) - 1):
                    inner_types_decl += EMPTY
                    inner_types_decl += EMPTY
            return inner_types_decl

        declarations = [declaration_of(StringEnumeration, self._string_enumeration_declaration),
                        declaration_of(EntityEnumeration, self.__entity_enumeration_declaration_without_imports),
                        declaration_of(Entity,
                                       lambda prop: self.__inner_entity_declaration_without_imports(
                                           prop,
                                           utils.snake_case(_python_full_name(entity))
                                       ))
                        ]
        result = Text()
        declarations = list(filter(lambda d: d.lines, declarations))
        for ind, decl in enumerate(declarations):
            result += decl
            if ind != (len(declarations) - 1):
                result += EMPTY
                result += EMPTY
        return result

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        result = Text()
        imports = sorted(list(filter(None,
                                     map(
                                         lambda e: _python_full_name(e[1]) if e[1] is not None else None,
                                         entity_enumeration.entities
                                     ))))
        imports = _make_python_imports(items=imports)
        if imports != EMPTY:
            result += imports
        result += EMPTY
        result += self.__entity_enumeration_declaration_without_imports(entity_enumeration)
        return result

    @staticmethod
    def __entity_enumeration_declaration_without_imports(entity_enumeration: EntityEnumeration) -> Text:
        result = Text()
        result += 'from typing import Union'
        result += EMPTY
        result += f'{_python_full_name(entity_enumeration)} = Union['
        entities = list(filter(None, map(lambda e: e[1], entity_enumeration.entities)))
        for e in entities:
            full_name = _python_full_name(e)
            result += Text(f'{utils.snake_case(full_name)}.{full_name},').indented(indent_width=4)
        result += ']'
        return result

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        result = Text()
        result += f'class {_python_full_name(string_enumeration)}(str, enum.Enum):'
        for _, value in string_enumeration.cases:
            result += Text(f"{utils.upper_snake_case(value)} = '{value}'").indented(indent_width=4)
        return result
