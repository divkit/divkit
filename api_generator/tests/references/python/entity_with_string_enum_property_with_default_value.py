# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithStringEnumPropertyWithDefaultValue(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_string_enum_property_with_default_value',
        value: typing.Optional[typing.Union[Expr, EntityWithStringEnumPropertyWithDefaultValueValue]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            value=value,
            **kwargs,
        )

    type: str = Field(default='entity_with_string_enum_property_with_default_value')
    value: typing.Optional[typing.Union[Expr, EntityWithStringEnumPropertyWithDefaultValueValue]] = Field(
    )


class EntityWithStringEnumPropertyWithDefaultValueValue(str, enum.Enum):
    FIRST = 'first'
    SECOND = 'second'
    THIRD = 'third'


EntityWithStringEnumPropertyWithDefaultValue.update_forward_refs()
