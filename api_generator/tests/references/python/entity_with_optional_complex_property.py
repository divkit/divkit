# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithOptionalComplexProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_optional_complex_property',
        property: typing.Optional[EntityWithOptionalComplexPropertyComplexProperty] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            property=property,
            **kwargs,
        )

    type: str = Field(default='entity_with_optional_complex_property')
    property: typing.Optional[EntityWithOptionalComplexPropertyComplexProperty] = Field(
    )


class EntityWithOptionalComplexPropertyComplexProperty(BaseDiv):

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


EntityWithOptionalComplexPropertyComplexProperty.update_forward_refs()


EntityWithOptionalComplexProperty.update_forward_refs()
