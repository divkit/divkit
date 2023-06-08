# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


# Entity with simple properties.
class EntityWithSimpleProperties(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_simple_properties',
        boolean: typing.Optional[typing.Union[Expr, bool]] = None,
        boolean_int: typing.Optional[typing.Union[Expr, bool]] = None,
        color: typing.Optional[typing.Union[Expr, str]] = None,
        double: typing.Optional[typing.Union[Expr, float]] = None,
        id: typing.Optional[typing.Union[Expr, int]] = None,
        integer: typing.Optional[typing.Union[Expr, int]] = None,
        positive_integer: typing.Optional[typing.Union[Expr, int]] = None,
        string: typing.Optional[typing.Union[Expr, str]] = None,
        url: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            boolean=boolean,
            boolean_int=boolean_int,
            color=color,
            double=double,
            id=id,
            integer=integer,
            positive_integer=positive_integer,
            string=string,
            url=url,
            **kwargs,
        )

    type: str = Field(default='entity_with_simple_properties')
    boolean: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Boolean property."
    )
    boolean_int: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Boolean value in numeric format. @deprecated"
    )
    color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Color."
    )
    double: typing.Optional[typing.Union[Expr, float]] = Field(
        description="Floating point number."
    )
    id: typing.Optional[typing.Union[Expr, int]] = Field(
        description="ID. Can\'t contain expressions."
    )
    integer: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Integer."
    )
    positive_integer: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Positive integer."
    )
    string: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description="String."
    )
    url: typing.Optional[typing.Union[Expr, str]] = Field(
        format="uri"
    )


EntityWithSimpleProperties.update_forward_refs()
