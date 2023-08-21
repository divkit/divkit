# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing

from . import entity


class EntityWithStrictArray(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_strict_array',
        array: typing.Optional[typing.Sequence[entity.Entity]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            array=array,
            **kwargs,
        )

    type: str = Field(default='entity_with_strict_array')
    array: typing.Sequence[entity.Entity] = Field(
        min_items=1
    )


EntityWithStrictArray.update_forward_refs()
