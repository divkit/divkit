from __future__ import annotations

import os.path
from abc import ABC, abstractmethod
from typing import List

from ..config import Config
from ..schema.modeling.entities import (
    Declarable,
    Entity,
    EntityEnumeration,
    StringEnumeration,
    Property,
    String,
    Array, Url, Int, Double,
)
from ..schema.modeling.text import Text
from .. import utils


def declaration_comment(p: Property, default_value_comment_fun) -> str:
    comments = []
    if isinstance(p.property_type, String):
        string: String = p.property_type
        if string.min_length > 0:
            end = 's' if string.min_length > 1 else ''
            comments.append(f'at least {string.min_length} char{end}')
        if string.regex is not None:
            comments.append(f'regex: {string.regex.pattern}')
    elif isinstance(p.property_type, Array):
        array: Array = p.property_type
        if array.min_items > 0:
            comments.append(f'at least {array.min_items} elements')
        if array.strict_parsing:
            comments.append('all received elements must be valid')
    elif isinstance(p.property_type, Url) and p.property_type.schemes:
        joined_schemes = ', '.join(p.property_type.schemes)
        comments.append(f'valid schemes: [{joined_schemes}]')
    elif isinstance(p.property_type, (Int, Double)):
        constraint = p.property_type.constraint
        if constraint is not None:
            comments.append(f'constraint: {constraint}')

    if p.default_value is not None:
        comments.append(f'default value: {default_value_comment_fun(p)}')

    if not comments:
        return ''
    joined_comments = '; '.join(comments)
    return f' // {joined_comments}'


class Generator(ABC):
    def __init__(self, config: Config) -> None:
        self._config = config
        self._output_path = config.output_path

    def generate(self, objects: List[Declarable]):
        self._clear_output_directory()
        self._generate_files(objects)

    def _clear_output_directory(self):
        utils.clear_content_of_directory(self._output_path)

    def _generate_files(self, objects: List[Declarable]):
        for obj in objects:
            declaration = []
            for line in str(self._main_declaration(obj)).strip().split('\n'):
                if line.isspace():
                    declaration.append('')
                else:
                    declaration.append(line)
            declaration = '\n'.join(declaration).strip()
            if not declaration:
                continue
            head_for_file = self._head_for_file + '\n' if self._head_for_file.strip() else ''
            file_content = f'{head_for_file}{declaration}\n'
            filename = os.path.join(self._output_path, self._filename(obj.name))
            with open(filename, 'w') as file:
                file.write(file_content)

    @property
    def _head_for_file(self) -> str:
        return self._config.generation.header

    def _main_declaration(self, obj: Declarable) -> Text:
        if isinstance(obj, Entity):
            return self._entity_declaration(obj)
        elif isinstance(obj, EntityEnumeration):
            return self._entity_enumeration_declaration(obj)
        elif isinstance(obj, StringEnumeration):
            return self._string_enumeration_declaration(obj)
        else:
            raise NotImplementedError

    @abstractmethod
    def _filename(self, name: str) -> str:
        pass

    @abstractmethod
    def _entity_declaration(self, entity: Entity) -> Text:
        pass

    @abstractmethod
    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        pass

    @abstractmethod
    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        pass
