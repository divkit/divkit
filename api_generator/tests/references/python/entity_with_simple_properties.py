# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Entity with simple properties.
class EntityWithSimpleProperties(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_simple_properties',
        boolean: typing.Optional[bool] = None,
        boolean_int: typing.Optional[bool] = None,
        color: typing.Optional[str] = None,
        double: typing.Optional[float] = None,
        id: typing.Optional[int] = None,
        integer: typing.Optional[int] = None,
        positive_integer: typing.Optional[int] = None,
        string: typing.Optional[str] = None,
        url: typing.Optional[str] = None,
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
        )

    type: str = Field(default='entity_with_simple_properties')
    boolean: typing.Optional[bool] = Field(
        description="Boolean property."
    )
    boolean_int: typing.Optional[bool] = Field(
        description="Boolean value in numeric format. @deprecated"
    )
    color: typing.Optional[str] = Field(
        format="color", 
        description="Color."
    )
    double: typing.Optional[float] = Field(
        description="Floating point number."
    )
    id: typing.Optional[int] = Field(
        description="ID. Can\'t contain expressions."
    )
    integer: typing.Optional[int] = Field(
        description="Integer."
    )
    positive_integer: typing.Optional[int] = Field(
        description="Positive integer."
    )
    string: typing.Optional[str] = Field(
        min_length=1, 
        description="String."
    )
    url: typing.Optional[str] = Field(
        format="uri"
    )


EntityWithSimpleProperties.update_forward_refs()
