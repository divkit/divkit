# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithOptionalStringEnumProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_optional_string_enum_property',
        property: typing.Optional[EntityWithOptionalStringEnumPropertyProperty] = None,
    ):
        super().__init__(
            type=type,
            property=property,
        )

    type: str = Field(default='entity_with_optional_string_enum_property')
    property: typing.Optional[EntityWithOptionalStringEnumPropertyProperty] = Field()


class EntityWithOptionalStringEnumPropertyProperty(str, enum.Enum):
    FIRST = 'first'
    SECOND = 'second'


EntityWithOptionalStringEnumProperty.update_forward_refs()
