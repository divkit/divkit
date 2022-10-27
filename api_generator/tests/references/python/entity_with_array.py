# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import entity


class EntityWithArray(BaseDiv):

    def __init__(
        self, *,
        array: typing.List[entity.Entity],
        type: str = 'entity_with_array',
    ):
        super().__init__(
            type=type,
            array=array,
        )

    type: str = Field(default='entity_with_array')
    array: typing.List[entity.Entity] = Field(
        min_items=1
    )


EntityWithArray.update_forward_refs()
