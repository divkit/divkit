# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithOptionalProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_optional_property',
        property: typing.Optional[str] = None,
    ):
        super().__init__(
            type=type,
            property=property,
        )

    type: str = Field(default='entity_with_optional_property')
    property: typing.Optional[str] = Field(min_length=1)


EntityWithOptionalProperty.update_forward_refs()
