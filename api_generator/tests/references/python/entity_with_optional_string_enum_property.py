# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithOptionalStringEnumProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_optional_string_enum_property',
        property: typing.Optional[typing.Union[Expr, EntityWithOptionalStringEnumPropertyProperty]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            property=property,
            **kwargs,
        )

    type: str = Field(default='entity_with_optional_string_enum_property')
    property: typing.Optional[typing.Union[Expr, EntityWithOptionalStringEnumPropertyProperty]] = Field(
    )


class EntityWithOptionalStringEnumPropertyProperty(str, enum.Enum):
    FIRST = 'first'
    SECOND = 'second'


EntityWithOptionalStringEnumProperty.update_forward_refs()
