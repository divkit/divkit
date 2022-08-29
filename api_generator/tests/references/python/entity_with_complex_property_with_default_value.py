# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithComplexPropertyWithDefaultValue(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_complex_property_with_default_value',
        property: typing.Optional[EntityWithComplexPropertyWithDefaultValueProperty] = None,
    ):
        super().__init__(
            type=type,
            property=property,
        )

    type: str = Field(default='entity_with_complex_property_with_default_value')
    property: typing.Optional[EntityWithComplexPropertyWithDefaultValueProperty] = Field()


class EntityWithComplexPropertyWithDefaultValueProperty(BaseDiv):

    def __init__(
        self, *,
        value: str,
    ):
        super().__init__(
            value=value,
        )

    value: str = Field()


EntityWithComplexPropertyWithDefaultValueProperty.update_forward_refs()


EntityWithComplexPropertyWithDefaultValue.update_forward_refs()
