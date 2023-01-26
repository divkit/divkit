from typing import cast, List
import os.path
from .dart_entities import (
    DartEntity,
    DartPropertyType,
    allowed_name,
    update_base,
)

from ..base import Generator
from ... import utils
from ...schema.modeling.entities import Entity, EntityEnumeration, StringEnumeration, Declarable
from ...schema.modeling.text import Text, EMPTY


class DartGenerator(Generator):
    def __init__(self, config):
        super(DartGenerator, self).__init__(config)

    def generate(self, objects: List[Declarable]):
        objects_dart: List[Declarable] = list(map(lambda obj: update_base(obj), objects))
        super(DartGenerator, self).generate(objects_dart)

    # ToDo(man-y): Needs to do a full export of all generated models or not?
    def generate_export(self, objects: List[Declarable]):
        file_content = Text('library generated_sources;\n')
        for obj in objects:
            file_content += f'export \'./{self.filename(obj.name)}\';'
        file_content += '\n'
        filename = os.path.join(self._output_path, 'generated_sources.dart')
        with open(filename, 'w') as file:
            file.write(file_content.__str__())

    def filename(self, name: str) -> str:
        return f'{utils.snake_case(name)}.dart'

    def _entity_declaration(self, entity: DartEntity) -> Text:
        result = Text()

        if entity.parent is None:
            imports = entity.imports
            if imports is not None:
                result += imports
                result += EMPTY

        pref = '' if entity.parent is None else utils.capitalize_camel_case(entity.parent.name)

        result += f'class {pref}{utils.capitalize_camel_case(entity.name)}' + ' {'

        props_decl = Text()
        for prop in entity.properties:
            if utils.lower_camel_case(prop.name) == 'type' and entity.static_type is not None:
                continue
            option = 'required ' if not prop.optional else ''
            props_decl += f'    {option}this.{utils.lower_camel_case(prop.name)},'

        constructor_props = f'  const {pref}{utils.capitalize_camel_case(entity.name)}' + '({\n' + props_decl.__str__() + '\n  });\n' if len(
            props_decl.__str__()) != 0 else f'  const {pref}{utils.capitalize_camel_case(entity.name)}();\n'
        result += constructor_props

        for prop in entity.properties:
            is_static_type_decl = utils.lower_camel_case(prop.name) == 'type' and entity.static_type is not None
            property_type = cast(DartPropertyType, prop.property_type)
            prop_type = property_type.declaration_by_prefixed()
            pref = 'static const' if is_static_type_decl else 'final'
            option = '?' if prop.optional and not is_static_type_decl else ''
            type_decl = f' = "{entity.static_type}"' if is_static_type_decl else ''
            result += f'  {pref} {prop_type}{option} {utils.lower_camel_case(prop.name)}{type_decl};'

        result += '}'

        for inner_type in entity.inner_types:
            result += EMPTY + self._main_declaration(inner_type)

        return result

    def _get_full_name(self, current: Entity) -> str:
        if current is not None:
            return self._get_full_name(current.parent) + utils.capitalize_camel_case(current.name)
        else:
            return ''

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        result = Text()

        pref = self._get_full_name(entity_enumeration.parent)

        result += f'enum {pref}{utils.capitalize_camel_case(entity_enumeration.name)}' + ' {'
        for entity in entity_enumeration.entity_names:
            result += f'  {allowed_name(utils.lower_camel_case(entity))},'
        result += '}'

        return result

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        result = Text()

        pref = self._get_full_name(string_enumeration.parent)

        result += f'enum {pref}{utils.capitalize_camel_case(string_enumeration.name)}' + ' {'
        for case in string_enumeration.cases:
            result += f'  {allowed_name(utils.lower_camel_case(case[0]))},'
        result += '}'

        return result

    pass
