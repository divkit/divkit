# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithArrayWithTransform(BaseDiv):

    def __init__(
        self, *,
        array: typing.List[str],
        type: str = 'entity_with_array_with_transform',
    ):
        super().__init__(
            type=type,
            array=array,
        )

    type: str = Field(default='entity_with_array_with_transform')
    array: typing.List[str] = Field(
        min_items=1
    )


EntityWithArrayWithTransform.update_forward_refs()
