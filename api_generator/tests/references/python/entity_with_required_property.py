# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithRequiredProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_required_property',
        property: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            property=property,
            **kwargs,
        )

    type: str = Field(default='entity_with_required_property')
    property: typing.Union[Expr, str] = Field(
        min_length=1
    )


EntityWithRequiredProperty.update_forward_refs()
