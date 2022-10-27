# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import entity


class EntityWithArrayOfNestedItems(BaseDiv):

    def __init__(
        self, *,
        items: typing.List[EntityWithArrayOfNestedItemsItem],
        type: str = 'entity_with_array_of_nested_items',
    ):
        super().__init__(
            type=type,
            items=items,
        )

    type: str = Field(default='entity_with_array_of_nested_items')
    items: typing.List[EntityWithArrayOfNestedItemsItem] = Field(
        min_items=1
    )


class EntityWithArrayOfNestedItemsItem(BaseDiv):

    def __init__(
        self, *,
        entity: entity.Entity,
        property: str,
    ):
        super().__init__(
            entity=entity,
            property=property,
        )

    entity: entity.Entity = Field(
    )
    property: str = Field(
        min_length=1
    )


EntityWithArrayOfNestedItemsItem.update_forward_refs()


EntityWithArrayOfNestedItems.update_forward_refs()
