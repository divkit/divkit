# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


class EntityWithOptionalComplexProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_optional_complex_property',
        property: typing.Optional[EntityWithOptionalComplexPropertyProperty] = None,
    ):
        super().__init__(
            type=type,
            property=property,
        )

    type: str = Field(default='entity_with_optional_complex_property')
    property: typing.Optional[EntityWithOptionalComplexPropertyProperty] = Field(
    )


class EntityWithOptionalComplexPropertyProperty(BaseDiv):

    def __init__(
        self, *,
        value: str,
    ):
        super().__init__(
            value=value,
        )

    value: str = Field(
        format="uri"
    )


EntityWithOptionalComplexPropertyProperty.update_forward_refs()


EntityWithOptionalComplexProperty.update_forward_refs()
