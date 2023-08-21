# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithArrayOfEnums(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_array_of_enums',
        items: typing.Optional[typing.Sequence[typing.Union[Expr, EntityWithArrayOfEnumsItem]]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            items=items,
            **kwargs,
        )

    type: str = Field(default='entity_with_array_of_enums')
    items: typing.Sequence[typing.Union[Expr, EntityWithArrayOfEnumsItem]] = Field(
        min_items=1
    )


class EntityWithArrayOfEnumsItem(str, enum.Enum):
    FIRST = 'first'
    SECOND = 'second'


EntityWithArrayOfEnums.update_forward_refs()
