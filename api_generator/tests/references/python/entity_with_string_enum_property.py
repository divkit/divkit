# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithStringEnumProperty(BaseDiv):

    def __init__(
        self, *,
        property: EntityWithStringEnumPropertyProperty,
        type: str = 'entity_with_string_enum_property',
    ):
        super().__init__(
            type=type,
            property=property,
        )

    type: str = Field(default='entity_with_string_enum_property')
    property: EntityWithStringEnumPropertyProperty = Field(
    )


class EntityWithStringEnumPropertyProperty(str, enum.Enum):
    FIRST = 'first'
    SECOND = 'second'


EntityWithStringEnumProperty.update_forward_refs()
