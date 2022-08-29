# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithoutProperties(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_without_properties',
    ):
        super().__init__(
            type=type,
        )

    type: str = Field(default='entity_without_properties')


EntityWithoutProperties.update_forward_refs()
