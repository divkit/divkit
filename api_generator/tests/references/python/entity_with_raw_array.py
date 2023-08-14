# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithRawArray(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_raw_array',
        array: typing.Optional[typing.Sequence[typing.Any]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            array=array,
            **kwargs,
        )

    type: str = Field(default='entity_with_raw_array')
    array: typing.Sequence[typing.Any] = Field(
    )


EntityWithRawArray.update_forward_refs()
