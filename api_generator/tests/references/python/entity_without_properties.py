# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithoutProperties(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_without_properties',
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            **kwargs,
        )

    type: str = Field(default='entity_without_properties')


EntityWithoutProperties.update_forward_refs()
