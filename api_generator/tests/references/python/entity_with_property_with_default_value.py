# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithPropertyWithDefaultValue(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_property_with_default_value',
        int: typing.Optional[typing.Union[Expr, int]] = None,
        nested: typing.Optional[EntityWithPropertyWithDefaultValueNested] = None,
        url: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            int=int,
            nested=nested,
            url=url,
            **kwargs,
        )

    type: str = Field(default='entity_with_property_with_default_value')
    int: typing.Optional[typing.Union[Expr, int]] = Field(
    )
    nested: typing.Optional[EntityWithPropertyWithDefaultValueNested] = Field(
        description=(
            "non_optional is used to suppress auto-generation of default "
            "value for object withall-optional fields."
        )
    )
    url: typing.Optional[typing.Union[Expr, str]] = Field(
        format="uri"
    )


# non_optional is used to suppress auto-generation of default value for object with
# all-optional fields.
class EntityWithPropertyWithDefaultValueNested(BaseDiv):

    def __init__(
        self, *,
        int: typing.Optional[typing.Union[Expr, int]] = None,
        non_optional: typing.Optional[typing.Union[Expr, str]] = None,
        url: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            int=int,
            non_optional=non_optional,
            url=url,
            **kwargs,
        )

    int: typing.Optional[typing.Union[Expr, int]] = Field(
    )
    non_optional: typing.Union[Expr, str] = Field(
    )
    url: typing.Optional[typing.Union[Expr, str]] = Field(
        format="uri"
    )


EntityWithPropertyWithDefaultValueNested.update_forward_refs()


EntityWithPropertyWithDefaultValue.update_forward_refs()
