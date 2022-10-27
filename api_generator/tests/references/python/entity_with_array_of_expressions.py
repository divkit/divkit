# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithArrayOfExpressions(BaseDiv):

    def __init__(
        self, *,
        items: typing.List[str],
        type: str = 'entity_with_array_of_expressions',
    ):
        super().__init__(
            type=type,
            items=items,
        )

    type: str = Field(default='entity_with_array_of_expressions')
    items: typing.List[str] = Field(
        min_items=1
    )


EntityWithArrayOfExpressions.update_forward_refs()
