# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithComplexProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_complex_property',
        property: typing.Optional[EntityWithComplexPropertyProperty] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            property=property,
            **kwargs,
        )

    type: str = Field(default='entity_with_complex_property')
    property: EntityWithComplexPropertyProperty = Field(
    )


class EntityWithComplexPropertyProperty(BaseDiv):

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
        format="uri"
    )


EntityWithComplexPropertyProperty.update_forward_refs()


EntityWithComplexProperty.update_forward_refs()
