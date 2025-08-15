# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithArrayOfExpressions(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_array_of_expressions',
        items: typing.Optional[typing.Sequence[typing.Union[Expr, str]]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            items=items,
            **kwargs,
        )

    type: str = Field(default='entity_with_array_of_expressions')
    items: typing.Sequence[typing.Union[Expr, str]] = Field(
        min_items=1
    )


EntityWithArrayOfExpressions.update_forward_refs()
