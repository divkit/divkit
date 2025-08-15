# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithComplexPropertyWithDefaultValue(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_complex_property_with_default_value',
        property: typing.Optional[EntityWithComplexPropertyWithDefaultValueProperty] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            property=property,
            **kwargs,
        )

    type: str = Field(default='entity_with_complex_property_with_default_value')
    property: typing.Optional[EntityWithComplexPropertyWithDefaultValueProperty] = Field(
    )


class EntityWithComplexPropertyWithDefaultValueProperty(BaseDiv):

    def __init__(
        self, *,
        value: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            value=value,
            **kwargs,
        )

    value: typing.Union[Expr, str] = Field(
    )


EntityWithComplexPropertyWithDefaultValueProperty.update_forward_refs()


EntityWithComplexPropertyWithDefaultValue.update_forward_refs()
