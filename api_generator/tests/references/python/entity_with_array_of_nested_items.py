# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing

from . import entity


class EntityWithArrayOfNestedItems(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_array_of_nested_items',
        items: typing.Optional[typing.Sequence[EntityWithArrayOfNestedItemsItem]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            items=items,
            **kwargs,
        )

    type: str = Field(default='entity_with_array_of_nested_items')
    items: typing.Sequence[EntityWithArrayOfNestedItemsItem] = Field(
        min_items=1
    )


class EntityWithArrayOfNestedItemsItem(BaseDiv):

    def __init__(
        self, *,
        entity: typing.Optional[entity.Entity] = None,
        property: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            entity=entity,
            property=property,
            **kwargs,
        )

    entity: entity.Entity = Field(
    )
    property: typing.Union[Expr, str] = Field(
        min_length=1
    )


EntityWithArrayOfNestedItemsItem.update_forward_refs()


EntityWithArrayOfNestedItems.update_forward_refs()
