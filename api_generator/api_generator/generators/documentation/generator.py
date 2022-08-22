import os
from pathlib import Path
from typing import List, Optional

from .documentation_entities import (
    update_declarable_base,
    DocumentationDeclarable,
    DocumentationEntity,
    DocumentationProperty
)
from .translations import translations
from .utils import bold, code, paragraph
from ..base import Generator
from ...config import Config, Platform
from ...schema.modeling.entities import (
    Declarable,
    DescriptionLanguage,
    Entity,
    EntityEnumeration,
    StringEnumeration,
    Property
)
from ...schema.modeling.text import Text


class DocumentationGenerator(Generator):

    def __init__(self, config: Config) -> None:
        super().__init__(config)
        self.__lang = ''
        self.__translations = {}

    def generate(self, objects: List[Declarable]):
        self._clear_output_directory()
        objects_doc: List[DocumentationDeclarable] = list(map(lambda obj: update_declarable_base(obj), objects))
        for lang in DescriptionLanguage:
            self.__lang = lang.lower()
            self._output_path = self._config.output_path + '/' + lang
            self.__translations = translations(lang)
            Path(self._output_path).mkdir(parents=True, exist_ok=True)
            self._generate_files(objects_doc)
        self.__make_menu_file(objects_doc)

    def __make_menu_file(self, objects: List[DocumentationDeclarable]):
        filename = os.path.join(self._config.output_path, 'toc.yaml')
        with open(filename, 'w') as file:
            file.write(self.__menu_content(objects))

    def __menu_content(self, objects: List[DocumentationDeclarable]) -> str:
        items = '\n'.join(map(
            lambda obj: self.__menu_item(obj),
            sorted(
                filter(lambda obj: obj.include_in_menu_file, objects),
                key=lambda obj: obj.name < obj.name
            )
        ))
        return '\n'.join([
            'title:',
            'href:',
            'items:',
            items
        ])

    @staticmethod
    def __menu_item(obj: Declarable) -> str:
        toc = '' if obj.include_in_documentation_toc else '\n   hidden: true'
        return '\n'.join([
            f'  -name: {obj.name}',
            f'   href: concepts/divs/{obj.name}.md{toc}'
        ])

    def _filename(self, name: str) -> str:
        return name + '.md'

    def _entity_declaration(self, entity: DocumentationEntity) -> Text:
        deprecation = self.__translations['div_generator_type_deprecated'] if entity.is_deprecated else ''
        header = '\n'.join(filter(
            lambda text: text.strip(),
            [entity.display_name, self.__description(entity), deprecation]
        ))
        params_header = self.__translations['div_generator_parameters']
        return Text([
            f'# {header}',
            '',
            '## JSON',
            '```json',
            str(entity.json_description(obj_stack=[])),
            '```',
            '',
            f'## {params_header}',
            self.__fields_table(entity)
        ])

    def __description(self, entity: DocumentationEntity) -> str:
        description = entity.description_doc(self.__lang)
        return description if description else self.__translations['div_generator_no_description']

    def __fields_table(self, entity: DocumentationEntity, table_name: Optional[str] = None) -> str:
        header = ('### ' + table_name + '\n') if table_name is not None else ''
        params_header = self.__translations['div_generator_parameters']
        description_header = self.__translations['div_generator_description']

        properties_table_content = Text(list(map(
            lambda p: f'| {code(p.dict_field)} | {self.__table_description(p)} |',
            entity.properties_doc
        )))

        inner_types_tables = '\n\n'.join(map(
            lambda inner_entity: self.__fields_table(entity=inner_entity, table_name=inner_entity.name),
            filter(lambda inner_type: isinstance(inner_type, Entity), entity.inner_types)
        ))
        inner_types_tables = ('\n\n' + inner_types_tables) if inner_types_tables.strip() else ''

        if len(properties_table_content.lines) == 0:
            return inner_types_tables

        return '\n'.join([
            f'{header}| {params_header} | {description_header} |',
            '| --- | --- |',
            f'{properties_table_content}{inner_types_tables}'
        ])

    def __table_description(self, prop: DocumentationProperty) -> str:
        property_type = prop.property_type_doc
        optionality = '' if prop.optional else self.__translations['div_generator_required_parameter']
        deprecation = self.__translations['div_generator_parameter_deprecated'] if prop.is_deprecated else ''
        default_value = '' if prop.default_value is None else \
            self.__translations['div_generator_default_value'].format(prop.default_value)

        return ''.join(map(
            lambda s: paragraph(s),
            map(
                lambda t: t.replace('\n', '</p><p>'),
                filter(None, [
                    bold(property_type.description),
                    optionality,
                    deprecation,
                    prop.description_doc(self.__lang),
                    property_type.constraints(self.__translations),
                    default_value,
                    self.__platforms(prop),
                    property_type.details(self.__translations)
                ])
            )
        ))

    def __platforms(self, prop: Property) -> str:
        if prop.platforms is None:
            return ''
        if len(prop.platforms) == 0:
            return self.__translations['div_generator_in_progress']
        platform_names = ', '.join(map(lambda p: self.__platform_name(p), prop.platforms))
        return self.__translations['div_generator_platforms'].format(platform_names)

    def __platform_name(self, platform: Platform) -> str:
        if platform == Platform.ANDROID:
            return self.__translations['div_generator_android']
        if platform == Platform.IOS:
            return self.__translations['div_generator_ios']
        if platform == Platform.WEB:
            return self.__translations['div_generator_web']
        raise NotImplementedError

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        header = self.__translations['div_generator_entity_enumeration']
        enumeration = '\n'.join(map(lambda entity: f'* [{entity[0]}]({entity[0]}.md#{entity[0]})',
                                    entity_enumeration.entities))
        return Text([
            f'# {entity_enumeration.name}',
            header,
            enumeration
        ])

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        return Text()
