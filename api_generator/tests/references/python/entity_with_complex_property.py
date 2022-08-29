# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithComplexProperty(BaseDiv):

    def __init__(
        self, *,
        property: EntityWithComplexPropertyProperty,
        type: str = 'entity_with_complex_property',
    ):
        super().__init__(
            type=type,
            property=property,
        )

    type: str = Field(default='entity_with_complex_property')
    property: EntityWithComplexPropertyProperty = Field()


class EntityWithComplexPropertyProperty(BaseDiv):

    def __init__(
        self, *,
        value: str,
    ):
        super().__init__(
            value=value,
        )

    value: str = Field(format="uri")


EntityWithComplexPropertyProperty.update_forward_refs()


EntityWithComplexProperty.update_forward_refs()
