# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithRequiredProperty(BaseDiv):

    def __init__(
        self, *,
        property: str,
        type: str = 'entity_with_required_property',
    ):
        super().__init__(
            type=type,
            property=property,
        )

    type: str = Field(default='entity_with_required_property')
    property: str = Field(
        min_length=1
    )


EntityWithRequiredProperty.update_forward_refs()
