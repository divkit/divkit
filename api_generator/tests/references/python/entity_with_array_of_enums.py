# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithArrayOfEnums(BaseDiv):

    def __init__(
        self, *,
        items: typing.List[EntityWithArrayOfEnumsItem],
        type: str = 'entity_with_array_of_enums',
    ):
        super().__init__(
            type=type,
            items=items,
        )

    type: str = Field(default='entity_with_array_of_enums')
    items: typing.List[EntityWithArrayOfEnumsItem] = Field(
        min_items=1
    )


class EntityWithArrayOfEnumsItem(str, enum.Enum):
    FIRST = 'first'
    SECOND = 'second'


EntityWithArrayOfEnums.update_forward_refs()
