# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithStringArrayProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_string_array_property',
        array: typing.Optional[typing.Sequence[typing.Union[Expr, str]]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            array=array,
            **kwargs,
        )

    type: str = Field(default='entity_with_string_array_property')
    array: typing.Sequence[typing.Union[Expr, str]] = Field(
        min_items=1
    )


EntityWithStringArrayProperty.update_forward_refs()
