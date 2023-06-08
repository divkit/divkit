# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Variable — HEX color as a string.
class ColorVariable(BaseDiv):

    def __init__(
        self, *,
        type: str = "color",
        name: typing.Optional[typing.Union[Expr, str]] = None,
        value: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
            **kwargs,
        )

    type: str = Field(default="color")
    name: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Variable name.",
    )
    value: typing.Union[Expr, str] = Field(
        format="color", 
        description="Value.",
    )


ColorVariable.update_forward_refs()
