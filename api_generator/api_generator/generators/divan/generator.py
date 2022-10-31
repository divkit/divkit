from ..base import Generator
from ...schema.modeling.entities import StringEnumeration, EntityEnumeration, Entity
from ...schema.modeling.text import Text

from ... import utils


class DivanGenerator(Generator):
    def filename(self, name: str) -> str:
        return f'{utils.snake_case(name).capitalize()}.kt'

    def _entity_declaration(self, entity: Entity) -> Text:
        return Text()

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        return Text()

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        return Text()
