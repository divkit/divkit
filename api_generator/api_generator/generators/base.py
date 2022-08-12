from __future__ import annotations
from abc import ABC, abstractmethod
from typing import List

from ..config import Config
from ..schema.modeling.entities import Declarable, Entity, EntityEnumeration, StringEnumeration
from ..schema.modeling.text import Text
from . import utils


class Generator(ABC):
    def __init__(self, config: Config) -> None:
        self._config = config

    def generate(self, objects: List[Declarable]):
        utils.clear_content_of_directory(self._config.output_path)
        for obj in objects:
            declaration = str(self._main_declaration(obj))
            if not declaration.strip():
                continue
            file_content = f'{self._head_for_file}\n{declaration}'
            filename = f'{self._config.output_path}/{self._filename(obj.name)}'
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
