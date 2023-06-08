# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithArrayWithTransform(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_array_with_transform',
        array: typing.Optional[typing.Sequence[typing.Union[Expr, str]]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            array=array,
            **kwargs,
        )

    type: str = Field(default='entity_with_array_with_transform')
    array: typing.Sequence[typing.Union[Expr, str]] = Field(
        min_items=1
    )


EntityWithArrayWithTransform.update_forward_refs()
