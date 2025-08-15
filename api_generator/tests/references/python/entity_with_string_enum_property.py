# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithStringEnumProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_string_enum_property',
        property: typing.Optional[typing.Union[Expr, EntityWithStringEnumPropertyProperty]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            property=property,
            **kwargs,
        )

    type: str = Field(default='entity_with_string_enum_property')
    property: typing.Union[Expr, EntityWithStringEnumPropertyProperty] = Field(
    )


class EntityWithStringEnumPropertyProperty(str, enum.Enum):
    FIRST = 'first'
    SECOND = 'second'


EntityWithStringEnumProperty.update_forward_refs()
